package ru.nikiforova.drivenext

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikiforova.drivenext.data.AppDatabase
import ru.nikiforova.drivenext.data.Car

class HomeViewModel : ViewModel() {

    private val _cars = MutableLiveData<List<Car>>()
    val cars: LiveData<List<Car>> get() = _cars

    private lateinit var database: AppDatabase

    fun initDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch {
            database.carDao().getAllCars().observeForever { cars ->
                _cars.postValue(cars ?: emptyList())
            }
        }
    }

    fun toggleCarSaveState(car: Car) {
        viewModelScope.launch {
            val updatedCar = car.copy(isSaved = if (car.isSaved == 1) 0 else 1)
            database.carDao().updateCar(updatedCar)
            loadCars()
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            val updatedCar = car.copy(isSaved = 0)
            database.carDao().updateCar(updatedCar)
            loadCars()
        }
    }
}