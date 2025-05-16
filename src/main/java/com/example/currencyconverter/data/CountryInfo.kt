package com.example.currencyconverter.data

data class CountryInfo(
    val name: String,
    val code: String,
    val capital: String,
    val region: String,
    val currency: Currency,
    val language: Language
)

data class Currency(
    val code: String,
    val name: String,
    val symbol: String
)

data class Language(
    val code: String,
    val name: String
)
