package com.example.aula04_fragments

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        private val LIST_NAMES_KEY = stringSetPreferencesKey("list_names")
    }

    val listNames: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[LIST_NAMES_KEY] ?: emptySet()
        }

    suspend fun addListName(name: String) {
        context.dataStore.edit { preferences ->
            val currentNames = preferences[LIST_NAMES_KEY] ?: emptySet()
            preferences[LIST_NAMES_KEY] = currentNames + name
        }
    }
}
