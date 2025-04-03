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
            val carDao = database.carDao()

            // Проверяем, есть ли уже записи в таблице car
            val carCount = carDao.getCount()

            if (carCount == 0) {

                val cars = listOf(
                    Car(name = "S 500 Sedan", brand = "Mercedes-Benz", price = "2500₽", transmission = "A/T", engineType = "Бензин", imageResId = R.drawable.car, isSaved = 0),
                    Car(name = "Model Y", brand = "Tesla", price = "3500₽", transmission = "A/T", engineType = "Электро", imageResId = R.drawable.tesla, isSaved = 0),
                    Car(name = "4 Series", brand = "BMW", price = "3000₽", transmission = "A/T", engineType = "Дизель", imageResId = R.drawable.bmw, isSaved = 0),
                    Car(name = "K5", brand = "Kia", price = "2000₽", transmission = "A/T", engineType = "Бензин", imageResId = R.drawable.kia, isSaved = 0)
                )

                for (car in cars) {
                    carDao.insertCar(car)
                }
            }
        }
    }

}