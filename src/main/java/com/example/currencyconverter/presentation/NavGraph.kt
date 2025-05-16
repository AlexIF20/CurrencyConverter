package com.example.currencyconverter.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen { langCode, fromCurrency, toCurrency, symbol ->
                navController.navigate("speech/$langCode/$fromCurrency/$toCurrency/$symbol")
            }
        }
        composable(
            route = "speech/{lang}/{from}/{to}/{symbol}",
            arguments = listOf(
                navArgument("lang") { defaultValue = "en" },
                navArgument("from") { defaultValue = "USD" },
                navArgument("to") { defaultValue = "RON" },
                navArgument("symbol") { defaultValue = "lei" }
            )
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("lang") ?: "en"
            val from = backStackEntry.arguments?.getString("from") ?: "USD"
            val to = backStackEntry.arguments?.getString("to") ?: "RON"
            val symbol = backStackEntry.arguments?.getString("symbol") ?: "lei"

            SpeechToTextScreen(
                selectedLanguage = lang,
                onResult = { whisper, chatNumber, number ->
                    navController.navigate("convert/$number/$from/$to/$symbol/$lang")
                }
            )

        }
        composable(
            route = "convert/{amount}/{from}/{to}/{symbol}/{lang}",
            arguments = listOf(
                navArgument("amount") { defaultValue = "1.0" },
                navArgument("from") { defaultValue = "USD" },
                navArgument("to") { defaultValue = "RON" },
                navArgument("symbol") { defaultValue = "lei" },
                navArgument("lang") { defaultValue = "ro" }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "1.0"
            val from = backStackEntry.arguments?.getString("from") ?: "USD"
            val to = backStackEntry.arguments?.getString("to") ?: "RON"
            val symbol = backStackEntry.arguments?.getString("symbol") ?: "lei"
            val lang = backStackEntry.arguments?.getString("lang") ?: "ro"

            ConversionResultScreen(
                amount = amount,
                fromCurrency = from,
                toCurrency = to,
                symbol = symbol,
                language = lang,
                onDone = { navController.popBackStack("home", false) }
            )
        }
    }
}
