package small.app.shopping.list.room.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import small.app.shopping.list.enums.SIUnit

@Entity(
    foreignKeys = [ForeignKey(
        entity = Department::class,
        parentColumns = ["dep_name"],
        childColumns = ["departmentId"]
    )]
)
data class Item(
    @PrimaryKey
    @NonNull
    val name: String,
    var isUsed: Boolean = false,

    var departmentId: String = "",
    var order: Long = -1,
    var qty: Long = 0,
    var unit: SIUnit = SIUnit.EMPTY
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (name != other.name) return false
        if (isUsed != other.isUsed) return false
        if (departmentId != other.departmentId) return false
        if (order != other.order) return false
        if (qty != other.qty) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + isUsed.hashCode()
        result = 31 * result + departmentId.hashCode()
        result = 31 * result + order.hashCode()
        result = 31 * result + qty.hashCode()
        result = 31 * result + unit.hashCode()
        return result
    }
}
