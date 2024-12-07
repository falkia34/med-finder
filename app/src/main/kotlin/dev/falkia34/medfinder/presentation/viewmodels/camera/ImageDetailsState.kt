package dev.falkia34.medfinder.presentation.viewmodels.camera

import dev.falkia34.medfinder.domain.entities.Plant

sealed class ImageDetailsState {
    data object Idle : ImageDetailsState()
    data object Loading : ImageDetailsState()
    data class Success(val plant: Plant) : ImageDetailsState()
    data class Error(val message: String) : ImageDetailsState()
}
