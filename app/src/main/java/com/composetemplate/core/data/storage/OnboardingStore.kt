package com.composetemplate.core.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val completedFlow: Flow<Boolean> = dataStore.data.map { it[COMPLETED_KEY] ?: false }

    suspend fun markCompleted() {
        dataStore.edit { it[COMPLETED_KEY] = true }
    }
}
