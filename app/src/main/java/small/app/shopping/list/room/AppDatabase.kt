package small.app.shopping.list.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import small.app.shopping.list.room.converters.Converter
import small.app.shopping.list.room.converters.ItemConverter
import small.app.shopping.list.room.dao.DepartmentDao
import small.app.shopping.list.room.dao.ItemDao
import small.app.shopping.list.room.dao.StoreDao
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.Item
import small.app.shopping.list.room.entities.Store

@Database(
    entities = [Item::class, Department::class, Store::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converter::class, ItemConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDao
    abstract fun departmentDAO(): DepartmentDao
    abstract fun storeDao(): StoreDao
}

@Volatile
private lateinit var INSTANCE: AppDatabase
fun getDBInstance(context: Context): AppDatabase {
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