package ru.nikiforova.drivenext

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            startActivity(Intent(this@BaseActivity, NoInternetActivity::class.java))
            finish()
        }

        override fun onAvailable(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                if (NetworkUtil.isInternetAvailable()) {
                    returnToPreviousActivity()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveCurrentActivity()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onResume() {
        super.onResume()
        val networkRequest = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@BaseActivity::class.java.name)
            apply()
        }
    }

    private fun returnToPreviousActivity() {
        val previousActivity = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getString("previous_activity", WelcomeActivity::class.java.name)

        val intent = Intent().setClassName(this, previousActivity!!)
        startActivity(intent)
        finish()
    }

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.createConfigurationContext(config)
    }

}
