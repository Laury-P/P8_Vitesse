package com.openclassroom.p8_vitesse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassroom.p8_vitesse.data.converter.LocalDateConverter
import com.openclassroom.p8_vitesse.data.dao.CandidateDao
import com.openclassroom.p8_vitesse.data.entity.CandidateDto

@Database(
    entities = [CandidateDto::class],
    version = 1,
    exportSchema = false,
    )
@TypeConverters(LocalDateConverter::class)
abstract class VitesseDatabase : RoomDatabase() {
    abstract fun candidateDao(): CandidateDao

    companion object {
        @Volatile
        private var INSTANCE: VitesseDatabase? = null

        fun getDatabase(context: Context): VitesseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitesseDatabase::class.java,
                    "VitesseDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}