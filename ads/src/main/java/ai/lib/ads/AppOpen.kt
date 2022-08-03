package ai.lib.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import org.jetbrains.annotations.NotNull



class AppOpen(private val appClass: Application, private val adId: String) :
    Application.ActivityLifecycleCallbacks, LifecycleObserver, LifecycleEventObserver {
   
    private val tag = "Admob_AppOpen"
    private var isShowingAd = false
    private var isAdShowAlways = false

    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var fullScreenContentCallback: FullScreenContentCallback? = null


    init {
        appClass.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (isAdShowAlways) {
            fetchAd {}
        }
    }

    fun setAdShownStatus(status: Boolean) {
        isAdShowAlways = status
    }

    /**
     * Request an ad
     */
    fun fetchAd(listener: (Boolean) -> Unit) {
        Log.e(tag, "fetchAd " + isAdAvailable())
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return
        }

        /*
          Called when an app open ad has failed to load.
          @param loadAdError the error.
         */
        // Handle the error.
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */

            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                listener.invoke(true)
                Log.e(tag, "loaded")
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                appOpenAd = null
                listener.invoke(false)
                Log.e(tag, "error $loadAdError")
            }
        }
        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
            appClass, adId, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }


    fun showAdIfAvailable(listener: (Boolean) -> Unit) {
        if (isAdAvailable()) {
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    if (isAdShowAlways) {
                        fetchAd {}
                    }
                    listener.invoke(true)
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }

            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let {
                appOpenAd?.show(it)
            } ?: kotlin.run {
                listener.invoke(false)
            }
        } else {
            if (isAdShowAlways) {
                fetchAd {}
            }
            listener.invoke(false)
        }
    }

    private fun isAdAvailable(): Boolean {
        return !isShowingAd && appOpenAd != null && !InterAdmobClass.isInterstitialShown
    }

    /**
     * Creates and returns ad request.
     */
    @NotNull
    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onStateChanged(p0: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            currentActivity?.let {
                if (isAdShowAlways) {
                    showAdIfAvailable {}
                }
            }
        }
    }
}