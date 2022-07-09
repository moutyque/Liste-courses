package small.app.shopping.list.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.Store
import small.app.shopping.list.room.entities.StoreWithDepartment

@Dao
interface StoreDao {
    @Query("SELECT * FROM Store ORDER BY store_name")
    fun fetchAll(): LiveData<List<Store>>

    @Query("SELECT * FROM Store")
    fun getAll(): List<Store>

    @Query("SELECT * FROM Store WHERE store_name==:name")
    fun getStore(name: String): Store?

    @Query("SELECT * FROM Store WHERE isUsed = 1")
    fun getUsedStore(): Store?

    @Query("SELECT store_name FROM Store")
    fun fetchNames(): LiveData<List<String>>

    @Query("SELECT * FROM Store WHERE isUsed = 1")
    fun fetchUsedStore(): LiveData<StoreWithDepartment?>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Store)

    @Transaction
    @Delete
    fun delete(item: Store)

}