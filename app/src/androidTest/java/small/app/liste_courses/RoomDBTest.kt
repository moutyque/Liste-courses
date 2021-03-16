package small.app.liste_courses

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import small.app.liste_courses.room.AppDatabase
import small.app.liste_courses.room.Repository
import small.app.liste_courses.room.dao.DepartmentDao
import small.app.liste_courses.room.dao.ItemDao
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.Item
import java.io.IOException
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)

class RoomDBTest {

    private lateinit var itemDao: ItemDao
    private lateinit var departmentDao: DepartmentDao

    private lateinit var repo : Repository

    private lateinit var db: AppDatabase
    lateinit var instrumentationContext: Context
    private val backgroundScope = CoroutineScope(Job() + Dispatchers.IO)


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        itemDao = db.itemDAO()
        departmentDao = db.departmentDAO()
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        repo = Repository(instrumentationContext)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createItem() {
        val item: Item = Item("POMME")
        itemDao.insertAll(item)
        val byName = itemDao.findByName("POMME")
        assertThat(byName.isClassified, equalTo(false))
    }

    @Test
    @Throws(Exception::class)
    fun createDepartment() {
        val dep = Department("FRUITS")
        departmentDao.insertAll(dep)
        val byName = departmentDao.findByName("FRUITS")
        assertThat(byName.name, equalTo("FRUITS"))

    }

    @Test
    @Throws(Exception::class)
    fun classifyItem() {
        val dep = Department("FRUITS")
        departmentDao.insertAll(dep)
        departmentDao.insertAll(Department("LEGUMES"))
        departmentDao.insertAll(Department("VIANDES"))
        var depa = departmentDao.findByName("FRUITS")
        assertThat(depa.name, equalTo("FRUITS"))

        val item: Item = Item("POMME")
        dep.classify(item)
        itemDao.insertAll(item)
        var it = itemDao.findByName("POMME")
        assertThat(it.isClassified, equalTo(false))

        val itemsFromDepartment = departmentDao.getItemsFromDepartment("FRUITS")
        Assert.assertEquals("","POMME",itemsFromDepartment.items.get(0).name)
        Log.d("TEST","$itemsFromDepartment.size")

    }

    @Test
    fun saveDepartmentWithItems(){
        val list = ArrayList<Item>()
        list.add(Item("POMME"))
        list.add(Item("BANANNE"))
        list.add(Item("POIRE"))
        val newDep = small.app.liste_courses.model.Department("FRUITS",list)

        backgroundScope.launch {
            repo.saveDepartment(newDep)
            val departmentByName = repo.getDepartmentByName("FRUITS")
            for(i in departmentByName.items)        {
                Assert.assertEquals("",i.departmentId,"FRUITS")
                Assert.assertEquals("",i.isClassified,true)
            }

            Assert.assertEquals("",departmentByName,newDep)
        }

    }

    @Test
    fun notExistingDepartment(){
        val findByName = departmentDao.findByName("NULL")
        assert(findByName == null)
        backgroundScope.launch { assert(!repo.departmentExist("NULL"))}
    }




}