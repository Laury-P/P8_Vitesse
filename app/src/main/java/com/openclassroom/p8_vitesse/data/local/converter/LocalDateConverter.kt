package com.openclassroom.p8_vitesse.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun localDateToTimeStamp(date: LocalDate?) : Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun timeStampToLocalDate(value: Long?) : LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
}