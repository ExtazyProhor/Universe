package ru.prohor.universe.chopper.client

import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@ComponentScan
class ChopperClientConfig {
    @Bean
    fun chopperClient(
        okHttpClient: OkHttpClient,
        @Value($$"${universe.chopper.base-url}") baseUrl: String,
        @Value($$"${universe.chopper.api-key}") apiKey: String
    ): ChopperClient {
        return ChopperOkHttpClient(
            client = okHttpClient,
            baseUrl = baseUrl,
            apiKey = apiKey
        )
    }

    @Bean
    fun okHttpClient(
        @Value($$"${universe.chopper.okhttp.connect-timeout}") connectTimeout: Long,
        @Value($$"${universe.chopper.okhttp.read-timeout}") readTimeout: Long,
        @Value($$"${universe.chopper.okhttp.write-timeout}") writeTimeout: Long,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}
