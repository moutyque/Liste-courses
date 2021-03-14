package small.app.liste_courses

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import small.app.liste_courses.room.AppDatabase
import small.app.liste_courses.room.dao.DepartmentDao
import small.app.liste_courses.room.dao.ItemDao
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.Item
import java.io.IOException

@RunWith(AndroidJUnit4::class)

class RoomDBTest {

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


}