package com.example.core.data.di

import com.example.core.data.repository.BookContentReaderImpl
import com.example.core.data.repository.CloudinaryRepositoryImpl
import com.example.core.data.repository.FirebaseRepositoryImpl
import com.example.core.data.repository.NetworkCheckerRepositoryImpl
import com.example.core.data.repository.YandexStorageRepositoryImpl
import com.example.core.domain.repository.BookContentReaderRepository
import com.example.core.domain.repository.CloudinaryRepository
import com.example.core.domain.repository.FirebaseRepository
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.core.domain.repository.YandexStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFirebaseRepository(impl: FirebaseRepositoryImpl): FirebaseRepository

    @Binds
    abstract fun bindCloudinaryRepository(impl: CloudinaryRepositoryImpl): CloudinaryRepository

    @Binds
    abstract fun bindYandexStorageRepository(impl: YandexStorageRepositoryImpl): YandexStorageRepository

    @Binds
    abstract fun bindsNetworkCheckerRepository(impl: NetworkCheckerRepositoryImpl): NetworkCheckerRepository

    @Binds
    abstract fun bindBookContentReaderRepository(impl: BookContentReaderImpl): BookContentReaderRepository

}
