package ru.nikiforova.drivenext

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nikiforova.drivenext.adapter.CarAdapter

class SavedFragment : Fragment() {

    private lateinit var carAdapter: CarAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)

        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.saved_recycler_view)
        val noSavedCarsTextView = view.findViewById<TextView>(R.id.no_saved_cars_txt)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        carAdapter = CarAdapter(emptyList())
        recyclerView.adapter = carAdapter

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        viewModel.initDatabase(requireContext())

        viewModel.cars.observe(viewLifecycleOwner) { cars ->
            val savedCars = cars.filter { it.isSaved == 1 }
            if (savedCars.isNotEmpty()) {
                carAdapter.updateList(savedCars)
                recyclerView.visibility = View.VISIBLE
                noSavedCarsTextView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                noSavedCarsTextView.visibility = View.VISIBLE
            }
        }

        carAdapter.setOnItemClickListener { car ->
            viewModel.toggleCarSaveState(car)
        }

        carAdapter.setOnDeleteClickListener { car ->
            viewModel.deleteCar(car)
        }

        return view
    }
}