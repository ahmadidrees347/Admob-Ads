## Using Admob Ads Library in your Android application

### Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### Add this in your build.gradle

```groovy
implementation 'com.github.ahmadidrees347:Admob-Ads:version'
```

Do not forget to add internet permission in manifest if already not present

```xml

<uses-permission android:name="android.permission.INTERNET" />
```

### For AdMob Ads, initialize it in onCreate() Method of Application/Activity class :

```kotlin
MobileAds.initialize(this)
```

### For AppOpen ads, create an object, passing context & adUnitId in it;

```kotlin
val appOpen = AppOpen(application, Constants.AppOpenId)
 ```

setAdShownStatus(true/false), if you want to show ad every time then pass true.

```kotlin
appOpen.setAdShownStatus(false)
```

you can explicitly can call load or show function of appOpen ad:
```kotlin
appOpen.fetchAd { booleanStatus -> }
appOpen.showAdIfAvailable { booleanStatus -> }
```

### For Interstitial:

For load an ad, call function, you get the result:

```kotlin
loadInterstitial(interAdId) { booleanStatus -> }
```

To show an ad, call function, you get the result:

```kotlin
showInterstitial(interAdId) { booleanStatus -> }
```

If you wanted to load an ad and show after loading, then call this function, you can pass any dialog
which you want to show before showing an ad.

```kotlin
showInterOnDemand(interAdId, ProgressDialog(this)) { booleanStatus -> }
```

If you wanted to load every time, auto when user user dismiss an ad:

```kotlin
InterAdmobClass.adLoadAuto = true
```

You can set failed attempt for inter ad:

```kotlin
InterAdmobClass.adFailedAttempts = 3
```

### For Banner ad,
Just pass id and frameLayout:

```kotlin
showAdmobBanner(bannerAdId, findViewById(R.id.bannerLayout))
```
If you wanted to check the banner is loaded or failed then call:
```kotlin
showAdmobBanner(bannerAdId, findViewById(R.id.bannerLayout),{
                       //success                                                         
            },{ error ->
                //failed
            })
```

### For Native ad,
Just pass id, frameLayout and layout which you want to inflate:

```kotlin
loadNativeAdmob(
    findViewById(R.id.nativeLayout),
    nativeAdId,
    R.layout.custom_ad_large)
```
If you wanted to check the native is loaded or failed then call:
```kotlin
loadNativeAdmob(
    findViewById(R.id.nativeLayout),
    nativeAdId,
    R.layout.custom_ad_large,
    {
        //success
    }, { error ->
        //failed
    })
```

### License

```
   Copyright (C) 2022 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```