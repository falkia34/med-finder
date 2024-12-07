package dev.falkia34.medfinder.presentation.viewmodels.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.falkia34.medfinder.application.auth.GetLoginStatusUseCase
import dev.falkia34.medfinder.application.intro.SetOnboardingStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val setOnboardingStatusUseCase: SetOnboardingStatusUseCase,
) : ViewModel() {
    private val _loginStatusState = MutableStateFlow<LoginStatusState>(LoginStatusState.Idle)
    private val _introState = MutableStateFlow(0)
    val loginStatusState: StateFlow<LoginStatusState> get() = _loginStatusState
    val introState: StateFlow<Int> get() = _introState

    fun checkLoginStatus() {
        viewModelScope.launch {
            when (val loginStatus = getLoginStatusUseCase()) {
                is Either.Right -> when (loginStatus.value) {
                    true -> _loginStatusState.value = LoginStatusState.LoggedIn
                    false -> _loginStatusState.value = LoginStatusState.LoggedOut
                }

                else -> {}
            }
        }
    }

    fun setCurrentItem(index: Int) {
        when (index) {
            0, 1, 2 -> _introState.value = index
        }

    }

    fun finishOnboarding() {
        viewModelScope.launch {
            setOnboardingStatusUseCase(true)
        }
    }
}
