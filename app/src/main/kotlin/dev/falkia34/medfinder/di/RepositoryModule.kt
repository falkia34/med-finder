package dev.falkia34.medfinder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.falkia34.medfinder.domain.repositories.AuthRepository
import dev.falkia34.medfinder.domain.repositories.OpenAIRepository
import dev.falkia34.medfinder.domain.repositories.PlantRepository
import dev.falkia34.medfinder.domain.repositories.PreferencesRepository
import dev.falkia34.medfinder.infrastructure.repositories.AuthRepositoryImpl
import dev.falkia34.medfinder.infrastructure.repositories.OpenAIRepositoryImpl
import dev.falkia34.medfinder.infrastructure.repositories.PlantRepositoryImpl
import dev.falkia34.medfinder.infrastructure.repositories.PreferencesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl,
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindPlantRepository(
        plantRepositoryImpl: PlantRepositoryImpl,
    ): PlantRepository

    @Binds
    @Singleton
    abstract fun bindOpenAIRepository(
        openAIRepositoryImpl: OpenAIRepositoryImpl,
    ): OpenAIRepository
}
