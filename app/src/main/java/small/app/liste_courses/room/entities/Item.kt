package small.app.liste_courses.room.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import small.app.liste_courses.SIUnit

@Entity
data class Item(
    @PrimaryKey @NonNull val name: String,
    var isClassified: Boolean = false,
    var isUsed: Boolean = false,
    var departmentId: String = "",
    var order: Long = -1,
    var qty: Long = 0,
    var unit: SIUnit = SIUnit.EMPTY //TODO : can be replace later by an enum, for lather use think of imperial unit
)
