package ru.nikiforova.drivenext.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car)

    @Query("SELECT * FROM cars")
    fun getAllCars(): LiveData<List<Car>>

    @Query("SELECT * FROM cars WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    fun searchCars(query: String): LiveData<List<Car>>
}