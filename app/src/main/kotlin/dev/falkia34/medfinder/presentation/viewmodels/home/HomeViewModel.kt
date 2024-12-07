package dev.falkia34.medfinder.presentation.viewmodels.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.falkia34.medfinder.application.auth.LogoutUseCase
import dev.falkia34.medfinder.application.plant.CountPlantsUseCase
import dev.falkia34.medfinder.application.plant.DeletePlantUseCase
import dev.falkia34.medfinder.application.plant.GetPlantsUseCase
import dev.falkia34.medfinder.domain.entities.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val countPlantsUseCase: CountPlantsUseCase,
    private val getPlantsUseCase: GetPlantsUseCase,
    private val deletePlantUseCase: DeletePlantUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _cursor = MutableLiveData<String?>(null)
    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Idle)
    val plants: StateFlow<List<Plant>> = _plants
    val homeState: StateFlow<HomeState> get() = _homeState

    init {
        loadMore()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun loadMore() {
        _homeState.value = HomeState.Loading

        viewModelScope.launch {
            val count = countPlantsUseCase()
            val plants = getPlantsUseCase(_cursor.value)

            when {
                count is Either.Right && plants is Either.Right -> {
                    _plants.value += plants.value

                    if (_plants.value.size < count.value) {
                        _homeState.value = HomeState.Page
                    } else {
                        _homeState.value = HomeState.LastPage
                    }

                    if (_plants.value.isNotEmpty()) {
                        _cursor.value = _plants.value.last().id
                    }
                }

                else -> _homeState.value = HomeState.Error("Failed to load plants!")
            }
        }
    }

    fun refresh() {
        _homeState.value = HomeState.Loading
        _plants.value = emptyList()
        _cursor.value = null

        loadMore()
    }

    fun clear() {
        _homeState.value = HomeState.Idle
        _plants.value = emptyList()
        _cursor.value = null
    }

    fun delete(index: Int) {
        viewModelScope.launch {
            deletePlantUseCase(_plants.value[index].id)

            _plants.value = mutableListOf(_plants.value).removeAt(index)
        }
    }
}
