# CurrencyConverter – Wear OS Application

CurrencyConverter is a standalone smartwatch app developed in Kotlin using Android Studio and Jetpack Compose.  
It enables fast and intuitive currency conversion using voice input, allowing users to select two countries, speak an amount, and instantly receive the converted result on screen.

The app automatically determines the correct language and currency based on the selected countries and provides a seamless voice-to-result flow using modern APIs and asynchronous architecture.

## Features
- Voice-based input using Whisper API
- Automatic extraction of spoken numbers with ChatGPT
- Real-time currency conversion via ExchangeRate API
- Text-to-speech output for conversion results
- Persistent country selection using DataStore
- Built entirely with Jetpack Compose for Wear OS

## Technologies
- Kotlin & Android Studio
- Jetpack Compose for Wear OS
- Kotlin Coroutines for async operations
- OkHttp for API requests
- Whisper & ChatGPT APIs (OpenAI)
- ExchangeRate API
- DataStore Preferences

