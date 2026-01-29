package com.example.feature.book.reader.data.di

import com.example.feature.book.reader.data.repository.BookReaderRepositoryImpl
import com.example.feature.book.reader.domain.repository.BookReaderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent:: class)
abstract class BookReaderModule {

    @Binds
    abstract fun bindBookReaderRepository(impl: BookReaderRepositoryImpl): BookReaderRepository

}