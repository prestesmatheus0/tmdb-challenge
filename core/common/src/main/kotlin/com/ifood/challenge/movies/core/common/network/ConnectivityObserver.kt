package com.ifood.challenge.movies.core.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

interface ConnectivityObserver {
    fun observe(): Flow<NetworkStatus>
}

class AndroidConnectivityObserver(
    private val context: Context,
) : ConnectivityObserver {
    override fun observe(): Flow<NetworkStatus> =
        callbackFlow {
            val manager = context.getSystemService(ConnectivityManager::class.java)
            if (manager == null) {
                trySend(NetworkStatus.Offline)
                awaitClose { }
                return@callbackFlow
            }

            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        trySend(NetworkStatus.Online)
                    }

                    override fun onLost(network: Network) {
                        trySend(NetworkStatus.Offline)
                    }

                    override fun onUnavailable() {
                        trySend(NetworkStatus.Offline)
                    }
                }

            val request =
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            trySend(currentStatus(manager))
            manager.registerNetworkCallback(request, callback)

            awaitClose { manager.unregisterNetworkCallback(callback) }
        }.distinctUntilChanged()

    private fun currentStatus(manager: ConnectivityManager): NetworkStatus {
        val caps = manager.getNetworkCapabilities(manager.activeNetwork)
        val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        return if (online) NetworkStatus.Online else NetworkStatus.Offline
    }
}
