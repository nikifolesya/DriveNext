package ru.nikiforova.drivenext

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.nikiforova.drivenext.adapter.CarAdapter

class HomeFragment : Fragment() {

    private lateinit var carAdapter: CarAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        carAdapter = CarAdapter(emptyList())
        recyclerView.adapter = carAdapter

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.initDatabase(requireContext())

        viewModel.cars.observe(viewLifecycleOwner) { cars ->
            Log.d("HomeFragment", "Cars loaded: ${cars.size}")
            carAdapter.updateList(cars)
        }

        val searchEditText = view.findViewById<TextInputEditText>(R.id.searchEditText)
        val searchTextInputLayout = view.findViewById<TextInputLayout>(R.id.searchTextInputLayout)

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    val intent = Intent(requireContext(), SearchResultsActivity::class.java)
                    intent.putExtra("query", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

            }
        })

        searchTextInputLayout.setEndIconOnClickListener {
            searchEditText.setText("")
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