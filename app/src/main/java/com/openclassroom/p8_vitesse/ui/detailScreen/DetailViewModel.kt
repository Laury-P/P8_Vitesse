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

    suspend fun getCandidateById(id: Long) {
        val candidate = repository.getCandidateById(id)
        candidate.id?.let {
            _candidateFlow.update {
                it.copy(
                    id = candidate.id,
                    firstName = candidate.firstName,
                    lastName = candidate.lastName,
                    phoneNumber = candidate.phoneNumber,
                    email = candidate.email,
                    photo = candidate.photo,
                    note = candidate.note,
                    expectedSalary = candidate.expectedSalary,
                    dateOfBirth = candidate.dateOfBirth,
                    isFavorite = candidate.isFavorite
                )
            }
        }
    }

    fun setFavorite() {
        val newFavoriteValue = !candidateFlow.value.isFavorite
        _candidateFlow.update {
            it.copy(isFavorite = newFavoriteValue)
        }
        viewModelScope.launch {
            repository.upsertCandidate(candidateBuilder())
        }
    }

    fun deleteCandidate(){
        viewModelScope.launch {
            repository.deleteCandidate(candidateBuilder())
        }
    }

    private fun candidateBuilder() : Candidate{
        return Candidate(
            id = candidateFlow.value.id,
            firstName = candidateFlow.value.firstName,
            lastName = candidateFlow.value.lastName,
            phoneNumber = candidateFlow.value.phoneNumber,
            email = candidateFlow.value.email,
            photo = candidateFlow.value.photo,
            note = candidateFlow.value.note,
            expectedSalary = candidateFlow.value.expectedSalary,
            dateOfBirth = candidateFlow.value.dateOfBirth,
            isFavorite = candidateFlow.value.isFavorite
        )
    }
}

data class CandidateDetail(
    val id: Long = -1,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val photo: String? = null,
    val note: String? = null,
    val expectedSalary: Double? = null,
    val dateOfBirth: LocalDate = LocalDate.now(),
    val isFavorite: Boolean = false
)