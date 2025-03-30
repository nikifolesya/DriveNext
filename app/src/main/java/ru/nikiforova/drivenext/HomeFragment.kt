package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import ru.nikiforova.drivenext.adapter.CarAdapter
import ru.nikiforova.drivenext.data.AppDatabase


class HomeFragment : Fragment() {

    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        carAdapter = CarAdapter(emptyList())
        recyclerView.adapter = carAdapter

        val database = AppDatabase.getDatabase(requireContext())

        database.carDao().getAllCars().observe(viewLifecycleOwner) { cars ->
            carAdapter.updateList(cars)
        }


        val searchView = view.findViewById<SearchView>(R.id.search_bar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {

                    val intent = Intent(requireContext(), LoadingActivity::class.java)
                    intent.putExtra("query", it)
                    startActivity(intent)
                }
                return true
            }
        })

        return view
    }
}