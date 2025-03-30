package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle

class LoadingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        saveCurrentActivity()

        val query = intent.getStringExtra("query")
        simulateSearch(query)
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@LoadingActivity::class.java.name)
            apply()
        }
    }

    private fun simulateSearch(query: String?) {
        Thread {
            Thread.sleep(2000)
            runOnUiThread {
                val intent = Intent(this, SearchResultsActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}