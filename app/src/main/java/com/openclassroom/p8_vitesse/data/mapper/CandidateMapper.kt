package com.openclassroom.p8_vitesse.data.mapper

import com.openclassroom.p8_vitesse.data.local.entity.CandidateDto
import com.openclassroom.p8_vitesse.domain.Candidate

fun Candidate.toDto(): CandidateDto {
    return CandidateDto(
        id = this.id,
        photo = this.photo,
        firstName = this.firstName,
        lastName = this.lastName,
        note = this.note,
        isFavorite = this.isFavorite,
        phoneNumber = this.phoneNumber,
        email = this.email,
        dateOfBirth = this.dateOfBirth,
        expectedSalary = this.expectedSalary,
    )
}

fun CandidateDto.toDomain(): Candidate {
    return Candidate(
        id = this.id,
        photo = this.photo,
        firstName = this.firstName,
        lastName = this.lastName,
        note = this.note,
        isFavorite = this.isFavorite,
        phoneNumber = this.phoneNumber,
        email = this.email,
        dateOfBirth = this.dateOfBirth,
        expectedSalary = this.expectedSalary,
    )
}