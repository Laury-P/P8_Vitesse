package com.openclassroom.p8_vitesse.ui.addScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {

    private val _candidateState = MutableStateFlow(CandidateState())
    val candidateState: StateFlow<CandidateState> = _candidateState

    /**
     * Récupération du candidat par son id
     */
    suspend fun getCandidate(id: Long) {
        val candidate = repository.getCandidateById(id)
        _candidateState.update {
            it.copy(candidate = candidate)
        }
    }

    fun setFirstName(newName: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(firstName = newName))
        }
    }

    fun setLastName(newName: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(lastName = newName))
        }
    }

    fun setPhoneNumber(newPhone: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(phoneNumber = newPhone))
        }
    }

    fun setEmail(newEmail: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(email = newEmail))
        }
    }

    fun setPhoto(newURI: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(photo = newURI))
        }
    }

    fun setNote(newNote: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(note = newNote))
        }
    }

    fun setExpectedSalary(newSalary: String) {
        _candidateState.update {
            it.copy(candidate = it.candidate.copy(expectedSalary = newSalary.toDoubleOrNull()))
        }
    }

    fun setDateOfBirth(newBirthDate: LocalDate) {
        _candidateState.update {
            it.copy(
                candidate = it.candidate.copy(
                    dateOfBirth = newBirthDate
                )
            )
        }
    }

    /**
     * Sauvegarde du candidat dans la base de données si il est validé par checkNecessaryFields()
     */
    fun saveCandidate() {
        checkNecessaryFields()
        if (_candidateState.value.isCandidateCorrect) {
            viewModelScope.launch {
                repository.upsertCandidate(_candidateState.value.candidate)
            }
        }
    }

    /**
     * Vérifie si les champs obligatoires sont remplis et si l'email est valide
     */
    private fun checkNecessaryFields() {
        val emptyError = R.string.mandatoryField
        val emailInvalid = R.string.emailError
        val candidate = _candidateState.value.candidate
        _candidateState.update {
            val firstnameError = if (candidate.firstName.isBlank()) emptyError else null
            val lastnameError = if (candidate.lastName.isBlank()) emptyError else null
            val phoneError = if (candidate.phoneNumber.isBlank()) emptyError else null
            val emailError =
                if (candidate.email.isBlank()) emptyError
                else if (!isEmailValid(candidate.email)) emailInvalid
                else null
            val birthdayError = if (candidate.dateOfBirth == LocalDate.now()) emptyError
            else null
            val isCandidateCorrect =
                firstnameError == null && lastnameError == null && phoneError == null && emailError == null && birthdayError == null
            it.copy(
                firstnameError = firstnameError,
                lastnameError = lastnameError,
                phoneError = phoneError,
                emailError = emailError,
                birthdayError = birthdayError,
                isCandidateCorrect = isCandidateCorrect
            )
        }
    }

    /**
     * Vérifie si l'email est valide
     *
     * @param email L'email à tester
     */
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailRegex.matches(email)
    }
}

data class CandidateState(
    val candidate: Candidate = Candidate(
        id = 0,
        firstName = "",
        lastName = "",
        phoneNumber = "",
        dateOfBirth = LocalDate.now(),
        email = ""
    ),

    val firstnameError: Int? = null,
    val lastnameError: Int? = null,
    val phoneError: Int? = null,
    val emailError: Int? = null,
    val birthdayError: Int? = null,

    val isCandidateCorrect: Boolean = false,
)


