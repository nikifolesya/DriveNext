package ru.nikiforova.drivenext

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikiforova.drivenext.data.AppDatabase
import java.io.File

class SettingsFragment : Fragment() {

    private lateinit var photoIcon: ImageView
    private lateinit var fullNameTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        photoIcon = view.findViewById(R.id.photo_icon)
        fullNameTextView = view.findViewById(R.id.full_name)
        emailTextView = view.findViewById(R.id.email_temp)

        loadUserData()

        view.findViewById<View>(R.id.next_arrow1).setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "no_email")

        if (userEmail != "no_email") {
            CoroutineScope(Dispatchers.IO).launch {
                val user = userEmail?.let {
                    AppDatabase.getDatabase(requireContext()).userDao().getUserByEmail(it)
                }
                user?.let {
                    requireActivity().runOnUiThread {
                        fullNameTextView.text = "${it.name} ${it.surname}"
                        emailTextView.text = it.email

                        if (it.profilePicture.isNotEmpty()) {
                            try {
                                val imageFile = File(it.profilePicture)

                                Glide.with(requireContext())
                                    .load(imageFile)
                                    .apply(RequestOptions()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .centerCrop())
                                    .into(photoIcon)

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

    private fun makeImageViewRound() {
        photoIcon.clipToOutline = true
        photoIcon.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val radius = view.width.coerceAtMost(view.height) / 2f
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
    }
}