package com.composetemplate.core.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderTrackingStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LAST_STATUS_KEY = stringPreferencesKey("last_order_status_map")
    }

    suspend fun getStatusMap(): Map<String, String> {
        val raw = dataStore.data.first()[LAST_STATUS_KEY] ?: ""
        if (raw.isBlank()) return emptyMap()
        return raw.split("|").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
    }

    suspend fun saveStatusMap(map: Map<String, String>) {
        val raw = map.entries.joinToString("|") { "${it.key}:${it.value}" }
        dataStore.edit { it[LAST_STATUS_KEY] = raw }
    }
}
