package com.example.feature.books.data.di

import com.example.feature.books.data.repository.BookRepositoryImpl
import com.example.feature.books.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BookBindingModule {
    @Binds
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository
}
