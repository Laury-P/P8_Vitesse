package com.openclassroom.p8_vitesse.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity (
    tableName = "candidates"
)
data class CandidateDto (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val photo: String? = null,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    val note: String? = null,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    val email: String,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: LocalDate,

    @ColumnInfo(name = "expected_salary")
    val expectedSalary: Double? = null,
)