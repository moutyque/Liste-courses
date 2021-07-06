package small.app.shopping.list.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import small.app.shopping.list.objects.ItemsComparator

data class DepartmentWithItems(
    @Embedded
    val department: Department,
    @Relation(
        parentColumn = "dep_name",
        entityColumn = "departmentId"
    )
    var items: List<Item>

) {
    fun toDepartment(): small.app.shopping.list.models.Department {
        return small.app.shopping.list.models.Department(
            name = this.department.name,
            isUsed = this.department.isUsed,
            itemsCount = this.department.itemsCount,
            items = this.items.toMutableList().sortedWith(ItemsComparator()).toMutableList(),
            order = this.department.order
        )
    }
}
