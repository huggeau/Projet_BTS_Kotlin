package com.btsciel.retrofit

import com.btsciel.Pojo.PojoPrix
import com.btsciel.models.ModelConsoOnduleur
import com.btsciel.models.ModelInfoOnduleur
import com.btsciel.models.ModelQPIWS
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IntWeb {
    @GET("/prix/voirPrix")
    fun getPrix(): Call<PojoPrix?>?

    @POST("/onduleur/addConso")
    fun postConso(@Body modelConsoOnduleur: ModelConsoOnduleur?): Call<Api_Retrofit?>?

    @POST("/onduleur/addInfoOnduleur")
    fun postInfo(@Body infoOnduleur: ModelInfoOnduleur?): Call<Api_Retrofit?>?

    @POST("/onduleur/warning")
    fun postWarning(@Body modeQPIWS: ModelQPIWS?): Call<Api_Retrofit?>?

//    @GET("/onduleur/parametreOnduleur")
    // fun getParamOndu(): Call<PojoParam?>?
}
