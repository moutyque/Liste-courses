package small.app.shopping.list.room.converters

import androidx.room.TypeConverter
import small.app.shopping.list.objects.SIUnit

class Converter {

    @TypeConverter
    fun toSIUnit(value: String) = enumValueOf<SIUnit>(value)

    @TypeConverter
    fun fromSIUnit(value: SIUnit) = value.name

}