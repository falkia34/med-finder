package dev.falkia34.medfinder.presentation.viewmodels.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.falkia34.medfinder.application.openai.GetImageDetailsUseCase
import dev.falkia34.medfinder.application.plant.AddPlantUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val getImageDetailsUseCase: GetImageDetailsUseCase,
    private val addPlantUseCase: AddPlantUseCase,
) : ViewModel() {
    private val _imageDetailsState = MutableStateFlow<ImageDetailsState>(ImageDetailsState.Idle)
    val imageDetailsState: StateFlow<ImageDetailsState> get() = _imageDetailsState

    fun scanImage(image: String) {
        viewModelScope.launch {
            _imageDetailsState.value = ImageDetailsState.Loading

            val imageDetails = getImageDetailsUseCase(image)

            when (imageDetails) {
                is Either.Left -> {
                    _imageDetailsState.value = ImageDetailsState.Error(imageDetails.value.message)
                    return@launch
                }

                else -> {}
            }

            when (val plant = addPlantUseCase((imageDetails as Either.Right).value)) {
                is Either.Right -> _imageDetailsState.value = ImageDetailsState.Success(plant.value)
                is Either.Left -> _imageDetailsState.value =
                    ImageDetailsState.Error("Failed to save image!")
            }
        }
    }
}
