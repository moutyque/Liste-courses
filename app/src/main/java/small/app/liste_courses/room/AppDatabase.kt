package small.app.liste_courses.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import small.app.liste_courses.room.dao.DepartmentDao
import small.app.liste_courses.room.dao.ItemDao
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.Item
import java.util.concurrent.Executors

@Database(
    entities = arrayOf(Item::class, Department::class),
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDao
    abstract fun departmentDAO(): DepartmentDao
}

private val IOScope = CoroutineScope(Job() + Dispatchers.IO)
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

@Volatile
private lateinit var INSTANCE: AppDatabase
fun getInstance(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {

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