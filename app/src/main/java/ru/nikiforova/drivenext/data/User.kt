package ru.nikiforova.drivenext.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val birthDate: String,
    val gender: String,
    val licenseNumber: String,
    val issueDate: String,
    val licenseFile: String,
    val passportFile: String,
    val profilePicture: String,
    val registrationDate: String = ""
)