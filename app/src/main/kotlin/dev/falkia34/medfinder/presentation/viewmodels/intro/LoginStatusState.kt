package dev.falkia34.medfinder.presentation.viewmodels.intro

sealed class LoginStatusState {
    data object Idle : LoginStatusState()
    data object LoggedOut : LoginStatusState()
    data object LoggedIn : LoginStatusState()
    data class Error(val message: String) : LoginStatusState()
}
