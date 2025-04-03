package ru.nikiforova.drivenext.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Car::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

//val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='car'")
//        if (!cursor.moveToFirst()) {
//            db.execSQL("""
//                CREATE TABLE IF NOT EXISTS car (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                    name TEXT NOT NULL,
//                    brand TEXT NOT NULL,
//                    price TEXT NOT NULL,
//                    transmission TEXT NOT NULL,
//                    engine_type TEXT NOT NULL,
//                    image_res_id INTEGER NOT NULL,
//                    is_saved INTEGER NOT NULL DEFAULT 0
//                )
//            """.trimIndent())
//        } else {
//            db.execSQL("ALTER TABLE car ADD COLUMN is_saved INTEGER NOT NULL DEFAULT 0")
//        }
//        cursor.close()
//    }
//}