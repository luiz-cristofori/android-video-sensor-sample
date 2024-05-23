package com.example.videosensorsample.domain

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

private const val CHILD_TYPE = "media"
private const val MAX_CACHE_SIZE = 100 * 1024 * 1024
private const val APPLICATION_NAME = "video-sample"

@UnstableApi
object CacheUtil {
    private var simpleCache: SimpleCache? = null

    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        synchronized(this) {
            if (simpleCache == null) {
                val cacheDir = File(context.cacheDir, CHILD_TYPE)
                val cacheEvictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE.toLong())
                simpleCache =
                    SimpleCache(cacheDir, cacheEvictor, StandaloneDatabaseProvider(context))
            }
        }

        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, APPLICATION_NAME)
        )

        return CacheDataSource.Factory()
            .setCache(simpleCache!!)
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setFlags(FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
