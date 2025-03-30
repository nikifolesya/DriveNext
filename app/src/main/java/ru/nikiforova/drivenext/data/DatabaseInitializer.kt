package ru.nikiforova.drivenext.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nikiforova.drivenext.R

object DatabaseInitializer {

    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            if (database.carDao().getAllCars().value.isNullOrEmpty()) {
                val cars = listOf(
                    Car(name = "S 500 Sedan", brand = "Mercedes-Benz", price = "2500₽", transmission = "A/T", engineType = "Бензин", imageResId = R.drawable.car),
                    Car(name = "Model Y", brand = "Tesla", price = "3500₽", transmission = "A/T", engineType = "Электро", imageResId = R.drawable.tesla),
                    Car(name = "4 Series", brand = "BMW", price = "3000₽", transmission = "A/T", engineType = "Дизель", imageResId = R.drawable.bmw),
                    Car(name = "K5", brand = "Kia", price = "2000₽", transmission = "A/T", engineType = "Бензин", imageResId = R.drawable.kia)
                )

                for (car in cars) {
                    database.carDao().insertCar(car)
                }
            }
        }
    }
}