package small.app.liste_courses

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class RoomDBTest {
/*
    private lateinit var itemDao: ItemDao
    private lateinit var departmentDao: DepartmentDao


    private lateinit var db: AppDatabase


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        itemDao = db.itemDAO()
        departmentDao = db.departmentDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun createDepartment() {
        val dep = Department("FRUITS", 0)
        departmentDao.insertAll(dep)
        val byName = departmentDao.findByName("FRUITS")
        assertThat(byName.dep_name, equalTo("FRUITS"))

    }

    @Test
    @Throws(Exception::class)
    fun classifyItem() {
        val dep = Department("FRUITS", 0)
        departmentDao.insertAll(dep)
        departmentDao.insertAll(Department("LEGUMES", 1))
        departmentDao.insertAll(Department("VIANDES", 2))
        var depa = departmentDao.findByName("FRUITS")
        assertThat(depa.dep_name, equalTo("FRUITS"))

        val item: Item = Item("POMME")
        depa.classify(item)
        itemDao.insertAll(item)

        val itemsFromDepartment = departmentDao.getItemsFromDepartment("FRUITS")
        Assert.assertEquals(
            "The name does not match",
            "POMME",
            itemsFromDepartment.items.get(0).name
        )

    }

    @Test
    @Throws(Exception::class)
    fun createItem() {
        val item: Item = Item("POMME")
        itemDao.insertAll(item)
        val byName = itemDao.findByName("POMME")
        assertThat(byName.isClassified, equalTo(false))
    }*/


}