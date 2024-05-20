package com.example.videosensorsample

import android.location.Location
import android.os.Looper
import com.example.videosensorsample.data.repository.VideoRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class VideoRepositoryImplTest {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var videoRepository: VideoRepositoryImpl

    @Before
    fun setup() {
        fusedLocationProviderClient = mockk(relaxed = true)
        videoRepository = VideoRepositoryImpl(fusedLocationProviderClient)
    }

    @Test
    fun `getVideoUrl should return the correct URL`() {
        val url = videoRepository.getVideoUrl()
        assertEquals(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            url
        )
    }

    @Test
    fun `getLocationUpdates should emit location updates`() = runBlocking {
        val mockLocation = mockk<Location>()
        val mockLocationResult = mockk<LocationResult> {
            every { locations } returns listOf(mockLocation)
        }
        val locationCallbackSlot = slot<LocationCallback>()

        every {
            fusedLocationProviderClient.requestLocationUpdates(
                any<LocationRequest>(),
                capture(locationCallbackSlot),
                any<Looper>()
            )
        } answers {
            locationCallbackSlot.captured.onLocationResult(mockLocationResult)
            mockk<Task<Void>>()
        }

        val locationFlow = videoRepository.getLocationUpdates()
        val location = locationFlow.first()

        assertEquals(mockLocation, location)
    }
}
