package com.github.pavelzhurman.freesound_api.datasource.network.di

import com.github.pavelzhurman.freesound_api.datasource.network.FreesoundAPI
import com.github.pavelzhurman.freesound_api.datasource.network.FreesoundDataSource
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
class RetrofitModule {

    @Named("base_url")
    @Provides
    fun provideBaseUrl(): String = "https://freesound.org/"

    @Named("fields")
    @Provides
    fun provideFields(): String =
        "id,name,tags,filesize,duration,username,download,num_downloads,images,previews,description"

    @Named("token")
    @Provides
    fun provideToken(): String = "Token hCmFb2zBmLB27I8Pj7K5s2ADUBfaEfKXEalJFcdC"

    @Provides
    fun provideRetrofit(
        @Named("base_url") baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

    @Provides
    fun provideOkhttpClient(@Named("token") token: String): OkHttpClient =
        OkHttpClient.Builder().addInterceptor { chain ->
            val originalRequest: Request = chain.request()

            val request = originalRequest.newBuilder()
                .addHeader("Authorization", token)
                .method(originalRequest.method(), originalRequest.body())
                .build()
            chain.proceed(request)
        }
            .build()

    @Provides
    fun provideFreesoundAPI(retrofit: Retrofit): FreesoundAPI =
        retrofit.create(FreesoundAPI::class.java)

    @Provides
    fun provideFreesoundSource(freesoundAPI: FreesoundAPI): FreesoundDataSource =
        FreesoundDataSource(freesoundAPI)


}