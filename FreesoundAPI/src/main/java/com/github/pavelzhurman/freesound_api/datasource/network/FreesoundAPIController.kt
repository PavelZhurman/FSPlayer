package com.github.pavelzhurman.freesound_api.datasource.network

import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSearchData
import com.github.pavelzhurman.freesound_api.datasource.network.entities.FreesoundSongItem
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://freesound.org/"
private const val TOKEN = "Token hCmFb2zBmLB27I8Pj7K5s2ADUBfaEfKXEalJFcdC"
private const val FIELDS =
    "id,name,tags,filesize,duration,username,download,num_downloads,images,previews,description"


class FreesoundAPIController  {

//    override fun getFreesoundSearchData(query: String): Single<FreesoundSearchData> =
//        retrofit.create(FreesoundAPI::class.java).getFreesoundSearchData(query)
//            .subscribeOn(Schedulers.io())
//
//    override fun getSongInfo(id: String): Single<FreesoundSongItem> =
//        retrofit.create(FreesoundAPI::class.java)
//            .getSongInfo(id, FIELDS)
//            .subscribeOn(Schedulers.io())
//
//    private object RetrofitHolder {
//        val retrofit: Retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(createClient())
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        private fun createClient(): OkHttpClient {
//            val httpClient = OkHttpClient.Builder().apply {
//                addInterceptorToHttpClient(this)
//            }
//            return httpClient.build()
//        }
//
//        private fun addInterceptorToHttpClient(builder: OkHttpClient.Builder) {
//            builder.addInterceptor { chain ->
//                val original: Request = chain.request()
//
//                val request = original.newBuilder()
//                    .header("Authorization", TOKEN)
//                    .method(original.method(), original.body())
//                    .build()
//                chain.proceed(request)
//            }
//        }
//    }

}