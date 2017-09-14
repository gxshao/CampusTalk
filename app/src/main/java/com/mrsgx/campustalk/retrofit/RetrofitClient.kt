package com.mrsgx.campustalk.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Shao on 2017/9/4.
 */
class RetrofitClient {
    companion object {
        var INSTANCE:RetrofitClient?=null
        fun getInstance():Retrofit?{
            return ClientHolder.retrofit
        }
    }
    class ClientHolder{
        companion object {
            var client=OkHttpClient.Builder().connectTimeout(60,TimeUnit.SECONDS)
                    .writeTimeout(60,TimeUnit.SECONDS)
                    .readTimeout(60,TimeUnit.SECONDS)
                    .build()
            var retrofit= Retrofit.Builder().baseUrl(Api.API_BASE)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        }
    }
}