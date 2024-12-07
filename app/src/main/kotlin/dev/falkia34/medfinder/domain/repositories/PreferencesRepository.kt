package dev.falkia34.medfinder.domain.repositories

import arrow.core.Either
import dev.falkia34.medfinder.domain.entities.Failure

interface PreferencesRepository {
    suspend fun getOnboardingStatus(): Either<Failure, Boolean>

    suspend fun setOnboardingStatus(status: Boolean): Either<Failure, Boolean>
}
