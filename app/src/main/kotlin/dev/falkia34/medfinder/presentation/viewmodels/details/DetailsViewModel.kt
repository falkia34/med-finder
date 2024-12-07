package dev.falkia34.medfinder.presentation.viewmodels.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.falkia34.medfinder.application.plant.DeletePlantUseCase
import dev.falkia34.medfinder.application.plant.GetPlantUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getPlantUseCase: GetPlantUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
) : ViewModel() {
    private val _detailsState = MutableStateFlow<DetailsState>(DetailsState.Idle)
    val detailsState: StateFlow<DetailsState> get() = _detailsState

    fun load(id: String) {
        viewModelScope.launch {
            _detailsState.value = DetailsState.Loading

            when (val result = getPlantUseCase(id)) {
                is Either.Left -> _detailsState.value =
                    DetailsState.Error("Failed to load plant details!")

                is Either.Right -> _detailsState.value = DetailsState.Success(result.value)
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            when (deletePlantUseCase(id)) {
                is Either.Left -> _detailsState.value =
                    DetailsState.Error("Failed to delete plant!")

                is Either.Right -> _detailsState.value = DetailsState.Deleted
            }
        }
    }
}
