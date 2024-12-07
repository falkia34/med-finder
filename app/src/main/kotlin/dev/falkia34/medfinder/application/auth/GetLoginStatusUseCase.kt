package dev.falkia34.medfinder.application.auth

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.repositories.AuthRepository
import javax.inject.Inject

class GetLoginStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Either<Failure, Boolean> = authRepository.getLoginStatus()
}
