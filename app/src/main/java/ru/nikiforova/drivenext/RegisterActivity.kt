package ru.nikiforova.drivenext

import android.content.Intent
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

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
            if (!isValidEmail(email)) {
                showToast("Введите корректный адрес электронной почты.")
            } else if (password1 != password2) {
                showToast("Пароли не совпадают.")
            } else if (!termsCheckBox.isChecked) {
                showToast("Необходимо согласиться с условиями обслуживания и политикой конфиденциальности.")
            } else {
                startActivity(Intent(this, AdditionalInfoActivity::class.java))
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        // Пример проверки интернет-соединения
        if (!isInternetAvailable()) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
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