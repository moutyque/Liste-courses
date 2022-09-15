package small.app.shopping.list.room.entities

import androidx.annotation.NonNull
import androidx.room.*

@Entity
data class Store(
    @PrimaryKey @NonNull @ColumnInfo(name = "store_name") val name: String,
    var isUsed: Boolean
)

data class StoreWithDepartment(
    @Embedded
    val store: Store,
    @Relation(
        parentColumn = "store_name",
        entityColumn = "dep_store",
        entity = Department::class
    )
    var departments: List<DepartmentWithItems>
)
