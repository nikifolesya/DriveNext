package ru.nikiforova.drivenext

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdditionalInfoActivity : BaseActivity() {

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

        // Получаем email и пароль из предыдущей активности
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        // Обработка выбора даты рождения
        birthDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        nextButton.setOnClickListener {
            val lastName = lastNameEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val middleName = middleNameEditText.text.toString()
            val birthDate = birthDateEditText.text.toString()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) findViewById<RadioButton>(selectedGenderId).text.toString() else ""

            if (lastName.isNotEmpty() && firstName.isNotEmpty() && middleName.isNotEmpty() &&
                isValidBirthDate(birthDate) && gender.isNotEmpty()) {

                val intent = Intent(this, UploadDocumentsActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                intent.putExtra("lastName", lastName)
                intent.putExtra("firstName", firstName)
                intent.putExtra("middleName", middleName)
                intent.putExtra("birthDate", birthDate)
                intent.putExtra("gender", gender)
                startActivity(intent)
            } else {
                showToast("Пожалуйста, заполните все обязательные поля.")
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                birthDateEditText.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun isValidBirthDate(birthDate: String): Boolean {
        val regex = Regex("""\d{2}/\d{2}/\d{4}""")
        return regex.matches(birthDate)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@AdditionalInfoActivity::class.java.name)
            apply()
        }
    }
}