package dev.falkia34.medfinder.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.falkia34.medfinder.R
import dev.falkia34.medfinder.application.auth.GetLoginStatusUseCase
import dev.falkia34.medfinder.application.intro.GetOnboardingStatusUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLoginStatusUseCase: GetLoginStatusUseCase,
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
) : ViewModel() {
    private val _startDestination = MutableSharedFlow<Int>()
    val startDestination: SharedFlow<Int> get() = _startDestination

    fun getStartDestination() {
        viewModelScope.launch {
            val loginStatus = getLoginStatusUseCase()
            val onboardingStatus = getOnboardingStatusUseCase()

            when {
                loginStatus is Either.Right && onboardingStatus is Either.Right -> {
                    if (!onboardingStatus.value) _startDestination.emit(R.id.nav_fragment_intro)
                    if (onboardingStatus.value && !loginStatus.value) _startDestination.emit(R.id.nav_fragment_login)
                    if (onboardingStatus.value && loginStatus.value) _startDestination.emit(R.id.navigation_mobile_home)
                }

                else -> {}
            }
        }
    }
}
