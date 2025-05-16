package com.example.currencyconverter.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import com.example.currencyconverter.data.CountryInfo
import com.example.currencyconverter.data.Currency
import com.example.currencyconverter.data.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.InputStream

@Composable
fun CountrySelectorScreen(
    onCountrySelected: (CountryInfo) -> Unit
) {
    val context = LocalContext.current
    var countries by remember { mutableStateOf(listOf<CountryInfo>()) }

    LaunchedEffect(true) {
        countries = loadCountriesFromAssets(context)
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize()
                             .background(Color.Black),
        contentPadding = PaddingValues(8.dp),
        state = ScalingLazyListState()
    ) {
        items(countries.size) { index ->
            val country = countries[index]
            Text(
                text = country.name,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onCountrySelected(country) }
            )
        }
    }
}

suspend fun loadCountriesFromAssets(context: Context): List<CountryInfo> {
    return withContext(Dispatchers.IO) {
        val json = context.assets.open("countries.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.getJSONObject(i)
            CountryInfo(
                name = obj.getString("name"),
                code = obj.getString("code"),
                capital = obj.getString("capital"),
                region = obj.getString("region"),
                currency = Currency(
                    code = obj.getJSONObject("currency").getString("code"),
                    name = obj.getJSONObject("currency").getString("name"),
                    symbol = obj.getJSONObject("currency").getString("symbol")
                ),
                language = Language(
                    code = obj.getJSONObject("language").getString("code"),
                    name = obj.getJSONObject("language").getString("name")
                )
            )
        }
    }
}
