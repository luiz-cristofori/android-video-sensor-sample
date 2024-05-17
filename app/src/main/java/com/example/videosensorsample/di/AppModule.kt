package com.example.videosensorsample.di

import com.example.videosensorsample.data.repository.VideoRepositoryImpl
import com.example.videosensorsample.domain.repository.VideoRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::VideoRepositoryImpl) bind VideoRepository::class
}