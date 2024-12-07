package dev.falkia34.medfinder.application.auth

import android.content.Context
import arrow.core.Either
import com.google.firebase.auth.AuthResult
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(context: Context): Either<Failure, AuthResult> =
        authRepository.loginWithGoogle(context)
}
