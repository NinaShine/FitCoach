package com.example.fitcoach.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ExerciseApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApi::class.java)
    }
}
