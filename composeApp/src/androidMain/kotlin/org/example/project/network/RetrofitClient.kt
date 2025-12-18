package org.example.project.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.network.GroupApiService
import org.example.project.data.network.NetworkGroupApiService
import org.example.project.data.network.NoteApiService
import org.example.project.data.network.NetworkNoteApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Фабрика для создания Retrofit клиента и API сервисов
 */
object RetrofitClient {

    private const val URL = "http://10.154.120.68:"
    private const val AUTH_PORT = "8081/"
    private const val ANALYZER_PORT = "8082/"
    private const val TASK_PORT = "8083/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val authClient: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL + AUTH_PORT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val taskClient: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL + TASK_PORT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    private val analyzerClient: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL + ANALYZER_PORT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val authApiService: AuthApiService by lazy {
        authClient.create(AuthApiService::class.java)
    }

    private val taskApiService: RetrofitNoteApiService by lazy {
        taskClient.create(RetrofitNoteApiService::class.java)
    }

    private val groupApiService: RetrofitGroupApiService by lazy {
        authClient.create(RetrofitGroupApiService::class.java)
    }

    private val analyzerApiService: AnalyzerApiService by lazy {
        analyzerClient.create(AnalyzerApiService::class.java)
    }

    fun createAuthRepository(repo: UserAuthPreferencesRepository): AuthRepository {
        return NetworkAuthRepository.getInstance(repo, authApiService)
    }

    fun createNoteApiService(): NoteApiService {
        println("[RetrofitClient] Creating NoteApiService with baseUrl=${URL + TASK_PORT}")
        return NetworkNoteApiService(taskApiService)
    }

    fun createGroupApiService(): GroupApiService {
        return NetworkGroupApiService(groupApiService)
    }

    fun createAnalyzerApiService(): AnalyzerApiService {
        return analyzerApiService
    }

    // Экспонируем базовые URL для логирования в других компонентах
    fun taskBaseUrl(): String = URL + TASK_PORT
    fun authBaseUrl(): String = URL + AUTH_PORT
    fun analyzerBaseUrl(): String = URL + ANALYZER_PORT
}
