package com.openclassroom.p8_vitesse.ui.homeScreen

import com.openclassroom.p8_vitesse.domain.Candidate

sealed class CandidateResult {
    object Loading : CandidateResult()
    data class Success(val candidates: List<Candidate>) : CandidateResult()
    data class Error(val errorMessage: Int) : CandidateResult()
}