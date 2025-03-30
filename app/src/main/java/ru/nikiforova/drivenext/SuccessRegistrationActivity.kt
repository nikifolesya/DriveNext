package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class SuccessRegistrationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_registration)

        saveCurrentActivity()

        val nextButton: MaterialButton = findViewById(R.id.next_btn)
        nextButton.setOnClickListener {
             startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@SuccessRegistrationActivity::class.java.name)
            apply()
        }
    }
}
