package small.app.liste_courses.room.dao

import androidx.room.*
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.ItemClassified


@Dao
interface DepartmentDao {
    @Query("SELECT * FROM Department")
    fun getAll(): List<Department>

    @Transaction
    @Query("SELECT * FROM Department")
    fun getUsersWithPlaylists(): List<ItemClassified>


    @Query("SELECT * FROM Department WHERE name == :name")
    fun findByName(name: String): Department

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Department)

    @Delete
    fun delete(item: Department)
}