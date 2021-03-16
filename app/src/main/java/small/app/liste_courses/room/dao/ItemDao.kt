package small.app.liste_courses.room.dao

import androidx.room.*
import small.app.liste_courses.room.entities.Item


@Dao
interface ItemDao {
    //TODO : check to get the classified item
    @Query("SELECT * FROM Item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM Item WHERE isUsed == :isUsed")
    fun getAllWithUsage(isUsed: Boolean): List<Item>

    @Query("SELECT * FROM Item WHERE isUsed == :isUsed AND isClassified == :isClassified")
    fun getAllWithUsageAndClassification(isUsed: Boolean, isClassified: Boolean): List<Item>

    @Query("SELECT * FROM Item WHERE name == :ref")
    fun findByName(ref: String): Item

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: Item)

    @Transaction
    @Delete
    fun delete(item: Item)
}