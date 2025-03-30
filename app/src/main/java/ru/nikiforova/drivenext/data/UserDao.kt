package ru.nikiforova.drivenext.data

import androidx.room.*
import androidx.lifecycle.LiveData
import ru.nikiforova.drivenext.data.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

//    @Update
//    suspend fun update(user: User)
//
//    @Delete
//    suspend fun delete(user: User)

//    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
//    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>
}