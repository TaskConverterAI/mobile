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
    private const val BASE_URL = "http://10.199.58.103:8090/"

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

    val retrofitAuthService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val retrofitNoteApiService: RetrofitNoteApiService by lazy {
        retrofit.create(RetrofitNoteApiService::class.java)
    }

    val retrofitGroupApiService: RetrofitGroupApiService by lazy {
        retrofit.create(RetrofitGroupApiService::class.java)
    }

    /**
     * Создать NoteApiService для использования в репозиториях
     */
    fun createAuthRepository(repo: UserAuthPreferencesRepository): AuthRepository {
        return NetworkAuthRepository.getInstance(repo, retrofitAuthService)
    }
    fun createNoteApiService(): NoteApiService {
        return NetworkNoteApiService(retrofitNoteApiService)
    }

    fun createGroupApiService() : GroupApiService {
        return NetworkGroupApiService(retrofitGroupApiService)
    }
}
