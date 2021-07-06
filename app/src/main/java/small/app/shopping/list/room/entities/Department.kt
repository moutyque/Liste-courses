package small.app.shopping.list.room.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Department(
    @PrimaryKey @NonNull @ColumnInfo(name = "dep_name") val name: String,
    @ColumnInfo(name = "dep_isUsed") var isUsed: Boolean,
    @ColumnInfo(name = "dep_itemsCount") var itemsCount: Int,
    @ColumnInfo(name = "dep_order") var order: Int

)
