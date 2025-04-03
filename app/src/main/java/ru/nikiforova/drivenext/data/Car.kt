package ru.nikiforova.drivenext.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car")
data class Car(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val price: String,
    val transmission: String,
    val engineType: String,
    val imageResId: Int,
    var isSaved: Int
)