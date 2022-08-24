package ai.lib.ads

import android.app.Activity
import android.util.Log
import android.widget.*
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

const val TAG ="Admob_Native"
fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

    adView.apply {
        mediaView = findViewById(R.id.ad_media)
        // Set other ad assets.
        headlineView = findViewById(R.id.ad_headline)
        bodyView = findViewById(R.id.ad_body)
        callToActionView = findViewById(R.id.ad_call_to_action)
        iconView = findViewById(R.id.ad_app_icon)
        priceView = findViewById(R.id.ad_price)
        starRatingView = findViewById(R.id.ad_stars)
        storeView = findViewById(R.id.ad_store)
        advertiserView = findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (headlineView as TextView?)?.text = nativeAd.headline
        nativeAd.mediaContent?.let {
            mediaView?.setMediaContent(it)
//            mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        }
    }

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    nativeAd.body?.let { body ->
        adView.bodyView?.beVisible()
        (adView.bodyView as? TextView?)?.text = body
    } ?: run {
        adView.bodyView?.beInVisible()
    }
    nativeAd.callToAction?.let { callToAction ->
        adView.callToActionView?.beVisible()
        (adView.callToActionView as? Button?)?.text = callToAction
    } ?: run {
        adView.callToActionView?.beInVisible()
    }

    nativeAd.icon?.let {
        adView.iconView?.beVisible()
        (adView.iconView as? ImageView?)?.setImageDrawable(it.drawable)
    } ?: run {
        adView.iconView?.beInVisible()
    }

    nativeAd.price?.let { price ->
        adView.priceView?.beVisible()
        (adView.priceView as? TextView?)?.text = price
    } ?: run {
        adView.priceView?.beInVisible()
    }

    nativeAd.store?.let { store ->
        adView.storeView?.beVisible()
        (adView.storeView as? TextView?)?.text = store
    } ?: run {
        adView.storeView?.beGone()
    }


    nativeAd.starRating?.let { starRating ->
        adView.starRatingView?.beVisible()
        (adView.starRatingView as? RatingBar?)?.rating = starRating.toFloat()
    } ?: run {
        adView.starRatingView?.beGone()
    }

    nativeAd.advertiser?.let { advertiser ->
        adView.advertiserView?.beVisible()
        (adView.advertiserView as? TextView?)?.text = advertiser
    } ?: run {
        adView.advertiserView?.beGone()
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeAd.mediaContent?.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    vc?.apply {
        if (hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            }
        }
    }
}


fun Activity.loadNativeAdmob(
    adFrame: FrameLayout?,
    adNativeId: String,
    @LayoutRes layoutRes: Int,
    successListener: ((NativeAd) -> Unit)? = null,
    failedListener: ((error: String) -> Unit)? = null
) {
    val builder = AdLoader.Builder(this, adNativeId)
    builder.forNativeAd { nativeAd ->
        // OnUnifiedNativeAdLoadedListener implementation.
        // If this callback occurs after the activity is destroyed, you must call
        // destroy and return or you may get a memory leak.
        val activityDestroyed: Boolean = isDestroyed
        if (activityDestroyed || isFinishing || isChangingConfigurations) {
            nativeAd.destroy()
            return@forNativeAd
        }
        // You must call destroy on old ads when you are done with them,
        // otherwise you will have a memory leak.
//        currentNativeAd?.destroy()
//        currentNativeAd = nativeAd
        val adView = layoutInflater
            .inflate(layoutRes, null, false) as NativeAdView
        populateNativeAdView(nativeAd, adView)
        adFrame?.removeAllViews()
        adFrame?.addView(adView)
        successListener?.invoke(nativeAd)
        Log.e(TAG, "NativeAd loaded.")
    }

    val videoOptions = VideoOptions.Builder()
        .setStartMuted(true)
        .build()

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()

    builder.withNativeAdOptions(adOptions)

    val adLoader = builder.withAdListener(object : AdListener() {
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            val error =
                "${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}"
            Log.e(TAG, error)
            failedListener?.invoke(error)
        }
    }).build()
    adLoader.loadAd(AdRequest.Builder().build())
}