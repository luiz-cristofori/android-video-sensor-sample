package com.example.videosensorsample.di

import com.example.videosensorsample.data.repository.VideoRepositoryImpl
import com.example.videosensorsample.domain.repository.VideoRepository
import com.example.videosensorsample.presentation.viewmodel.VideoPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::VideoPlayerViewModel)
    singleOf(::VideoRepositoryImpl) bind VideoRepository::class
}