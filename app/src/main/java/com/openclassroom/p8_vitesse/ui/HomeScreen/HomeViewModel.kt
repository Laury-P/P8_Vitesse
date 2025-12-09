package com.openclassroom.p8_vitesse.ui.HomeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filter = MutableStateFlow("")

    private val _isFavoriteSelected = MutableStateFlow(false)

    val candidateFlow = combine(
        repository.getAllCandidates(),
        repository.getFavoriteCandidates(),
        _filter,
        _isFavoriteSelected
    ) { listAll, listFavorite, filter, favoriteTabSelected ->
        val list = if (favoriteTabSelected) listFavorite else listAll
        if (filter.isNullOrBlank()) list
        else list.filter { candidate ->
            candidate.lastName.contains(filter, ignoreCase = true) || candidate.firstName.contains(
                filter,
                ignoreCase = true
            )
        }
    }.onStart {
        _isLoading.value = true
    }
        .onEach { _isLoading.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun setFilter(newQuery: String) {
        _filter.value = newQuery
    }

    fun setFavoriteTabSelected(tab: Boolean) {
        _isFavoriteSelected.value = tab
    }
}

