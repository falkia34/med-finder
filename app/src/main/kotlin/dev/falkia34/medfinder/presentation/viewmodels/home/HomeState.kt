package dev.falkia34.medfinder.presentation.viewmodels.home

sealed class HomeState {
    data object Idle : HomeState()
    data object Loading : HomeState()
    data object Page : HomeState()
    data object LastPage : HomeState()
    data class Error(val message: String) : HomeState()
}
