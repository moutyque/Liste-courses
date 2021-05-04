package small.app.liste_courses.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import small.app.liste_courses.room.entities.Department
import small.app.liste_courses.room.entities.DepartmentWithItems


@Dao
interface DepartmentDao {
    @Query("SELECT * FROM Department")
    fun getAll(): List<Department>

    @Transaction
    @Query("SELECT * FROM Department WHERE dep_name==:departmentName ORDER BY dep_order")
    fun getItemsFromDepartment(departmentName: String): DepartmentWithItems?
    @Transaction
    @Query("SELECT * FROM Department ORDER BY dep_order")
    fun getAllDepartment(): LiveData<List<DepartmentWithItems>>

    @Transaction
    @Query("SELECT * FROM Department WHERE dep_isUsed==:used ORDER BY dep_order")
    fun getUnusedDepartment(used: Boolean = false): LiveData<List<DepartmentWithItems>?>


    //TODO : ça marche pas
    @Transaction
    //@Query("SELECT Department.*,Item.* FROM Department,Item WHERE Department.isUsed==:used AND Item.isUsed==:used ORDER BY `order`")
    @Query("SELECT  *  FROM  Department  INNER JOIN Item ON Item.departmentId = Department.dep_name AND Item.isUsed=1 WHERE Department.dep_isUsed = :used AND Item.isUsed= :used ORDER BY Department.dep_order")
    //@Query("SELECT Department.`order` as dOrder, Department.isUsed as dUsage, Department.name as dName, Department.itemsCount as dCount, Item.name as iName, Item.isUsed as iUsage, Item.`order` as iOrder, Item.unit as iUnit, Item.qty as iQty, Item.isClassified as iClassified, Item.departmentId as iDepName  FROM  Department  INNER JOIN Item ON Item.departmentId = Department.name WHERE Item.isUsed= :used ORDER BY Department.'order'")
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
}