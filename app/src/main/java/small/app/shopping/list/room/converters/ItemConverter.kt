package small.app.shopping.list.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import small.app.shopping.list.room.entities.Item

class ItemConverter {
    private val type = object : TypeToken<Item>() {}.type

    @TypeConverter
    fun toItem(itemInfo: String): Item {
        return Gson().fromJson(itemInfo, type)
    }

    @TypeConverter
    fun toJson(item: Item): String {
        return Gson().toJson(item, type)
    }
}