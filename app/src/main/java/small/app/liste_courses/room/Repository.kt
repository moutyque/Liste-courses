package small.app.liste_courses.room

import android.content.Context
import small.app.liste_courses.room.entities.Item

class Repository(context: Context) {
    val db = getInstance(context)

    suspend fun getAllDepartment() {

    }

    suspend fun getUnusedItems(): List<Item> {
        return db.itemDAO().getAllWithUsage(false)
    }

    suspend fun getUsedItems(): List<Item> {
        return db.itemDAO().getAllWithUsage(true)
    }

    suspend fun getUnclassifiedItem(): List<Item> {
        return db.itemDAO().getAllWithUsageAndClassification(isUsed = true, isClassified = false)
    }

    suspend fun addItem(i: Item) {
        db.itemDAO().insertAll(i)
    }

}