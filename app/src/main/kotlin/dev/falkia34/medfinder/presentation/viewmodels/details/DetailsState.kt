package dev.falkia34.medfinder.presentation.viewmodels.details

import dev.falkia34.medfinder.domain.entities.Plant

sealed class DetailsState {
    data object Idle : DetailsState()
    data object Loading : DetailsState()
    data class Success(val plant: Plant) : DetailsState()
    data object Deleted : DetailsState()
    data class Error(val message: String) : DetailsState()
}
