package ru.nikiforova.drivenext

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nikiforova.drivenext.adapter.CarAdapter
import ru.nikiforova.drivenext.data.AppDatabase


class SearchResultsActivity : BaseActivity() {

    private lateinit var carAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        saveCurrentActivity()

        val query = intent.getStringExtra("query") ?: ""

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(emptyList())
        recyclerView.adapter = carAdapter

        val database = AppDatabase.getDatabase(this)

        database.carDao().searchCars(query).observe(this) { filteredCars ->
            carAdapter.updateList(filteredCars)
        }
    }

    private fun saveCurrentActivity() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("previous_activity", this@SearchResultsActivity::class.java.name)
            apply()
        }
    }
}