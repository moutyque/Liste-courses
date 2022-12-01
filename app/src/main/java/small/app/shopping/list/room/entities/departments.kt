package small.app.shopping.list.room.entities

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import small.app.shopping.list.comparators.ItemsComparator

@Entity(
    foreignKeys = [ForeignKey(
        entity = Store::class,
        parentColumns = ["store_name"],
        childColumns = ["dep_store"],
        onDelete = CASCADE
    )],
    primaryKeys = ["dep_id"]
)
data class Department(
    @NonNull @ColumnInfo(name = "dep_id") val id: String,
    @NonNull @ColumnInfo(name = "dep_name") val name: String,
    @ColumnInfo(name = "dep_isUsed") var isUsed: Boolean,
    @ColumnInfo(name = "dep_itemsCount") var itemsCount: Int,
    @ColumnInfo(name = "dep_order") var order: Int,
    @ColumnInfo(name = "dep_store") var storeId: String

)

data class DepartmentWithItems(
    @Embedded
    val department: Department,
    @Relation(
        parentColumn = "dep_id",
        entityColumn = "departmentId"
    )
    var items: List<Item>
) {
    fun toDepartment(): small.app.shopping.list.models.Department {
        return small.app.shopping.list.models.Department(
            id= department.id.ifBlank { "${department.storeId}_${department.name}" },
            name = department.name,
            isUsed = department.isUsed,
            itemsCount = department.itemsCount,
            items = items.asSequence().sortedWith(ItemsComparator()).toMutableList(),
            order = department.order,
            storeId =  department.storeId
        )
    }
}