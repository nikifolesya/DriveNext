package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import at.favre.lib.crypto.bcrypt.BCrypt


class RegisterActivity : BaseActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText1: TextInputEditText
    private lateinit var passwordEditText2: TextInputEditText
    private lateinit var termsCheckBox: MaterialCheckBox
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        saveCurrentActivity()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText1 = findViewById(R.id.passwordEditText1)
        passwordEditText2 = findViewById(R.id.passwordEditText2)
        termsCheckBox = findViewById(R.id.privacyCheckBox)
        nextButton = findViewById(R.id.next_btn)
        backButton = findViewById(R.id.back_btn)

        nextButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password1 = passwordEditText1.text.toString()
            val password2 = passwordEditText2.text.toString()

            when {
                email.isEmpty() -> showToast("Введите адрес электронной почты.")
                password1.isEmpty() || password2.isEmpty() -> showToast("Введите пароль.")
                !isValidEmail(email) -> showToast("Введите корректный адрес электронной почты.")
                !isValidPassword(password1) -> showToast("Пароль должен содержать минимум 6 символов, цифры и буквы.")
                password1 != password2 -> showToast("Пароли не совпадают.")
                !termsCheckBox.isChecked -> showToast("Необходимо согласиться с условиями.")
                else -> {
                    // Хэширование пароля
                    val hashedPassword = BCrypt.withDefaults().hashToString(12, password1.toCharArray())
                    // Переход к следующему экрану с передачей email и пароля
                    val intent = Intent(this, AdditionalInfoActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("password", hashedPassword)
                    startActivity(intent)
                }
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@RegisterActivity::class.java.name)
            apply()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}