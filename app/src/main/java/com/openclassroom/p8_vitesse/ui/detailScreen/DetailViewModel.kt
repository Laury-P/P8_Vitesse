package com.openclassroom.p8_vitesse.ui.detailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: CandidateRepository) :
    ViewModel() {
    private val _candidateFlow = MutableStateFlow(CandidateDetail())
    val candidateFlow: StateFlow<CandidateDetail> = _candidateFlow

    /**
     * Récupération du taux de conversion euro → livre depuis le repository
     *
     * @return Double correspendant au Taux de conversion euro → livre
     */
    suspend fun getRate(): Double {
        return repository.getEurToPoundsRate()
    }

    /**
     * Récupération du candidat par son id
     *
     * @param id L'id du candidat
     */
    suspend fun getCandidateById(id: Long) {
        val candidate = repository.getCandidateById(id)
        candidate.id?.let {
            _candidateFlow.update {
                it.copy(
                    candidate = candidate
                )
            }
        }
    }

    /**
     * Mise à jour du statut favorite du candidat dans la BDD et dans le candidateFlow
     */
    fun setFavorite() {
        val newFavoriteValue = !candidateFlow.value.candidate.isFavorite
        _candidateFlow.update {
            val updatedCandidate = it.candidate.copy(isFavorite = newFavoriteValue)
            it.copy(candidate = updatedCandidate)
        }
        viewModelScope.launch {
            repository.upsertCandidate(candidateFlow.value.candidate)
        }
    }

    /**
     * Suppression du candidat de la BDD
     */
    fun deleteCandidate() {
        viewModelScope.launch {
            repository.deleteCandidate(candidateFlow.value.candidate)
        }
    }

}

data class CandidateDetail(
    var candidate: Candidate = Candidate(
        id = -1,
        firstName = "",
        lastName = "",
        phoneNumber = "",
        email = "",
        photo = null,
        note = null,
        expectedSalary = null,
        dateOfBirth = LocalDate.now(),
        isFavorite = false
    )
)


