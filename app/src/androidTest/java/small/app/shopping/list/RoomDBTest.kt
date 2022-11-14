package small.app.shopping.list

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import small.app.shopping.list.objects.Utils
import small.app.shopping.list.room.AppDatabase
import small.app.shopping.list.room.Repository
import small.app.shopping.list.room.converters.DepartmentConverter
import small.app.shopping.list.room.converters.ItemConverter
import small.app.shopping.list.room.dao.DepartmentDao
import small.app.shopping.list.room.dao.ItemDao
import small.app.shopping.list.room.dao.StoreDao
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.DepartmentWithItems
import small.app.shopping.list.room.entities.Item
import small.app.shopping.list.room.entities.Store
import java.io.IOException

@RunWith(AndroidJUnit4::class)

class RoomDBTest {
    private lateinit var itemDao: ItemDao
    private lateinit var departmentDao: DepartmentDao
    private lateinit var storeDao: StoreDao


    private lateinit var db: AppDatabase


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        itemDao = db.itemDAO()
        departmentDao = db.departmentDAO()
        storeDao = db.storeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun createDepartment() {
        val store = Store("", true)
        storeDao.insertAll(store)
        val dep = Department("id", "FRUITS", true, 0, 0, "")
        departmentDao.insertAll(dep)
        val byName = departmentDao.getByName("FRUITS")
        assertThat(byName!!.name, equalTo("FRUITS"))

    }

    @Test
    @Throws(Exception::class)
    fun classifyItem() {

        val storeID = "storeId"
        val store = Store(storeID, true)
        storeDao.insertAll(store)
        val dep = Department("1", "FRUITS", true, 0, 0, storeID)
        departmentDao.insertAll(dep)
        departmentDao.insertAll(Department("2", "LEGUMES", true, 0, 1, storeID))
        departmentDao.insertAll(Department("3", "VIANDES", true, 0, 2, storeID))
        val dep1 = departmentDao.getByName("FRUITS")
        assertThat(dep1, notNullValue())
        assertThat(dep1!!.name, equalTo("FRUITS"))
        Utils.repo = Repository(db)
        val item = Item("POMME")

        DepartmentWithItems(dep1, emptyList()).toDepartment().classify(item) //Not saved before query
        departmentDao.insertAll(dep1)
        itemDao.insertAll(item)
        val itemsFromDepartment = departmentDao.getItemsFromDepartment("FRUITS", storeID)
        assertThat("", itemsFromDepartment!!.items.size, equalTo(1))
        assertThat(
            "The name does not match",
            "POMME",
            equalTo(itemsFromDepartment.items[0].name)
        )


    }

    @Test
    @Throws(Exception::class)
    fun createAndExportItem() {
        val storeId = "storeId"
        val store = Store(storeId, true)
        storeDao.insertAll(store)
        val dep = Department(
            id = "id",
            name = "DEP1",
            isUsed = false,
            itemsCount = 0,
            order = 0,
            storeId = storeId
        )
        departmentDao.insertAll(dep)
        val item = Item(name = "TEST", storeId = storeId, departmentId = dep.id)
        itemDao.insertAll(item)
        val found = itemDao.findByName("TEST", storeId)
        assertThat(found!!.name, equalTo(item.name))

        val conv = ItemConverter()
        val expected = """
            {"departmentId":"id","isUsed":false,"name":"TEST","order":-1,"qty":0,"storeId":"storeId","unit":"EMPTY"}
                    """.trimIndent()
        assertThat(conv.toJson(item), equalTo(expected))
    }

    @Test
    @Throws(Exception::class)
    fun createAndExportDepartment() {
        val storeId = "storeId"
        val store = Store(storeId, true)
        storeDao.insertAll(store)
        val dep = Department(
            id = "id",
            name = "DEP1",
            isUsed = false,
            itemsCount = 0,
            order = 0,
            storeId = storeId
        )
        departmentDao.insertAll(dep)
        val found = departmentDao.getByName("DEP1")
        assertThat(found!!.name, equalTo(dep.name))

        val conv = DepartmentConverter()
        Log.d("TEST", conv.toJson(dep))
        val expected = """
            {"id":"id","isUsed":false,"itemsCount":0,"name":"DEP1","order":0,"storeId":"storeId"}
                     """.trimIndent()
        assertThat(conv.toJson(dep), equalTo(expected))
    }


}