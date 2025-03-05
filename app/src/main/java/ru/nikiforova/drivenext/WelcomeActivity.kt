package ru.nikiforova.drivenext

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

class WelcomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        saveCurrentActivity()

        viewPager = findViewById(R.id.viewPager)

        val adapter = WelcomePagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Логика для отображения кнопок на последнем слайде
                val currentView = adapter.instantiateItem(viewPager, position) as View
                val nextButton: Button = currentView.findViewById(R.id.next_btn)
                val skipButton: Button = currentView.findViewById(R.id.miss_btn)

                if (position == adapter.count - 1) {
                    nextButton.text = getString(R.string.drive_btn)
                    nextButton.setOnClickListener {
                        startActivity(Intent(this@WelcomeActivity, LoginRegisterActivity::class.java))
                        finish()
                    }
                } else {
                    nextButton.text = getString(R.string.next_btn)
                    nextButton.setOnClickListener {
                        viewPager.currentItem = position + 1
                    }
                }

                skipButton.setOnClickListener {
                    startActivity(Intent(this@WelcomeActivity, LoginRegisterActivity::class.java))
                    finish()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        // Пример проверки интернет-соединения
        if (!isInternetAvailable()) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        }
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
            putString("previous_activity", this@WelcomeActivity::class.java.name)
            apply()
        }
    }
}