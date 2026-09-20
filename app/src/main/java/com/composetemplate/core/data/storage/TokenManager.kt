package com.composetemplate.core.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }

    suspend fun getToken(): String? = tokenFlow.first()

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
