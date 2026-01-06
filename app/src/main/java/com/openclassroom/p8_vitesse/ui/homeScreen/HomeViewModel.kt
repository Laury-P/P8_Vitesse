package com.openclassroom.p8_vitesse.ui.homeScreen

import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.data.repository.CandidateRepository
import com.openclassroom.p8_vitesse.domain.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CandidateRepository) : ViewModel() {

    private val _filter = MutableStateFlow("")

    private val _isFavoriteSelected = MutableStateFlow(false)
    val isFavoriteSelected: StateFlow<Boolean> = _isFavoriteSelected

    /**
     * Flow transmettant la liste de candidat en fonction de l'onglet selectionner et du filtre appliqué
     */
    val candidateFlow : StateFlow<CandidateResult> = combine(
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
    }.map<List<Candidate>, CandidateResult> { list ->
        CandidateResult.Success(list)
    }.catch { e ->
        Log.e("HomeViewModel", "Error while fetching candidates", e)
        emit(CandidateResult.Error(R.string.error_message))
    }.onStart {
        emit(CandidateResult.Loading)
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CandidateResult.Loading
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



