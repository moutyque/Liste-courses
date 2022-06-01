package small.app.shopping.list.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import small.app.shopping.list.room.entities.Item


@Dao
interface ItemDao {

    @Query("SELECT * FROM Item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM Item WHERE isUsed == :isUsed ORDER BY `order`")
    fun getAllWithUsage(isUsed: Boolean): LiveData<List<Item>?>

    @Query("SELECT name FROM Item WHERE isUsed == :isUsed ORDER BY `order`")
    fun getUnusedItemsName(isUsed: Boolean = false): LiveData<List<String>>

    @Query("SELECT * FROM Item WHERE name == :ref ORDER BY `order`")
    fun findByName(ref: String): Item?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Item)

    @Transaction
    @Delete
    fun delete(item: Item)

    @Query("SELECT * FROM Item WHERE departmentId == :depName ORDER BY `order`")
    fun findByDepName(depName: String): LiveData<List<Item>?>

    @Query("SELECT * FROM Item WHERE departmentId == :depName AND isUsed == :isUsed ORDER BY `order`")
    fun fetchAssociatedUsedItems(depName: String, isUsed: Boolean = true): List<Item>

    @Query("SELECT name FROM Item WHERE departmentId == :depName AND isUsed == :isUsed ORDER BY `order`")
    fun findUnusedItemsNameByDepName(depName: String, isUsed: Boolean = false): LiveData<List<String>>

}