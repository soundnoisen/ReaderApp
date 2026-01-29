package com.example.core.data.di

import com.example.core.data.repository.ReaderPositionRepositoryImpl
import com.example.core.data.repository.ThemeRepositoryImpl
import com.example.core.domain.repository.ReaderPositionRepository
import com.example.core.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    abstract fun bindReaderPositionRepository(impl: ReaderPositionRepositoryImpl): ReaderPositionRepository
}