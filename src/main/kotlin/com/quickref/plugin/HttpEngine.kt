package com.quickref.plugin

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel

@Suppress("unused")
object HttpEngine {

    private const val MAX_CONNECTION_COUNT = 10
    private const val MAX_REQUEST_TIMEOUT = 15_000L

    // storage key
    private const val ACCESS_EXTERNAL_NETWORK = "AccessExternalNetwork"
    private var lastPingTime = 0L

    fun setAccessExternalNetwork(access: Boolean) {
        App.appProps[ACCESS_EXTERNAL_NETWORK] = if (access) {
            "1"
        } else {
            "0"
        }
    }

    fun isAccessExternalNetwork(): Boolean {
        // one hour
//        if (lastPingTime > 60 * 60 * 1000L) {
//            ioScope.launch {
//                val result = pingGoogle()
//                setAccessExternalNetwork(result)
//            }
//        }
        return "1" == App.appProps.getProperty(ACCESS_EXTERNAL_NETWORK, "1")
    }

    private val ktorHttpClient = HttpClient(CIO) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    PluginLogger.debug(message)
                }
            }
            level = LogLevel.INFO
        }
        engine {
            maxConnectionsCount = MAX_CONNECTION_COUNT
            requestTimeout = MAX_REQUEST_TIMEOUT
            threadsCount = 1
            pipelining = true
        }
    }

    private suspend fun pingGoogle(): Boolean {
        val response: HttpResponse = ktorHttpClient.head("https://www.google.com")
        return response.status.isSuccess()
    }

    suspend fun get(url: String): ByteReadChannel {
        val response: HttpResponse = ktorHttpClient.get(url)
        return response.content
    }

}
