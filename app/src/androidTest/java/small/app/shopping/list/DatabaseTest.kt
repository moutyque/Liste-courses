package small.app.shopping.list

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import small.app.shopping.list.room.AppDatabase
import small.app.shopping.list.room.converters.DepartmentConverter
import small.app.shopping.list.room.converters.ItemConverter
import small.app.shopping.list.room.dao.DepartmentDao
import small.app.shopping.list.room.dao.ItemDao
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.Item
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var itemDao: ItemDao
    private lateinit var depDao: DepartmentDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        itemDao = db.itemDAO()
        depDao = db.departmentDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndExportItem() {
        val item = Item(name = "TEST")
        itemDao.insertAll(item)
        val found = itemDao.findByName("TEST")
        MatcherAssert.assertThat(found!!.name, equalTo(item.name))

        val conv = ItemConverter()
        val expected = """
            {"departmentId":"","isUsed":false,"name":"TEST","order":-1,"qty":0,"unit":"EMPTY"}
                    """.trimIndent()
        MatcherAssert.assertThat(conv.toJson(item), equalTo(expected))
    }

    @Test
    @Throws(Exception::class)
    fun createAndExportDepartment() {
        val dep = Department(name = "DEP1", isUsed = false, itemsCount = 0, order = 0)
        depDao.insertAll(dep)
        val found = depDao.findByName("DEP1")
        MatcherAssert.assertThat(found!!.name, equalTo(dep.name))

        val conv = DepartmentConverter()
        Log.d("TEST", conv.toJson(dep))
        val expected = """
            {"isUsed":false,"itemsCount":0,"name":"DEP1","order":0}
                     """.trimIndent()
        MatcherAssert.assertThat(conv.toJson(dep), equalTo(expected))
    }

}