package small.app.shopping.list.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.DepartmentWithItems


@Dao
interface DepartmentDao {
    @Query("SELECT * FROM Department")
    fun getAll(): List<Department>

    @Transaction
    @Query("SELECT * FROM Department WHERE dep_name==:departmentName AND dep_store == :storeId  ORDER BY dep_order")
    fun getItemsFromDepartment(departmentName: String, storeId: String): DepartmentWithItems?

    @Transaction
    @Query("SELECT * FROM Department ORDER BY dep_order")
    fun fetchAllDepartment(): LiveData<List<DepartmentWithItems>?>

    @Transaction
    @Query("SELECT * FROM Department WHERE dep_isUsed==:used ORDER BY dep_order")
    fun fetchUnusedDepartment(used: Boolean = false): LiveData<List<DepartmentWithItems>?>

    @Transaction
    @Query("SELECT dep_name FROM Department WHERE dep_isUsed==:used ORDER BY dep_order")
    fun fetchUnusedDepartmentsName(used: Boolean = false): LiveData<List<String>>


    //Left join for the new department that have no items inside
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM  Department LEFT JOIN Store ON Store.store_name == Department.dep_store WHERE Department.dep_isUsed = :used AND Store.isUsed = 1 ORDER BY Department.dep_order")
    fun fetchUsedDepartments(used: Int = 1): LiveData<List<DepartmentWithItems>?>

    @Transaction
    @Query("SELECT * FROM Department ORDER BY dep_order")
    fun fetchDepartments(): LiveData<List<DepartmentWithItems>>

    @Query("SELECT * FROM Department WHERE dep_name == :name AND dep_store == :storeId ORDER BY dep_order")
    fun getByName(name: String, storeId: String): Department?

    @Query("SELECT * FROM Department WHERE dep_id == :depId AND dep_store == :storeId ORDER BY dep_order")
    fun getByIds(depId: String, storeId: String): Department?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg department: Department)

    @Transaction
    @Delete
    fun delete(item: Department)

    @Query("SELECT COUNT(*) FROM Department")
    fun getNbDep(): Int

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Department WHERE dep_store==:id")
    fun getStoreDepartments(id: String): List<DepartmentWithItems>


}