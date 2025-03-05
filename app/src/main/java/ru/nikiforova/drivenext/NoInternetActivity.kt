package ru.nikiforova.drivenext

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtil {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun isInternetAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        }
    }
}

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        val retryButton: MaterialButton = findViewById(R.id.btn_retry)
        retryButton.setOnClickListener {
            checkInternetAndReturn()
        }
    }

    private fun checkInternetAndReturn() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                if (NetworkUtil.isInternetAvailable()) {
                    val previousActivity = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        .getString("previous_activity", WelcomeActivity::class.java.name)
                    val intent = Intent().setClassName(this@NoInternetActivity, previousActivity!!)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Нет подключения к интернету. Проверьте подключение и повторите попытку.")
                }
            }
        } else {
            showToast("Нет подключения к интернету. Проверьте подключение и повторите попытку.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}