package com.example.fitcoach.data.model

data class YouTubeResponse(
    val items: List<YouTubeVideo>
)

data class YouTubeVideo(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(val videoId: String)
data class Snippet(
    val title: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val medium: Thumbnail
)

data class Thumbnail(
    val url: String
)
