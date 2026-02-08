package com.example.feature.upload.data.di

import com.example.feature.upload.data.repository.UploadRepositoryImpl
import com.example.feature.upload.domain.repository.UploadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UploadModule {

    @Binds
    abstract fun bindUploadRepository(impl: UploadRepositoryImpl): UploadRepository
}
