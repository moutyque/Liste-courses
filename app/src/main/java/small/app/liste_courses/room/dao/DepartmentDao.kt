package small.app.liste_courses.room.dao

import androidx.room.*
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.DepartmentWithItems


@Dao
interface DepartmentDao {
    @Query("SELECT * FROM Department")
    fun getAll(): List<Department>


    @Query("SELECT * FROM Department WHERE name==:departmentName ORDER BY `order`")
    fun getItemsFromDepartment(departmentName: String): DepartmentWithItems

    @Query("SELECT * FROM Department ORDER BY `order`")
    fun getAllDepartment(): DepartmentWithItems

    @Query("SELECT * FROM Department WHERE name == :name ORDER BY `order`")
    fun findByName(name: String): Department

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Department)

    @Transaction
    @Delete
    fun delete(item: Department)
}