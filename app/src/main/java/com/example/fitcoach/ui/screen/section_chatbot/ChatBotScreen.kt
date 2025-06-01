package com.example.fitcoach.ui.screen.section_chatbot

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*
import com.example.fitcoach.data.remote.askMistral
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle


@Composable
fun ChatBotScreen(navController: NavController) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf(TextFieldValue()) }
    val messages = remember { mutableStateListOf<Pair<String, Boolean>>() }
    var isLoading by remember { mutableStateOf(false) }
    var speakerEnabled by remember { mutableStateOf(true) }


    val listState = rememberLazyListState()
    val tts = remember {
        TextToSpeech(context, null)
    }


    LaunchedEffect(tts) {
        val result = tts.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "Langue non supportée")
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!spokenText.isNullOrBlank()) {
            userInput = TextFieldValue(spokenText)
        }
    }

    fun speak(text: String) {
        if (text.isNotBlank()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ask FitBot a question", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFFFF0E5), RoundedCornerShape(12.dp))
                .padding(12.dp),
            state = listState

        ) {
            items(messages) { message ->
                val msg = message.first
                val isBot = message.second
                RenderMarkdownText(msg, isBot)
                Spacer(modifier = Modifier.height(8.dp))

            }
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("e.g., How to build muscle?") }
            )

            IconButton(onClick = {
                if (userInput.text.isNotBlank()) {
                    messages.add(userInput.text to false)
                    isLoading = true
                    askMistral(context, userInput.text) { response ->
                        isLoading = false
                        messages.add(response to true)
                        if (speakerEnabled) {
                            speak(response)
                        }
                    }
                    userInput = TextFieldValue("")
                } else {
                    Toast.makeText(context, "Enter a question", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            OutlinedButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    }
                    voiceLauncher.launch(intent)
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text("Speak")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Speaker:")
                Spacer(modifier = Modifier.height(8.dp))
                Switch(
                    checked = speakerEnabled,
                    onCheckedChange = { speakerEnabled = it }
                )
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }
}

@Composable
fun RenderMarkdownText(text: String, isBot: Boolean) {
    val lines = text.lines()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isBot) Color(0xFFFFF0E5) else Color(0xFFD3F0FF), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        for (line in lines) {
            when {
                line.startsWith("###") -> MarkdownText(line.removePrefix("### "), MaterialTheme.typography.bodySmall)
                line.startsWith("##") -> MarkdownText(line.removePrefix("## "), MaterialTheme.typography.bodyMedium)
                line.startsWith("#") -> MarkdownText(line.removePrefix("# "), MaterialTheme.typography.titleLarge)
                line.startsWith("- ") || line.startsWith("* ") -> MarkdownText("• " + line.drop(2), MaterialTheme.typography.bodyMedium)
                line.startsWith("> ") -> MarkdownText(line.drop(2), MaterialTheme.typography.bodyMedium)
                else -> MarkdownText(line.trim(), MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
fun MarkdownText(line: String, style: androidx.compose.ui.text.TextStyle) {
    Text(
        text = buildAnnotatedString {
            val regexBoldItalic = Regex("""\*\*\*(.*?)\*\*\*""")
            val regexBold = Regex("""\*\*(.*?)\*\*""")
            val regexItalic = Regex("""\*(.*?)\*""")

            var remaining = line
            while (remaining.isNotEmpty()) {
                when {
                    regexBoldItalic.containsMatchIn(remaining) -> {
                        val match = regexBoldItalic.find(remaining)!!
                        append(remaining.substringBefore(match.value))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                            append(match.groupValues[1])
                        }
                        remaining = remaining.substringAfter(match.value)
                    }

                    regexBold.containsMatchIn(remaining) -> {
                        val match = regexBold.find(remaining)!!
                        append(remaining.substringBefore(match.value))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(match.groupValues[1])
                        }
                        remaining = remaining.substringAfter(match.value)
                    }

                    regexItalic.containsMatchIn(remaining) -> {
                        val match = regexItalic.find(remaining)!!
                        append(remaining.substringBefore(match.value))
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(match.groupValues[1])
                        }
                        remaining = remaining.substringAfter(match.value)
                    }

                    else -> {
                        append(remaining)
                        break
                    }
                }
            }
        },
        style = style
    )
}





