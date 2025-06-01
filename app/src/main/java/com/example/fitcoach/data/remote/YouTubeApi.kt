package com.example.fitcoach.data.remote

import com.example.fitcoach.data.model.YouTubeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    @GET("search")
    suspend fun searchVideosByChannelAndQuery(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeResponse


    @GET("playlistItems")
    suspend fun getVideosFromPlaylist(
        @Query("part") part: String = "snippet",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeResponse
}
