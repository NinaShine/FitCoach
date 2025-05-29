package com.example.fitcoach.data.remote

import com.example.fitcoach.data.model.Exercise
import retrofit2.http.GET
import retrofit2.http.Headers

interface ExerciseApi {
    @Headers(
        "X-RapidAPI-Key: 6a19a4ceb3msh0d83f50af546268p1c29f1jsnbcfc3e59ed7c\n",
        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    )
    @GET("exercises")
    suspend fun getAllExercises(): List<Exercise>
}
