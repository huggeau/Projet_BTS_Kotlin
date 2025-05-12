package com.btsciel.retrofit

import com.btsciel.Pojo.PojoParam
import com.btsciel.Pojo.PojoPrix
import com.btsciel.models.*
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

    @POST("/onduleur/loginAdmin")
    fun postLogin(@Body modelLoginAdmin: ModelLoginAdmin?): Call<Api_Retrofit?>?

    @POST("/onduleur/parametreOnduleur")
    fun getParamOndu(@Body modelSourcePriority: ModelSourcePriority): Call<PojoParam?>?

    @POST("/onduleur/gain")
    fun postGainJournalier(@Body modelGainJournalier: ModelGainJournalier?): Call<Api_Retrofit?>?

    @POST("/onduleur/gain")
    fun postGainMensuel(@Body modelGainMensuel: ModelGainMensuel?): Call<Api_Retrofit?>?

    @POST("/onduleur/gain")
    fun postGainAnnuel(@Body modelGainAnnuel: ModelGainAnnuel?): Call<Api_Retrofit?>?

}
