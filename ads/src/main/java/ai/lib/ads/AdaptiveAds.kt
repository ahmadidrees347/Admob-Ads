package ai.lib.ads

import android.content.Context
import com.google.android.gms.ads.AdSize

class AdaptiveAds(private val context: Context) {
    val adSize: AdSize
        get() {
            val outMetrics = context.resources.displayMetrics
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context,
                adWidth
            )
        }
}