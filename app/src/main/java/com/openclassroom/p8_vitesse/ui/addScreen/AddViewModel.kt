package com.openclassroom.p8_vitesse.ui.addScreen

import androidx.lifecycle.ViewModel
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {
    private var candidate = Candidate(
        firstName = " ",
        lastName = " ",
        phoneNumber = " ",
        dateOfBirth = LocalDate.now(),
        email = " "
    )

    fun setPhoto(newURI: String){
        candidate.photo = newURI
    }


}