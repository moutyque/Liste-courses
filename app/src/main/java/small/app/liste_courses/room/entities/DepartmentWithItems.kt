package small.app.liste_courses.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DepartmentWithItems(
    @Embedded val department: Department,
    @Relation(
        parentColumn = "name",
        entityColumn = "departmentId"
    )
    val items: List<Item>

) {
    fun toDepartment(): small.app.liste_courses.models.Department {
        return small.app.liste_courses.models.Department(
            name = this.department.name,
            isUsed = this.department.isUsed,
            itemsCount = this.department.itemsCount,
            items = this.items.toMutableList(),
            order = this.department.order
        )
    }
}
