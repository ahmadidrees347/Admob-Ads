package ai.lib.ads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


open class InterAdmobClass {

    companion object {
        const val TAG = "Admob_Inter"

        @Volatile
        private var instance: InterAdmobClass? = null

        @JvmStatic
        var waitingTimeForAd = 8000L

        @JvmStatic
        var isInterstitialShown = false

        @JvmStatic
        var adFailedAttempts = 3

        @JvmStatic
        var adLoadAuto = false

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: InterAdmobClass().also { instance = it }
            }
    }

    private var admobInterAd: InterstitialAd? = null
    private var adFailedCounter = 0
    private var isAdLoaded = false

    private fun isAdLoaded() = (isAdLoaded && (admobInterAd != null))


    fun loadInterstitialAd(
        context: Context,
        adInterId: String,
        listener: (Boolean) -> Unit
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adInterId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(ad: LoadAdError) {
                    Log.e(TAG, "onAdFailedToLoad - $ad")
                    admobInterAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    adFailedCounter++
                    if (adFailedCounter < adFailedAttempts) {
                        loadInterstitialAd(context, adInterId) {}
                    }
                    listener.invoke(false)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    admobInterAd = ad
                    isAdLoaded = true
                    isInterstitialShown = false
                    Log.e(TAG, "Loaded")
                    listener.invoke(true)
                }

            })
    }

    fun showInterstitialAd(
        activity: Activity,
        adInterId: String,
        listener: (Boolean) -> Unit,
        listenerImp: (() -> Unit)? = null
    ) {
        admobInterAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.e(TAG, "onAdDismissedFullScreenContent")
                    admobInterAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    if (adLoadAuto) {
                        loadInterstitialAd(activity, adInterId) {}
                    }
                    activity.runOnUiThread { listener.invoke(true) }
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Log.e(TAG, "onAdDismissedFullScreenContent")
                    isAdLoaded = false
                    isInterstitialShown = false
                    super.onAdFailedToShowFullScreenContent(p0)
                    activity.runOnUiThread { listener.invoke(false) }
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isInterstitialShown = true
                    listenerImp?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()

                    isInterstitialShown = true
                }
            }

        if (isAdLoaded) {
            admobInterAd?.show(activity)
        } else {
            activity.runOnUiThread { listener.invoke(false) }
        }
    }

    fun loadAndShowInter(
        activity: Activity,
        adInterId: String,
        dialog: Dialog? = null,
        listener: () -> Unit
    ) {
        var isTimeUp = false
        var isAdShow = false
        if (activity.isNetworkAvailable()) {
            afterDelay(waitingTimeForAd) {
                if (!activity.isDestroyed && !activity.isFinishing)
                    if (dialog?.isShowing == true) {
                        try {
                            dialog.dismiss()
                        } catch (e: IllegalArgumentException) {
                            // Do nothing.
                        } catch (e: Exception) {
                            // Do nothing.
                        }
                    }
                isTimeUp = true
                if (!isAdShow)
                    activity.runOnUiThread { listener.invoke() }
            }
            Log.e(TAG, "isAdLoaded ${isAdLoaded()}")
            if (isAdLoaded()) {
                Log.e(TAG, "Already Loaded")
                if (!activity.isDestroyed && !activity.isFinishing)
                    if (dialog?.isShowing == true) {
                        try {
                            dialog.dismiss()
                        } catch (e: IllegalArgumentException) {
                            // Do nothing.
                        } catch (e: Exception) {
                            // Do nothing.
                        }
                    }
                if (!isTimeUp)
                    showInterstitialAd(activity, adInterId, {
                        isAdShow = true
                        activity.runOnUiThread { listener.invoke() }
                    }, {
                        isAdShow = true
                    })
            } else {
                dialog?.show()
                loadInterstitialAd(activity, adInterId) {
                    Log.e(TAG, "Load Ad")
                    if (!activity.isDestroyed && !activity.isFinishing)
                        if (dialog?.isShowing == true) {
                            try {
                                dialog.dismiss()
                            } catch (e: IllegalArgumentException) {
                                // Do nothing.
                            } catch (e: Exception) {
                                // Do nothing.
                            }
                        }
                    if (!isTimeUp)
                        showInterstitialAd(activity, adInterId, {
                            Log.e(TAG, "isAdShown $it")
                            activity.runOnUiThread { listener.invoke() }
                        }, {
                            isAdShow = true
                        })
                }
            }
        } else {
            activity.runOnUiThread { listener.invoke() }
        }
    }
}