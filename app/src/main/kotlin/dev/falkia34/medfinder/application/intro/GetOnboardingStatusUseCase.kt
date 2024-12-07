package dev.falkia34.medfinder.application.intro

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.repositories.PreferencesRepository
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(): Either<Failure, Boolean> =
        preferencesRepository.getOnboardingStatus()
}
