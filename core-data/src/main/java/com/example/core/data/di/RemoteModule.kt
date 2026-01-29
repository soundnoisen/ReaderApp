package com.example.core.data.di

import com.example.core.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Named("yandexIamToken")
    fun provideYandexIamToken(): String = BuildConfig.YANDEX_IAM_TOKEN

    @Provides
    @Named("cloudinaryCloudName")
    fun provideCloudinaryCloudName(): String = BuildConfig.CLOUDINARY_CLOUD_NAME

    @Provides
    @Named("cloudinaryUploadPreset")
    fun provideCloudinaryUploadPreset(): String = BuildConfig.CLOUDINARY_UPLOAD_PRESET

}