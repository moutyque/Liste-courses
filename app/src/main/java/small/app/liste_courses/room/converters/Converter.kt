package small.app.liste_courses.room.converters

import androidx.room.TypeConverter
import small.app.liste_courses.SIUnit

class Converter {

    @TypeConverter
    fun toSIUnit(value: String) = enumValueOf<SIUnit>(value)

    @TypeConverter
    fun fromSIUnit(value: SIUnit) = value.name

}