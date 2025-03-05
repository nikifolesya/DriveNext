package ru.nikiforova.drivenext

import android.content.Intent
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AdditionalInfoActivity : AppCompatActivity() {

    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var middleNameEditText: TextInputEditText
    private lateinit var birthDateEditText: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)

        saveCurrentActivity()

        lastNameEditText = findViewById(R.id.surnameEditText)
        firstNameEditText = findViewById(R.id.nameEditText)
        middleNameEditText = findViewById(R.id.patronymicEditText)
        birthDateEditText = findViewById(R.id.birthEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        nextButton = findViewById(R.id.next_btn)
        backButton = findViewById(R.id.back_btn)

        nextButton.setOnClickListener {
            val lastName = lastNameEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val birthDate = birthDateEditText.text.toString()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) findViewById<RadioButton>(selectedGenderId).text.toString() else ""

            if (lastName.isNotEmpty() && firstName.isNotEmpty() && isValidBirthDate(birthDate) && gender.isNotEmpty()) {
                startActivity(Intent(this, UploadDocumentsActivity::class.java))
            } else {
                showToast("Пожалуйста, заполните все обязательные поля.")
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

    private fun isValidBirthDate(birthDate: String): Boolean {
        val regex = Regex("""\d{2}/\d{2}/\d{4}""")
        return regex.matches(birthDate)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            putString("previous_activity", this@AdditionalInfoActivity::class.java.name)
            apply()
        }
    }
}