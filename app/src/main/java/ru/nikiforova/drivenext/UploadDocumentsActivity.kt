package ru.nikiforova.drivenext

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UploadDocumentsActivity : BaseActivity() {

    private lateinit var licenseNumberEditText: TextInputEditText
    private lateinit var issueDateEditText: TextInputEditText
    private lateinit var uploadLicenseButton: ImageButton
    private lateinit var uploadPassportButton: ImageButton
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: ImageButton
    private lateinit var profileImageView: ImageView
    private lateinit var buttonUpload: ImageButton

    private val requestImagePick = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_documents)

        saveCurrentActivity()

        licenseNumberEditText = findViewById(R.id.licenceEditText)
        issueDateEditText = findViewById(R.id.dateEditText)
        uploadLicenseButton = findViewById(R.id.upload_photo_btn1)
        uploadPassportButton = findViewById(R.id.upload_photo_btn2)
        nextButton = findViewById(R.id.next_btn)
        backButton = findViewById(R.id.back_btn)
        profileImageView = findViewById(R.id.imageView)
        buttonUpload = findViewById(R.id.buttonUpload)

        buttonUpload.setOnClickListener {
            openImagePicker()
        }

        uploadLicenseButton.setOnClickListener {
            openImagePicker()
        }

        uploadPassportButton.setOnClickListener {
            openImagePicker()
        }

        // Открытие календаря при нажатии на поле даты
        issueDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        nextButton.setOnClickListener {
            val licenseNumber = licenseNumberEditText.text.toString()
            val issueDate = issueDateEditText.text.toString()
            if (licenseNumber.isNotEmpty() && isValidIssueDate(issueDate)) {
                startActivity(Intent(this, SuccessRegistrationActivity::class.java))
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openImagePicker() {
        requestImagePick.launch("image/*")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                issueDateEditText.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun isValidIssueDate(issueDate: String): Boolean {
        val regex = Regex("""\d{2}/\d{2}/\d{4}""")
        return regex.matches(issueDate)
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@UploadDocumentsActivity::class.java.name)
            apply()
        }
    }
}
