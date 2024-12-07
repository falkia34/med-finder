package dev.falkia34.medfinder.presentation.viewmodels.intro

sealed class IntroState {
    data object First : IntroState()
    data object Second : IntroState()
    data object Third : IntroState()
}
