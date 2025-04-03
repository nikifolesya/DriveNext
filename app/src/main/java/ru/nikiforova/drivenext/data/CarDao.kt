package ru.nikiforova.drivenext.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)

    @Update
    suspend fun updateCar(car: Car)

    @Query("SELECT * FROM car")
    fun getAllCars(): LiveData<List<Car>>

    @Query("SELECT * FROM car WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    fun searchCars(query: String): LiveData<List<Car>>

    @Query("SELECT * FROM car WHERE isSaved = 1")
    fun getSavedCars(): LiveData<List<Car>>

    @Query("SELECT COUNT(*) FROM car")
    suspend fun getCount(): Int
}