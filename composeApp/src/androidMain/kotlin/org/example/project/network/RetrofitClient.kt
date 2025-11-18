package org.example.project.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.example.project.data.network.NoteApiService
import org.example.project.data.network.NetworkNoteApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Фабрика для создания Retrofit клиента и API сервисов
 */
object RetrofitClient {
    // TODO: Замените на URL вашего сервера
    private const val BASE_URL = "http://192.168.31.79:8090/api/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val retrofitNoteApiService: RetrofitNoteApiService by lazy {
        retrofit.create(RetrofitNoteApiService::class.java)
    }

    /**
     * Создать NoteApiService для использования в репозиториях
     */
    fun createNoteApiService(): NoteApiService {
        return NetworkNoteApiService(retrofitNoteApiService)
    }
}

