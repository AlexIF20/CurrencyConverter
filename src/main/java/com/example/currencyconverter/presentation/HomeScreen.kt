package com.example.currencyconverter.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.example.currencyconverter.data.CountryInfo
import com.example.currencyconverter.data.CountryStore
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onVoiceInputClick: (langCode: String, fromCurrency: String, toCurrency: String, symbol: String) -> Unit
) {
    val context = LocalContext.current
    var showCountrySelector by remember { mutableStateOf(false) }
    var selectingForSource by remember { mutableStateOf(true) }

    var allCountries by remember { mutableStateOf<List<CountryInfo>>(emptyList()) }
    val scope = rememberCoroutineScope()

    var sourceCountry by remember { mutableStateOf<CountryInfo?>(null) }
    var targetCountry by remember { mutableStateOf<CountryInfo?>(null) }

    LaunchedEffect(Unit) {
        allCountries = loadCountriesFromAssets(context)
        val (savedSource, savedTarget) = CountryStore.getSelectedCountries(context)
        sourceCountry = allCountries.find { it.code == savedSource } ?: allCountries.find { it.code == "US" }
        targetCountry = allCountries.find { it.code == savedTarget } ?: allCountries.find { it.code == "RO" }
    }

    if (sourceCountry == null || targetCountry == null) {
        Text("Loading...", color = Color.White)
        return
    }

    if (showCountrySelector) {
        CountrySelectorScreen { selected ->
            if (selectingForSource) {
                sourceCountry = selected
            } else {
                targetCountry = selected
            }
            scope.launch {
                CountryStore.saveSelectedCountries(context, sourceCountry!!.code, targetCountry!!.code)
            }
            showCountrySelector = false
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 0.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Țara sursă
            CompactChip(
                label = {
                    Text(
                        sourceCountry!!.name,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onClick = {
                    selectingForSource = true
                    showCountrySelector = true
                },
                modifier = Modifier
                    .padding(vertical = 0.dp)
            )

            // Buton swap
            Button(
                onClick = {
                    val temp = sourceCountry
                    sourceCountry = targetCountry
                    targetCountry = temp
                    scope.launch {
                        CountryStore.saveSelectedCountries(context, sourceCountry!!.code, targetCountry!!.code)
                    }
                },
                modifier = Modifier
                    .padding(vertical = 0.dp)
                    .size(20.dp)
            ) {
                Text("↕", fontSize = 14.sp, textAlign = TextAlign.Center)
            }

            // Țara destinație
            CompactChip(
                label = {
                    Text(
                        targetCountry!!.name,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onClick = {
                    selectingForSource = false
                    showCountrySelector = true
                },
                modifier = Modifier
                    .padding(vertical = 0.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Voice input
            CompactChip(
                label = {
                    Text(
                        "\uD83C\uDFA4 Voice Input",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onClick = {
                    onVoiceInputClick(
                        sourceCountry!!.language.code,
                        sourceCountry!!.currency.code,
                        targetCountry!!.currency.code,
                        targetCountry!!.currency.symbol
                    )
                },
                modifier = Modifier
                    .padding(vertical = 0.dp)
            )
        }
    }
}
