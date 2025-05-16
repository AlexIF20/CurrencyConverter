package com.example.currencyconverter.presentation

import android.content.Context
import android.media.MediaRecorder
import android.widget.Toast
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
import com.example.currencyconverter.data.ChatGptApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun SpeechToTextScreen(
    selectedLanguage: String = "en",
    onResult: (whisper: String, chatNumber: String, number: String) -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var whisperText by remember { mutableStateOf("") }
    var chatGptNumber by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    val audioFile = remember { File(context.cacheDir, "recorded_audio.mp3") }
    val scope = rememberCoroutineScope()

    val startRecording = {
        try {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            whisperText = ""
            chatGptNumber = ""
            errorText = ""
            isRecording = true
        } catch (e: Exception) {
            Toast.makeText(context, "Recording error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val stopRecordingAndTranscribe = {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            // Așteptăm puțin să ne asigurăm că fișierul e scris
            Thread.sleep(500)
            scope.launch {
                // 1. Transcriere cu Whisper
                val whisper = transcribeAudioWithWhisper(context, audioFile, selectedLanguage)
                whisperText = whisper.trim()
                errorText = ""
                // 2. Dacă Whisper returnează direct un număr (max 12 cifre, cifre și punct), trecem mai departe
                val extracted = extractNumber(whisperText)
                if (extracted != null && extracted.length <= 12) {
                    chatGptNumber = ""
                    errorText = ""
                    onResult(whisperText, "", extracted)
                } else {
                    // 3. Altfel trimitem la ChatGPT
                    val chatNum = ChatGptApi.extractNumberOnly(whisperText, selectedLanguage)
                    chatGptNumber = chatNum.trim()
                    // Validare să nu fie frază sau aiurea
                    if (chatGptNumber.matches(Regex("^\\d+(\\.\\d{1,2})?$")) && chatGptNumber.length <= 12) {
                        errorText = ""
                        onResult(whisperText, chatGptNumber, chatGptNumber)
                    } else {
                        errorText = "Nu s-a detectat un număr valid!"
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Stop error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isRecording) "Recording" else "Tap to Record",
            color = Color.Yellow,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        if (whisperText.isNotBlank()) {
            Text(
                text = "Whisper: $whisperText",
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        if (chatGptNumber.isNotBlank()) {
            Text(
                text = "ChatGPT: $chatGptNumber",
                color = Color.Cyan,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        if (errorText.isNotBlank()) {
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            if (isRecording) stopRecordingAndTranscribe() else startRecording()
        }) {
            Text(if (isRecording) "Stop" else "Start")
        }
    }
}


suspend fun transcribeAudioWithWhisper(context: Context, file: File, language: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val client = okhttp3.OkHttpClient()
            val mediaType = "audio/mp3".toMediaTypeOrNull()

            val body = okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody(mediaType))
                .addFormDataPart("model", "whisper-1")
                .addFormDataPart("language", language)
                .build()

            val request = okhttp3.Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .addHeader("Authorization", "YOUR-API-KEY")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext "Eroare API: ${response.code} – $responseBody"
            }

            val json = org.json.JSONObject(responseBody)
            return@withContext json.getString("text")

        } catch (e: Exception) {
            return@withContext "Eroare în aplicație: ${e.message}"
        }
    }
}

fun extractNumber(text: String): String? {
    val regex = Regex("""\d+(\.\d+)?""")
    return regex.find(text)?.value
}
