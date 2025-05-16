package com.example.currencyconverter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.example.currencyconverter.data.ExchangeRateApi
import kotlinx.coroutines.launch

@Composable
fun ConversionResultScreen(
    amount: String,
    fromCurrency: String,
    toCurrency: String,
    symbol: String,
    language: String,
    currencyName: String = "",
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var converted by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(amount, fromCurrency, toCurrency) {
        scope.launch {
            try {
                val result = ExchangeRateApi.convertCurrency(amount, fromCurrency, toCurrency)
                converted = result
            } catch (e: Exception) {
                error = "Eroare la conversie: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Text("Converting...", color = Color.Gray)
        } else if (error != null) {
            Text(text = error!!, color = Color.Red, fontSize = 14.sp)
        } else {
            Text(
                text = "Suma introdusă: $amount",
                color = Color.Cyan,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$amount $fromCurrency = $converted $toCurrency",
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDone) {
                Text("OK")
            }
        }
    }
}
