package ru.nikiforova.drivenext

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LanguageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val buttonRussian: MaterialButton = findViewById(R.id.button_russian)
        val buttonEnglish: MaterialButton = findViewById(R.id.button_english)

        buttonRussian.setOnClickListener {
            setLocale("ru")
        }

        buttonEnglish.setOnClickListener {
            setLocale("en")
        }
    }

    private fun setLocale(language: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("app_language", language)
            apply()
        }

        // Перезапускаем SplashActivity, чтобы применить язык
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}
