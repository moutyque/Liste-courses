package small.app.liste_courses.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import small.app.liste_courses.objects.ItemsComparator

data class DepartmentWithItems(
    @Embedded
    val department: Department,
    @Relation(
        parentColumn = "dep_name",
        entityColumn = "departmentId"
    )
    val items: List<Item>

) {
    fun toDepartment(): small.app.liste_courses.models.Department {
        return small.app.liste_courses.models.Department(
            name = this.department.dep_name,
            isUsed = this.department.dep_isUsed,
            itemsCount = this.department.dep_itemsCount,
            items = this.items.toMutableList().sortedWith(ItemsComparator()).toMutableList(),
            order = this.department.dep_order
        )
    }
}
