package small.app.shopping.list.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.DepartmentWithItems


@Dao
interface DepartmentDao {
    @Query("SELECT * FROM Department")
    fun getAll(): LiveData<List<Department>?>

    @Transaction
    @Query("SELECT * FROM Department WHERE dep_name==:departmentName ORDER BY dep_order")
    fun getItemsFromDepartment(departmentName: String): DepartmentWithItems?

    @Transaction
    @Query("SELECT * FROM Department ORDER BY dep_order")
    fun getAllDepartment(): LiveData<List<DepartmentWithItems>?>

    @Transaction
    @Query("SELECT * FROM Department WHERE dep_isUsed==:used ORDER BY dep_order")
    fun getUnusedDepartment(used: Boolean = false): LiveData<List<DepartmentWithItems>?>

    @Transaction
    @Query("SELECT dep_name FROM Department WHERE dep_isUsed==:used ORDER BY dep_order")
    fun getUnusedDepartmentsName(used: Boolean = false): LiveData<List<String>>


    //Left join for the new department that have no items inside
    @Transaction
    @Query("SELECT  *  FROM  Department  WHERE Department.dep_isUsed = :used ORDER BY Department.dep_order")
    fun getUsedDepartment(used: Int = 1): LiveData<List<DepartmentWithItems>?>

    @Transaction
    @Query("SELECT * FROM Department  ORDER BY dep_order")
    fun getDepartments(): LiveData<List<DepartmentWithItems>>

    @Query("SELECT * FROM Department WHERE dep_name == :name ORDER BY dep_order")
    fun findByName(name: String): Department?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Department)

    @Transaction
    @Delete
    fun delete(item: Department)

    @Query("SELECT COUNT(*) FROM Department")
    fun getNbDep(): Int


}