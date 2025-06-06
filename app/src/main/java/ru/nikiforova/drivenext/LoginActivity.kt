package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikiforova.drivenext.data.AppDatabase
import at.favre.lib.crypto.bcrypt.BCrypt

class LoginActivity : BaseActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var googleLoginButton: MaterialButton
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        saveCurrentActivity()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.log_in_btn)
        googleLoginButton = findViewById(R.id.login_google)
        registerButton = findViewById(R.id.sign_in_btn)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()


            when {
                email.isEmpty() -> showToast("Введите адрес электронной почты.")
                password.isEmpty() -> showToast("Введите пароль.")
                !isValidEmail(email) -> showToast("Введите корректный адрес электронной почты.")
                else -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = AppDatabase.getDatabase(this@LoginActivity).userDao().getUserByEmail(email)

                        runOnUiThread {
                            if (user != null) {
                                if (verifyPassword(password, user.password)) {
                                    saveLoginData(email, password)
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                } else {
                                    showToast("Неверный пароль.")
                                }
                            } else {
                                showToast("Пользователь с таким email не найден.")
                            }
                        }
                    }
                }
            }
        }

        googleLoginButton.setOnClickListener {
            // Логика авторизации через Google
            // Успех: сохранить токен, открыть пустой «главный экран»
            // Ошибка: отобразить сообщение об ошибке
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun verifyPassword(inputPassword: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(inputPassword.toCharArray(), hashedPassword).verified
    }

    private fun saveLoginData(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@LoginActivity::class.java.name)
            apply()
        }
    }
}
