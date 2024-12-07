package dev.falkia34.medfinder.infrastructure.repositories

import androidx.datastore.preferences.core.booleanPreferencesKey
import arrow.core.Either
import dev.falkia34.medfinder.di.IODispatcher
import dev.falkia34.medfinder.domain.entities.Failure
import dev.falkia34.medfinder.domain.repositories.PreferencesRepository
import dev.falkia34.medfinder.infrastructure.datasources.DataStoreDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

val ONBOARDING_STATUS_KEY = booleanPreferencesKey("onboarding_status")

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PreferencesRepository {

    override suspend fun getOnboardingStatus(): Either<Failure, Boolean> {
        return withContext(dispatcher) {
            try {
                val result = dataStoreDataSource.get(ONBOARDING_STATUS_KEY)

                if (result != null) {
                    return@withContext Either.Right(result)
                } else {
                    return@withContext Either.Right(false)
                }
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }

    override suspend fun setOnboardingStatus(status: Boolean): Either<Failure, Boolean> {
        return withContext(dispatcher) {
            try {
                dataStoreDataSource.set(ONBOARDING_STATUS_KEY, status)

                return@withContext Either.Right(status)
            } catch (e: Exception) {
                return@withContext Either.Left(Failure.Unknown())
            }
        }
    }
}
