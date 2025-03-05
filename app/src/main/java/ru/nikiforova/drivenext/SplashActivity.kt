package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        saveCurrentActivity()

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstLaunch() || !isAccessTokenValid()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
            } else {
                startActivity(Intent(this, LoginRegisterActivity::class.java))
            }
            finish()
        }, 2000)
    }

    private fun isFirstLaunch(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_first_launch", true)
    }

    private fun isAccessTokenValid(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("access_token", null)
        return !accessToken.isNullOrEmpty()
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@SplashActivity::class.java.name)
            apply()
        }
    }
}
