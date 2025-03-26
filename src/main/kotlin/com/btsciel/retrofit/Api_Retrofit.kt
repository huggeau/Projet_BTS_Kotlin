package com.btsciel.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api_Retrofit {
    var api: IntWeb

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.0.184:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(IntWeb::class.java)
    }
}
