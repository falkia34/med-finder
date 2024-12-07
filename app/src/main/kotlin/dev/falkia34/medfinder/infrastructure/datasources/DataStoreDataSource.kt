package dev.falkia34.medfinder.infrastructure.datasources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreDataSource @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>
) {
    suspend fun <T> get(key: Preferences.Key<T>): T? {
        return preferencesDataStore.data.map { preferences ->
            preferences[key]
        }.first()
    }

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        preferencesDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> remove(key: Preferences.Key<T>) {
        preferencesDataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
