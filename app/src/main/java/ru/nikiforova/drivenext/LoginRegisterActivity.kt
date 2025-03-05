package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class LoginRegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        saveCurrentActivity()

        val loginButton: MaterialButton = findViewById(R.id.log_in_btn)
        val registerButton: MaterialButton = findViewById(R.id.sign_up__btn)

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@LoginRegisterActivity::class.java.name)
            apply()
        }
    }
}
