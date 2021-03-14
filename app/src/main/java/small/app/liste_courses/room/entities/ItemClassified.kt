package small.app.liste_courses.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ItemClassified(
    @Embedded val department: Department,
    @Relation(
        parentColumn = "name",
        entityColumn = "departmentId"
    )
    val items: List<Item>

)
