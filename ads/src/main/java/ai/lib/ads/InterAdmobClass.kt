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

    private var isAdLoaded = false
    private var admobInterstitialAd: InterstitialAd? = null

    companion object {
        const val TAG = "Admob_Inter"

        @Volatile
        private var instance: InterAdmobClass? = null

        @JvmStatic
        var waitingTimeForAd = 8000L

        @JvmStatic
        var isInterstitialShown = false

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: InterAdmobClass().also { instance = it }
            }
    }

    private fun isAdLoaded() = (isAdLoaded && (admobInterstitialAd != null))


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
                    admobInterstitialAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    listener.invoke(false)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    admobInterstitialAd = ad
                    isAdLoaded = true
                    isInterstitialShown = false
                    Log.e(TAG, "Loaded")
                    listener.invoke(true)
                }

            })
    }

    fun showInterstitialAd(
        activity: Activity,
        listener: (Boolean) -> Unit,
        listenerImp: (() -> Unit)? = null
    ) {
        admobInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.e(TAG, "onAdDismissedFullScreenContent")
                    admobInterstitialAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    listener.invoke(true)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Log.e(TAG, "onAdDismissedFullScreenContent")
                    isAdLoaded = false
                    isInterstitialShown = false
                    super.onAdFailedToShowFullScreenContent(p0)
                    listener.invoke(false)
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
            admobInterstitialAd?.show(activity)
        } else {
            listener.invoke(false)
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
        if (activity.isNetworkAvailable() &&
            activity.verifyInstallerId()
        ) {

            afterDelay(waitingTimeForAd) {
                if (dialog?.isShowing == true)
                    dialog.dismiss()
                isTimeUp = true
                if (!isAdShow)
                    listener.invoke()
            }
            Log.e(TAG, "isAdLoaded ${isAdLoaded()}")
            if (isAdLoaded()) {
                Log.e(TAG, "Already Loaded")
                if (dialog?.isShowing == true)
                    dialog.dismiss()
                if (!isTimeUp)
                    showInterstitialAd(activity, {
                        isAdShow = true
                        listener.invoke()
                    }, {
                        isAdShow = true
                    })
            } else {
                dialog?.show()
                loadInterstitialAd(activity, adInterId) {
                    Log.e(TAG, "Load Ad")
                    if (dialog?.isShowing == true)
                        dialog.dismiss()
                    if (!isTimeUp)
                        showInterstitialAd(activity, {
                            Log.e(TAG, "isAdShown $it")
                            listener.invoke()
                        }, {
                            isAdShow = true
                        })
                }
            }
        } else {
            listener.invoke()
        }
    }
}