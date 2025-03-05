package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager

class WelcomeActivity : BaseActivity(), OnButtonClickListener {

    private lateinit var viewPager: ViewPager
    private lateinit var adapter: WelcomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        saveCurrentActivity()

        viewPager = findViewById(R.id.viewPager)
        adapter = WelcomePagerAdapter(this, this) // Передаём текущий класс как слушателя событий
        viewPager.adapter = adapter
    }

    override fun onNextClicked(position: Int) {
        if (position == adapter.count - 1) {
            startActivity(Intent(this, LoginRegisterActivity::class.java))
            finish()
        } else {
            viewPager.currentItem = position + 1
        }
    }

    override fun onSkipClicked() {
        startActivity(Intent(this, LoginRegisterActivity::class.java))
        finish()
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@WelcomeActivity::class.java.name)
            apply()
        }
    }
}
