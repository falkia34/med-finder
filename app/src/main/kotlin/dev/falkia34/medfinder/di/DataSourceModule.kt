package dev.falkia34.medfinder.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.falkia34.medfinder.infrastructure.datasources.DataStoreDataSource
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDataStoreDataSource(
        dataStore: DataStore<Preferences>
    ): DataStoreDataSource {
        return DataStoreDataSource(dataStore)
    }

    @Provides
    @Singleton
    fun provideOpenAIService(): OpenAI {
        return OpenAI(
            token = dev.falkia34.medfinder.BuildConfig.OPENAI_API_KEY,
            timeout = Timeout(socket = 60.seconds),
        )
    }
}