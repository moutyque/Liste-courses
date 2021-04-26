package small.app.liste_courses.room.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Department(
    @PrimaryKey @NonNull val name: String,
    var isUsed: Boolean,
    var itemsCount : Int,
    var order: Int

)
