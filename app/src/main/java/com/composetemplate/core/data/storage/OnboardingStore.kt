package com.composetemplate.core.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.composetemplate.core.domain.model.OnboardingPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val CACHE_KEY = stringPreferencesKey("onboarding_cache")
    }

    val completedFlow: Flow<Boolean> = dataStore.data.map { it[COMPLETED_KEY] ?: false }

    suspend fun markCompleted() {
        dataStore.edit { it[COMPLETED_KEY] = true }
    }

    suspend fun cachePages(pages: List<OnboardingPage>) {
        val json = Json.encodeToString(
            ListSerializer(OnboardingPage.serializer()),
            pages
        )
        dataStore.edit { it[CACHE_KEY] = json }
    }

    suspend fun getCachedPages(): List<OnboardingPage> {
        return try {
            val raw = dataStore.data.first()[CACHE_KEY] ?: return emptyList()
            Json.decodeFromString(
                ListSerializer(OnboardingPage.serializer()),
                raw
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
}
