package com.example.videosensorsample.di

import android.content.Context.AUDIO_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.media.AudioManager
import com.example.videosensorsample.data.repository.VideoRepositoryImpl
import com.example.videosensorsample.domain.repository.VideoRepository
import com.example.videosensorsample.presentation.viewmodel.VideoPlayerViewModel
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::VideoPlayerViewModel)
    single { androidContext().getSystemService(SENSOR_SERVICE) as SensorManager }
    single { androidContext().getSystemService(AUDIO_SERVICE) as AudioManager }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    singleOf(::VideoRepositoryImpl) bind VideoRepository::class
}
