package com.example.videosensorsample

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.media.AudioManager
import com.example.videosensorsample.data.repository.VideoRepositoryImpl
import com.example.videosensorsample.domain.repository.VideoRepository
import com.example.videosensorsample.presentation.viewmodel.VideoPlayerViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class AppModuleTest : KoinTest {

    private val sensorManager: SensorManager by inject()
    private val audioManager: AudioManager by inject()
    private val fusedLocationProviderClient: FusedLocationProviderClient by inject()
    private val videoRepository: VideoRepositoryImpl by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)

        val mockContext = mockk<Context>(relaxed = true)
        val mockSensorManager = mockk<SensorManager>(relaxed = true)
        val mockAudioManager = mockk<AudioManager>(relaxed = true)

        every { mockContext.getSystemService(SENSOR_SERVICE) } returns mockSensorManager
        every { mockContext.getSystemService(AUDIO_SERVICE) } returns mockAudioManager

        androidContext(mockContext)
        modules(appModule)
    }

    @After
    fun autoClose() {
        stopKoin()
    }

    @Test
    fun `viewModelOf should provide VideoPlayerViewModel`() {
        val viewModel by inject<VideoPlayerViewModel>()
        assertNotNull(viewModel)
    }

    @Test
    fun `sensorManager should be provided correctly`() {
        assertNotNull(sensorManager)
    }

    @Test
    fun `audioManager should be provided correctly`() {
        assertNotNull(audioManager)
    }

    @Test
    fun `fusedLocationProviderClient should be provided correctly`() {
        assertNotNull(fusedLocationProviderClient)
    }

    @Test
    fun `videoRepository should be provided correctly`() {
        assertNotNull(videoRepository)
    }

    companion object {
        val appModule = module {
            viewModelOf(::VideoPlayerViewModel)
            single { androidContext().getSystemService(SENSOR_SERVICE) as SensorManager }
            single { androidContext().getSystemService(AUDIO_SERVICE) as AudioManager }
            single { LocationServices.getFusedLocationProviderClient(androidContext()) }
            singleOf(::VideoRepositoryImpl) bind VideoRepository::class
        }
    }
}
