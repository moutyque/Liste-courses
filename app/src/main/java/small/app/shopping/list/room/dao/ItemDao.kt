package small.app.shopping.list.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import small.app.shopping.list.room.entities.Item


@Dao
interface ItemDao {

    @Query("SELECT * FROM Item")
    fun getAll(): List<Item>

    @Query("SELECT name FROM Item WHERE isUsed == :isUsed ORDER BY `order`")
    fun fetchUnusedItemsName(isUsed: Boolean = false): LiveData<List<String>>

    @Query("SELECT * FROM Item WHERE name == :itemName AND storeId == :storeName ORDER BY `order`")
    fun findByName(itemName: String, storeName: String): Item?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Item)

    @Transaction
    @Delete
    fun delete(item: Item)

    @Query("SELECT * FROM Item WHERE departmentId == :depName ORDER BY `order`")
    fun fetchDepItems(depName: String): LiveData<List<Item>?>

    @Query("SELECT * FROM Item WHERE departmentId == :depId ORDER BY `order`")
    fun getDepItems(depId: String): List<Item>


    @Query("SELECT * FROM Item WHERE departmentId == :depId AND isUsed == :isUsed ORDER BY `order`")
    fun getUsedDepItems(depId: String, isUsed: Boolean = true): List<Item>

    @Query("SELECT name FROM Item WHERE departmentId == :depId AND isUsed == :isUsed ORDER BY `order`")
    fun fetchUnusedDepItems(
        depId: String,
        isUsed: Boolean = false
    ): LiveData<List<String>>


}