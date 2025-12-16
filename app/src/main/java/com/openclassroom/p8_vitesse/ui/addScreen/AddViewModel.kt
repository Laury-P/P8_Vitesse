package com.openclassroom.p8_vitesse.ui.addScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.findNavController
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

    private var candidate = Candidate(
        firstName = "",
        lastName = "",
        phoneNumber = "",
        dateOfBirth = LocalDate.now(),
        email = ""
    )

    fun setFirstName(newName: String) {
        candidate.firstName = newName
    }

    fun setLastName(newName: String) {
        candidate.lastName = newName
    }

    fun setPhoneNumber(newPhone: String) {
        candidate.phoneNumber = newPhone
    }

    fun setEmail(newEmail: String) {
        candidate.email = newEmail
    }

    fun setPhoto(newURI: String) {
        candidate.photo = newURI
    }

    fun setNote(newNote: String) {
        candidate.note = newNote
    }

    fun setExpectedSalary(newSalary: String) {
        candidate.expectedSalary = newSalary.toDouble()
    }

    fun setDateOfBirth(newBirthDate: Long) {
        candidate.dateOfBirth =
            Instant.ofEpochMilli(newBirthDate).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun saveCandidate() {
        checkNecessaryFields()
        if (_candidateState.value.isCandidateCorrect) {
            viewModelScope.launch {
                repository.upsertCandidate(candidate)
            }
        }
    }

    private fun checkNecessaryFields() {
        val emptyError = R.string.mandatoryField
        val emailError = R.string.emailError
        _candidateState.update {
            val firstnameError = if (candidate.firstName.isBlank()) emptyError else null
            val lastnameError = if (candidate.lastName.isBlank()) emptyError else null
            val phoneError = if (candidate.phoneNumber.isBlank()) emptyError else null
            val emailError =
                if (candidate.email.isBlank()) emptyError
                /**
                 * else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(candidate.email)
                 *                         .matches()
                 *                 ) emailError
                 * Fonctionne mais bloque les test
                 */
                else if (!isEmailValid(candidate.email)) emailError
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

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailRegex.matches(email)
    }
}

data class CandidateState(
    val firstnameError: Int? = null,
    val lastnameError: Int? = null,
    val phoneError: Int? = null,
    val emailError: Int? = null,
    val birthdayError: Int? = null,

    val isCandidateCorrect: Boolean = false,
)


