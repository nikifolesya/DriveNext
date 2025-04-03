package ru.nikiforova.drivenext

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikiforova.drivenext.UploadDocumentsActivity.UploadType
import ru.nikiforova.drivenext.data.AppDatabase
import ru.nikiforova.drivenext.utils.FileUtils
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var fullNameTextView: TextView
    private lateinit var joinedDateTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var buttonUpload: ImageButton
    private lateinit var buttonLogOut: Button


    private var currentUploadType: UploadType? = null
    private var profilePictureUri: Uri? = null
    private var savedProfileImagePath: String? = null

    private val requestImagePick = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            when (currentUploadType) {
                UploadType.PROFILE -> {
                    profilePictureUri = it

                    val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val userEmail = sharedPreferences.getString("email", "no_email") ?: "no_email"

                    savedProfileImagePath = FileUtils.saveProfileImageToInternalStorage(this, userEmail, it)

                    savedProfileImagePath?.let { path ->
                        Glide.with(this)
                            .load(File(path))
                            .into(profileImageView)

                        CoroutineScope(Dispatchers.IO).launch {
                            val userDao = AppDatabase.getDatabase(this@ProfileActivity).userDao()
                            val user = userDao.getUserByEmail(userEmail)

                            user?.let { currentUser ->
                                val updatedUser = currentUser.copy(profilePicture = path)
                                userDao.update(updatedUser)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImageView = findViewById(R.id.imageView)
        fullNameTextView = findViewById(R.id.full_name)
        joinedDateTextView = findViewById(R.id.joined)
        emailTextView = findViewById(R.id.email_tmp)
        genderTextView = findViewById(R.id.gender)
        backButton = findViewById(R.id.back_btn)
        buttonUpload = findViewById(R.id.buttonUpload)
        buttonLogOut = findViewById(R.id.logout_btn)

        makeImageViewRound()

        loadUserData()

        backButton.setOnClickListener {
            finish()
        }


        buttonUpload.setOnClickListener {
            currentUploadType = UploadType.PROFILE
            openImagePicker()
        }

        buttonLogOut.setOnClickListener {
            val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .remove("email")
                .remove("password")
                .apply()

            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "no_email")
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = AppDatabase.getDatabase(this@ProfileActivity).userDao()
            val user = userEmail?.let {
                userDao.getUserByEmail(it)
            }

            user?.let {
                runOnUiThread {
                    fullNameTextView.text = "${it.name} ${it.surname}"
                    joinedDateTextView.text = "${joinedDateTextView.text} ${it.registrationDate}"
                    emailTextView.text = userEmail
                    genderTextView.text = it.gender
                    if (it.profilePicture.isNotEmpty()) {
                        try {
                            val imageFile = File(it.profilePicture)

                            Glide.with(this@ProfileActivity)
                                .load(imageFile)
                                .apply(
                                    RequestOptions()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .centerCrop())
                                .into(profileImageView)

                            makeImageViewRound()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}