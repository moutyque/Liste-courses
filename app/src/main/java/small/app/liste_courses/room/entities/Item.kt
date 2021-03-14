package small.app.liste_courses.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey val name: String,
    val isClassified: Boolean = false,
    var isUsed: Boolean = false,
    val departmentId: String = "",
    val order: Long = -1,
    val qty: Long = 0,
    val unit: String = "" //TODO : can be replace later by an enum, for lather use think of imperial unit
)
