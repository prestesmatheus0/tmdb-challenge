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

            val validatedNetworks = mutableSetOf<Network>()

            fun emitCurrent() {
                trySend(if (validatedNetworks.isNotEmpty()) NetworkStatus.Online else NetworkStatus.Offline)
            }

            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onCapabilitiesChanged(
                        network: Network,
                        capabilities: NetworkCapabilities,
                    ) {
                        val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        if (validated) {
                            validatedNetworks.add(network)
                        } else {
                            validatedNetworks.remove(network)
                        }
                        emitCurrent()
                    }

                    override fun onLost(network: Network) {
                        validatedNetworks.remove(network)
                        emitCurrent()
                    }

                    override fun onUnavailable() {
                        emitCurrent()
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
        val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        return if (online) NetworkStatus.Online else NetworkStatus.Offline
    }
}
