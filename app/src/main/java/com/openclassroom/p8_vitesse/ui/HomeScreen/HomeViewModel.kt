package com.openclassroom.p8_vitesse.ui.HomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val allCandidatesFlow : StateFlow<List<Candidate>> = repository.getAllCandidates()
        .onStart {
            _isLoading.value = true
            delay(1000)} // Permet de simuler un delay de chargement pour voir le loading
        .onEach { _isLoading.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val favoriteCandidatesFlow : StateFlow<List<Candidate>> = repository.getFavoriteCandidates()
        .onStart { _isLoading.value = true }
        .onEach { _isLoading.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

}

