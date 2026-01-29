package com.example.core.data.repository

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.example.core.domain.repository.NetworkCheckerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class NetworkCheckerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): NetworkCheckerRepository {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}