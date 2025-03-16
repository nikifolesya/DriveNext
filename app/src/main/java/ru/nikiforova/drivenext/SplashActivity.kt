package ru.nikiforova.drivenext

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.*

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        saveCurrentActivity()

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val language = sharedPreferences.getString("app_language", null)

            if (language == null) {
                // Если язык не выбран, переходим на экран выбора языка
                startActivity(Intent(this, LanguageActivity::class.java))
            } else {
                // Применяем язык
                applyLanguage(language)

                // Проверяем, первый ли запуск или есть ли токен доступа
                if (isFirstLaunch() || !isAccessTokenValid()) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
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

    private fun applyLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        createConfigurationContext(config)

        // Перезапускаем SplashActivity, чтобы язык применился во всем приложении
        val refreshIntent = Intent(this, SplashActivity::class.java)
        startActivity(refreshIntent)
        finish()
    }
}
