package small.app.shopping.list.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import small.app.shopping.list.room.entities.Department
import small.app.shopping.list.room.entities.Store

class StoreConverter {
    private val type = object : TypeToken<Store>() {}.type

    @TypeConverter
    fun toStore(storeInfo: String): Store {
        return Gson().fromJson(storeInfo, type)
    }

    @TypeConverter
    fun toJson(store: Store): String {
        return Gson().toJson(store, type)
    }
}
