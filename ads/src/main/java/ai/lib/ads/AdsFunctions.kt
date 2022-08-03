package ai.lib.ads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.verifyInstallerId(): Boolean {
    if (BuildConfig.DEBUG)
        return true
    val validInstallers = listOf("com.android.vending", "com.google.android.feedback")
    return getInstallerPackageName() != null && validInstallers.contains(getInstallerPackageName())
}

@Suppress("DEPRECATION")
fun Context.getInstallerPackageName(): String? {
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return packageManager.getInstallSourceInfo(packageName).installingPackageName
        return packageManager.getInstallerPackageName(packageName)
    }
    return null
}

fun Activity.loadInterstitial(adInterId: String, listener: (Boolean) -> Unit) {
    InterAdmobClass.getInstance()
        .loadInterstitialAd(this, adInterId, listener)
}

fun Activity.showInterstitial(adInterId: String, listener: (Boolean) -> Unit) {
    InterAdmobClass.getInstance()
        .showInterstitialAd(this, adInterId, listener, null)
}

fun Activity.showInterOnDemand(
    adInterId: String,
    dialog: Dialog? = null,
    listener: () -> Unit
) {
    InterAdmobClass.getInstance()
        .loadAndShowInter(this, adInterId, dialog) {
            listener.invoke()
        }
}

fun afterDelay(delayInTime: Long, listener: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        listener.invoke()
    }, delayInTime)
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beInVisible() {
    visibility = View.INVISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}