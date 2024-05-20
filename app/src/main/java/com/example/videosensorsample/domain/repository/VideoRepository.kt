package com.example.videosensorsample.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getVideoUrl(): String
    fun getLocationUpdates(): Flow<Location>
}
