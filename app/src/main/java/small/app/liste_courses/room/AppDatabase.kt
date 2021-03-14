package small.app.liste_courses.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import small.app.liste_courses.room.dao.DepartmentDao
import small.app.liste_courses.room.dao.ItemDao
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.Item

@Database(
    entities = arrayOf(Item::class, Department::class),
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDao
    abstract fun departmentDAO(): DepartmentDao
}

lateinit var _context: Context

@Volatile
private lateinit var INSTANCE: AppDatabase
fun getInstance(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            _context = context
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration()
                .build()

        }
        return INSTANCE
    }
}