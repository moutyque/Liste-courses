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

    @Query("SELECT * FROM Item WHERE departmentId == :depName AND storeId == :storeId ORDER BY `order`")
    fun fetchDepItems(depName: String, storeId: String): LiveData<List<Item>?>

    @Query("SELECT * FROM Item WHERE departmentId == :depName AND storeId == :storeId ORDER BY `order`")
    fun getDepItems(depName: String, storeId: String): List<Item>


    @Query("SELECT * FROM Item WHERE departmentId == :depName AND storeId == :storeId AND isUsed == :isUsed ORDER BY `order`")
    fun getUsedDepItems(depName: String, storeId: String, isUsed: Boolean = true): List<Item>

    @Query("SELECT name FROM Item WHERE departmentId == :depName AND storeId == :storeId AND isUsed == :isUsed ORDER BY `order`")
    fun fetchUnusedDepItems(
        depName: String,
        storeId: String,
        isUsed: Boolean = false
    ): LiveData<List<String>>


}