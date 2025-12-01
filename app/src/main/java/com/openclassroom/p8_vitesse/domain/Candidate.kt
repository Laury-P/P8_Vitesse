package com.openclassroom.p8_vitesse.domain

import java.time.LocalDate

data class Candidate(
    val id: Long? = null,
    var photo: String? = null,
    var firstName: String,
    var lastName: String,
    var note: String? = null,
    var isFavorite: Boolean = false,
    var phoneNumber: String,
    var email: String,
    var dateOfBirth: LocalDate,
    var expectedSalary: Double? = null,

)
