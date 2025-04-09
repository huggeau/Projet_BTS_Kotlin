package com.btsciel.retrofit

import com.btsciel.Utils.ConfigServeur
import com.btsciel.models.JsonConfigServeur
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class Api_Retrofit {
    var api: IntWeb

    val confServeur: ConfigServeur = ConfigServeur()
    val config = confServeur.loadConfig()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(config.addServeur)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(IntWeb::class.java)
    }


}
