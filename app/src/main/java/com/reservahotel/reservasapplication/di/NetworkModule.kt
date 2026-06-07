package com.reservahotel.reservasapplication.di

import com.reservahotel.reservasapplication.BuildConfig
import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.data.remote.api.*
import com.reservahotel.reservasapplication.data.remote.interceptor.AuthInterceptor
import com.reservahotel.reservasapplication.data.remote.interceptor.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton
    fun provideOkHttpClient(
        tokenDataStore: TokenDataStore,
        authInterceptor: AuthInterceptor,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .authenticator(authInterceptor)
        .addInterceptor(BearerTokenInterceptor(tokenDataStore))
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideHabitacionApi(retrofit: Retrofit): HabitacionApi =
        retrofit.create(HabitacionApi::class.java)

    @Provides @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi =
        retrofit.create(AdminApi::class.java)

    @Provides @Singleton
    fun provideClienteApi(retrofit: Retrofit): ClienteApi =
        retrofit.create(ClienteApi::class.java)

    @Provides @Singleton
    fun provideReservaApi(retrofit: Retrofit): ReservaApi =
        retrofit.create(ReservaApi::class.java)

    @Provides @Singleton
    fun provideServicioApi(retrofit: Retrofit): ServicioApi =
        retrofit.create(ServicioApi::class.java)

    @Provides @Singleton
    fun provideFacturaApi(retrofit: Retrofit): FacturaApi =
        retrofit.create(FacturaApi::class.java)
}
