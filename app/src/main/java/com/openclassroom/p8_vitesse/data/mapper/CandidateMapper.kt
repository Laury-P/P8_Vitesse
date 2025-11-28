package com.openclassroom.p8_vitesse.data.mapper

import com.openclassroom.p8_vitesse.data.entity.CandidateDto
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

fun Candidate.fromDto(dto: CandidateDto): Candidate {
    return Candidate(
        id = dto.id,
        photo = dto.photo,
        firstName = dto.firstName,
        lastName = dto.lastName,
        note = dto.note,
        isFavorite = dto.isFavorite,
        phoneNumber = dto.phoneNumber,
        email = dto.email,
        dateOfBirth = dto.dateOfBirth,
        expectedSalary = dto.expectedSalary,
    )
}