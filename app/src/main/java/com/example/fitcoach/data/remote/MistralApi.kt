package com.example.fitcoach.data.remote


import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MistralApi {
    @GET("/")
    suspend fun askMistral(
        @Query("provider") provider: String = "mistral",
        @Query("message") message: String
    ): Response<ResponseBody>
}

