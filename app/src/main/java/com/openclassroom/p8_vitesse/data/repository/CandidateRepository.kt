package com.openclassroom.p8_vitesse.data.repository

import com.openclassroom.p8_vitesse.data.local.dao.CandidateDao
import com.openclassroom.p8_vitesse.data.mapper.toDomain
import com.openclassroom.p8_vitesse.data.mapper.toDto
import com.openclassroom.p8_vitesse.data.remote.api.ApiService
import com.openclassroom.p8_vitesse.domain.Candidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CandidateRepository @Inject constructor(private val candidateDao: CandidateDao, private val apiService: ApiService) {

    suspend fun upsertCandidate(candidate: Candidate) {
        candidateDao.upsertCandidate(candidate.toDto())
    }


    suspend fun deleteCandidate(candidate: Candidate) {
        candidateDao.deleteCandidate(candidate.toDto())
    }

    fun getAllCandidates(): Flow<List<Candidate>> {
        return candidateDao.getAllCandidates()
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }
    }

    suspend fun getCandidateById(id: Long): Candidate {
        return candidateDao.getCandidateById(id).toDomain()
    }

    fun getFavoriteCandidates(): Flow<List<Candidate>> {
        return candidateDao.getFavoriteCandidates()
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }
    }

    suspend fun getEurToPoundsRate(): Double {
        val response = apiService.getEuroToPoundsRate()
        val rate = response.eurRate.euroToPoundsRate
        return rate
    }


}