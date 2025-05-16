package com.example.currencyconverter.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "country_preferences")

object CountryStore {
    private val SOURCE_KEY = stringPreferencesKey("source_country_code")
    private val TARGET_KEY = stringPreferencesKey("target_country_code")

    suspend fun saveSelectedCountries(context: Context, sourceCode: String, targetCode: String) {
        context.dataStore.edit { prefs ->
            prefs[SOURCE_KEY] = sourceCode
            prefs[TARGET_KEY] = targetCode
        }
    }

    suspend fun getSelectedCountries(context: Context): Pair<String?, String?> {
        val prefs = context.dataStore.data.first()
        return Pair(prefs[SOURCE_KEY], prefs[TARGET_KEY])
    }
}
