package ru.nikiforova.drivenext

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.nikiforova.drivenext.adapter.CarAdapter
import ru.nikiforova.drivenext.data.AppDatabase

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var carAdapter: CarAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val noResultsTextView: TextView = findViewById(R.id.no_results_text_view)
        val backButton: ImageButton = findViewById(R.id.back_btn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(emptyList())
        recyclerView.adapter = carAdapter

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.initDatabase(this)

        backButton.setOnClickListener {
            finish()
        }

        carAdapter.setOnItemClickListener { car ->
            viewModel.toggleCarSaveState(car)
        }

        carAdapter.setOnDeleteClickListener { car ->
            viewModel.deleteCar(car)
        }

        val query = intent.getStringExtra("query")
        if (query != null) {
            val database = AppDatabase.getDatabase(this)
            database.carDao().searchCars("%$query%").observe(this) { cars ->
                if (cars.isNotEmpty()) {
                    carAdapter.updateList(cars)
                    noResultsTextView.visibility = View.GONE
                } else {
                    carAdapter.updateList(emptyList())
                    noResultsTextView.visibility = View.VISIBLE
                }
            }
        } else {
            noResultsTextView.visibility = View.VISIBLE
        }
    }
}