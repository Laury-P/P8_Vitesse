package com.openclassroom.p8_vitesse.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filter = MutableStateFlow("")

    private val _isFavoriteSelected = MutableStateFlow(false)

    /**
     * Flow transmettant la liste de candidat en fonction de l'onglet selectionner et du filtre appliqué
     */
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
        //delay(1000) //pour simuler un chargement et l'observer dans l'UI
    }
        .onEach { _isLoading.value = false }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )

    /**
     * Mise à jour du filtre de recherche
     */
    fun setFilter(newQuery: String) {
        _filter.value = newQuery
    }

    /**
     * Mise à jour du statut de l'onglet favoris
     */
    fun setFavoriteTabSelected(tab: Boolean) {
        _isFavoriteSelected.value = tab
    }
}

