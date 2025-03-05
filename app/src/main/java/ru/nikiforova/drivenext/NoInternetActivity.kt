package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        val retryButton: MaterialButton = findViewById(R.id.btn_retry)
        retryButton.setOnClickListener {
            checkNetworkAndReturn()
        }
    }

    private fun checkNetworkAndReturn() {
        // Проверяем доступность сети
        if (NetworkUtil.isNetworkAvailable(this)) {
            // Если сеть доступна, проверяем доступность интернета
            CoroutineScope(Dispatchers.Main).launch {
                if (NetworkUtil.isInternetAvailable()) {
                    // Если интернет доступен, переходим на предыдущую активность
                    val previousActivity = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        .getString("previous_activity", WelcomeActivity::class.java.name)
                    val intent = Intent().setClassName(this@NoInternetActivity, previousActivity!!)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Интернет-соединение недоступно.")
                }
            }
        } else {
            showToast("Сеть недоступна.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
