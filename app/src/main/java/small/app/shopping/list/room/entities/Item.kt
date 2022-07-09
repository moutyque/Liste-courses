package small.app.shopping.list.room.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import small.app.shopping.list.enums.SIUnit

@Entity(
    foreignKeys = [ForeignKey(
        entity = Department::class,
        parentColumns = ["dep_id"],
        childColumns = ["departmentId"]
    )],
    primaryKeys = ["name","storeId"]
)
data class Item(
    @NonNull
    val name: String,
    var isUsed: Boolean = false,
    @ColumnInfo(index = true)
    var departmentId: String = "",
    @ColumnInfo(index = true)
    var storeId: String = "",
    var order: Long = -1,
    var qty: Long = 0,
    var unit: SIUnit = SIUnit.EMPTY
)
