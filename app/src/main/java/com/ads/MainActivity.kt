package com.ads

import ai.lib.ads.*
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    private var appOpen: OpenApp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        appOpen = OpenApp(application, Constants.AppOpenId)
        appOpen?.setAdShownStatus(false)

        findViewById<Button>(R.id.btnLoadInter).setOnClickListener {
            loadInterstitial(Constants.InterId) {}
        }

        findViewById<Button>(R.id.btnShowInter).setOnClickListener {
            showInterstitial { }
        }

        findViewById<Button>(R.id.btnDemandInter).setOnClickListener {
            showInterOnDemand(Constants.InterId, ProgressDialog(this)) {}
        }

        findViewById<Button>(R.id.btnLoadAppOpen).setOnClickListener {
            appOpen?.fetchAd()
        }

        findViewById<Button>(R.id.btnShowAppOpen).setOnClickListener {
            appOpen?.showAdIfAvailable {}
        }

        findViewById<Button>(R.id.btnLoadNative).setOnClickListener {
            loadNativeAdmob(
                findViewById(R.id.nativeLayout),
                Constants.NativeId,
                ai.lib.ads.R.layout.custom_ad_large,
                {

                }, {

                })
        }

        findViewById<Button>(R.id.btnLoadBanner).setOnClickListener {
            showAdmobBanner(Constants.BannerId, findViewById(R.id.bannerLayout))
        }

    }
}