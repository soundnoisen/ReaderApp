package com.example.core.domain.repository

interface NetworkCheckerRepository {
    fun isNetworkAvailable(): Boolean
}