package com.example.videosensorsample.data.repository

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.example.videosensorsample.domain.repository.VideoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

private const val LOCATION_INTERVAL = 1000L

class VideoRepositoryImpl(private val client: FusedLocationProviderClient) : VideoRepository {
    override fun getVideoUrl() =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(LOCATION_INTERVAL)
            .setPriority(PRIORITY_HIGH_ACCURACY)
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> {
        return callbackFlow {

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch {
                            send(location)
                        }
                    }
                }
            }

            client.requestLocationUpdates(
                getLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}
