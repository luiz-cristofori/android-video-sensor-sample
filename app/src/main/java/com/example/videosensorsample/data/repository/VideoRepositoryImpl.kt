package com.example.videosensorsample.data.repository

import com.example.videosensorsample.domain.repository.VideoRepository

class VideoRepositoryImpl : VideoRepository {
    override fun getVideoUrl() =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"

}