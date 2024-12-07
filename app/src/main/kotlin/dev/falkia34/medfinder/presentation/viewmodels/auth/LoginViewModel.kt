package dev.falkia34.medfinder.presentation.viewmodels.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.falkia34.medfinder.application.auth.LoginWithGoogleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (loginWithGoogleUseCase(context)) {
                is Either.Left -> _loginState.value = LoginState.Error("Login failed!")
                is Either.Right -> _loginState.value = LoginState.Success("Logged in successfully!")
            }
        }
    }
}
