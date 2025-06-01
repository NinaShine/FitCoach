package com.example.fitcoach.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    val api: ExerciseApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApi::class.java)
    }

    val youtubeApi: YouTubeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApi::class.java)
    }

    private val mistralHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val mistralApi: MistralApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://fitcoachspotify.vercel.app")
            .client(mistralHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MistralApi::class.java)
    }
}
