package com.example.fitcoach.data.remote

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


fun askMistral(context: Context, message: String, onResponse: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.mistralApi.askMistral(message = message)
            if (response.isSuccessful) {
                val body = response.body()?.string()
                val json = JSONObject(body ?: "")
                val content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                Handler(Looper.getMainLooper()).post {
                    onResponse(content)
                }
            } else {
                Log.e("Mistral", "❌ Erreur API Retrofit : ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Mistral", "❌ Exception Retrofit : ${e.message}")
        }
    }
}

