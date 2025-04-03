package ru.nikiforova.drivenext

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Outline
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikiforova.drivenext.data.AppDatabase
import ru.nikiforova.drivenext.data.User
import java.util.Date
import ru.nikiforova.drivenext.utils.FileUtils


class UploadDocumentsActivity : BaseActivity() {

    private lateinit var licenseNumberEditText: TextInputEditText
    private lateinit var issueDateEditText: TextInputEditText
    private lateinit var uploadLicenseButton: ImageButton
    private lateinit var uploadPassportButton: ImageButton
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: ImageButton
    private lateinit var profileImageView: ImageView
    private lateinit var buttonUpload: ImageButton
    private lateinit var uploadLicenseText: TextView
    private lateinit var uploadPassportText: TextView

    private var currentUploadType: UploadType? = null
    private var profilePictureUri: Uri? = null
    private var savedProfileImagePath: String? = null

    private val requestImagePick = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            when (currentUploadType) {
                UploadType.LICENSE -> uploadLicenseText.text = getFileName(it)
                UploadType.PASSPORT -> uploadPassportText.text = getFileName(it)
                UploadType.PROFILE -> {
                    profilePictureUri = it

                    // Сохраняем изображение с использованием FileUtils
                    val email = intent.getStringExtra("email") ?: ""
                    savedProfileImagePath = FileUtils.saveProfileImageToInternalStorage(this, email, it)

                    savedProfileImagePath?.let { path ->
                        // Загружаем сохраненное изображение
                        Glide.with(this)
                            .load(path)
                            .into(profileImageView)
                    }
                }

                else -> {}
            }
        } ?: run {
            Toast.makeText(this, "Файл не выбран", Toast.LENGTH_SHORT).show()
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
        uploadLicenseText = findViewById(R.id.upload_photo_txt1)
        uploadPassportText = findViewById(R.id.upload_photo_txt2)

        makeImageViewRound()

        buttonUpload.setOnClickListener {
            currentUploadType = UploadType.PROFILE
            openImagePicker()
        }

        uploadLicenseButton.setOnClickListener {
            currentUploadType = UploadType.LICENSE
            openImagePicker()
        }

        uploadPassportButton.setOnClickListener {
            currentUploadType = UploadType.PASSPORT
            openImagePicker()
        }

        issueDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        nextButton.setOnClickListener {
            val licenseNumber = licenseNumberEditText.text.toString()
            val issueDate = issueDateEditText.text.toString()

            if (!isValidLicenseNumber(licenseNumber)) {
                Toast.makeText(this, "Номер водительского удостоверения должен состоять из 10 цифр.", Toast.LENGTH_SHORT).show()
            } else if (!isValidIssueDate(issueDate)) {
                Toast.makeText(this, "Введите корректную дату выдачи.", Toast.LENGTH_SHORT).show()
            } else {
                saveUserDataToDatabase()
                startActivity(Intent(this, SuccessRegistrationActivity::class.java))
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun makeImageViewRound() {
        profileImageView.clipToOutline = true
        profileImageView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val radius = view.width.coerceAtMost(view.height) / 2f
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
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

    private fun isValidLicenseNumber(licenseNumber: String): Boolean {
        val regex = Regex("""\d{10}""")
        return regex.matches(licenseNumber)
    }

    private fun isValidIssueDate(issueDate: String): Boolean {
        val regex = Regex("""\d{2}/\d{2}/\d{4}""")
        return regex.matches(issueDate)
    }

    private fun saveUserDataToDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val email = intent.getStringExtra("email") ?: ""
            val password = intent.getStringExtra("password") ?: ""
            val lastName = intent.getStringExtra("lastName") ?: ""
            val firstName = intent.getStringExtra("firstName") ?: ""
            val middleName = intent.getStringExtra("middleName") ?: ""
            val birthDate = intent.getStringExtra("birthDate") ?: ""
            val gender = intent.getStringExtra("gender") ?: ""
            val licenseNumber = licenseNumberEditText.text.toString()
            val issueDate = issueDateEditText.text.toString()
            val licenseFile = uploadLicenseText.text.toString()
            val passportFile = uploadPassportText.text.toString()
            val profilePicturePath = savedProfileImagePath ?: ""

            // Сохраняем путь изображения в базу данных
            val user = User(
                email = email,
                password = password,
                name = firstName,
                surname = lastName,
                patronymic = middleName,
                birthDate = birthDate,
                gender = gender,
                licenseNumber = licenseNumber,
                issueDate = issueDate,
                licenseFile = licenseFile,
                passportFile = passportFile,
                profilePicture = profilePicturePath, // Сохраняем путь
                registrationDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            )

            AppDatabase.getDatabase(this@UploadDocumentsActivity).userDao().insertUser(user)
        }
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@UploadDocumentsActivity::class.java.name)
            apply()
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Выбранное изображение"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }

    enum class UploadType {
        LICENSE, PASSPORT, PROFILE
    }
}