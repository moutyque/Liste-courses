package small.app.shopping.list.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import small.app.shopping.list.room.entities.Department

class DepartmentConverter {
    private val type = object : TypeToken<Department>() {}.type

    @TypeConverter
    fun toItem(depInfo: String): Department {
        return Gson().fromJson(depInfo, type)
    }

    @TypeConverter
    fun toJson(dep: Department): String {
        return Gson().toJson(dep, type)
    }
}