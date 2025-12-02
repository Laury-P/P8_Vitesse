package com.openclassroom.p8_vitesse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.openclassroom.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    @Upsert
    suspend fun upsertCandidate(candidate: CandidateDto)

    @Delete
    suspend fun deleteCandidate(candidate: CandidateDto)

    @Query("SELECT * FROM candidates ORDER BY last_name")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    @Query("SELECT * FROM candidates WHERE id = :id")
    suspend fun getCandidateById(id: Long): CandidateDto

    @Query("SELECT * FROM candidates WHERE is_favorite = :isFavorite ORDER BY last_name")
    fun getFavoriteCandidates(isFavorite: Boolean = true): Flow<List<CandidateDto>>

}