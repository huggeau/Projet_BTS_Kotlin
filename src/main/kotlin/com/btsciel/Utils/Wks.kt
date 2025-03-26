package com.btsciel.Utils

import com.btsciel.Pojo.PojoPrix
import com.btsciel.RequeteBdd.DataBaseRequest
import com.btsciel.models.*
import com.btsciel.retrofit.Api_Retrofit
import jssc.SerialPortEvent
import java.sql.*

class Wks : LiaisonSerie() {
    private val QPIRI = "QPIRI".toByteArray()
    private val QPIGS = "QPIGS".toByteArray()
    private val QPIWS = "QPIWS".toByteArray()
    private val CR: Byte = 0x0D
    private val modelQPIGS: ModelQPIGS = ModelQPIGS()
    private val modelQPIRI: ModelQPIRI = ModelQPIRI()
    private val modelQPIWS: ModelQPIWS = ModelQPIWS()
    private val dataBaseRequest: DataBaseRequest = DataBaseRequest()
    private val listACOutputApparentPower: java.util.ArrayList<String> = java.util.ArrayList()
    private var retrofit: Api_Retrofit = Api_Retrofit()

    /** Méthode qui permet de calculer le crc en fonction de la requête envoyé */
    private fun crc16King5k(data: ByteArray): Int {
        var crc = 0x0000
        val polynomial = 0x1021
        for (b in data) {
            for (i in 0..7) {
                val bit = ((b.toInt() shr (7 - i) and 1) == 1)
                val c15 = ((crc shr 15 and 1) == 1)
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }
        crc = crc and 0xffff
        return crc
    }

    /** Méthode permettant d'envoyer la requête QPIGS avec crc et \r vers l'onduleur */
    fun requeteQPIGS() {
        val crcQPIGS = crc16King5k(QPIGS)
        val crcByteQPIGS = intToByteArray(crcQPIGS)

        super.ecrire(
            org.apache.commons.lang3.ArrayUtils.add(
                org.apache.commons.lang3.ArrayUtils.addAll(
                    QPIGS,
                    *crcByteQPIGS
                ), CR
            )
        )
    }

    /** Méthode permettant d'envoyer la requête QPIRI avec crc et \r vers l'onduleur */
    fun requeteQPIRI() {
        val crcQPIRI = crc16King5k(QPIRI)
        val crcByteQPIRI = intToByteArray(crcQPIRI)

        super.ecrire(
            org.apache.commons.lang3.ArrayUtils.add(
                org.apache.commons.lang3.ArrayUtils.addAll(
                    QPIRI,
                    *crcByteQPIRI
                ), CR
            )
        )
    }

    /** Méthode permettant d'envoyer la requête QIWS avec crc et \r vers l'onduleur */
    fun requeteQPIWS() {
        val crcQPIWS = crc16King5k(QPIWS)
        val crcByteQPIWS = intToByteArray(crcQPIWS)

        super.ecrire(
            org.apache.commons.lang3.ArrayUtils.add(
                org.apache.commons.lang3.ArrayUtils.addAll(
                    QPIWS,
                    *crcByteQPIWS
                ), CR
            )
        )
    }

    /** Sert à transformer le crc en tableau d'octet */
    private fun intToByteArray(i: Int): ByteArray {
        /*
        exemple de ce que fait " >> "
        00 00 23 96 >> 00 00 00 23
        */

        val b = ByteArray(2)
        b[1] = (i and 0x000000ff).toByte()
        b[0] = (i and 0x0000ff00).toByte()
        return b
    }

    /** Est l'évênement qui écoute la réponse de l'onduleur et renvoie la trame sur son modèle respectif en fonction de sa longueur */
    override fun serialEvent(event: SerialPortEvent) {
        try {
            Thread.sleep(500)
            val tram = super.lireTrame(super.detecteSiReception())
            /*String tramString = new String(tram);
            System.out.print(tramString);*/
            when (tram!!.size) {
                40 -> modelQPIWS(tram)
                104 -> modelQPIRI(tram)
                110 -> modelQPIGS(tram)
                else -> {}
            }
        } catch (e: InterruptedException) {
            System.err.println("erreur : " + e.message)
        }
    }

    /** Est le model de la requête QPIGS, et mets en mémoire chaque information transmis par l'onduleur dans sa trame */
    private fun modelQPIGS(tram: ByteArray) {
        val s = String(tram, java.nio.charset.StandardCharsets.US_ASCII)

        val tab2 = s.substring(1).split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        tab2[20] = tab2[20].substring(0, 3)

        modelQPIGS.grid_voltage = tab2[0]
        modelQPIGS.grid_frequency = tab2[1]
        modelQPIGS.aC_output_voltage = tab2[2]
        modelQPIGS.aC_output_frequency = tab2[3]
        modelQPIGS.setAC_output_apparent_power(tab2[4])
        modelQPIGS.aC_output_active_power = tab2[5]
        modelQPIGS.output_load_percent = tab2[6]
        modelQPIGS.bus_voltage = tab2[7]
        modelQPIGS.battery_voltage = tab2[8]
        modelQPIGS.battery_charging_current = tab2[9]
        modelQPIGS.battery_capacity = tab2[10]
        modelQPIGS.inverter_heat_sink_temperature = tab2[11]
        modelQPIGS.pv_input_current_for_battery = tab2[12]
        modelQPIGS.pv_input_voltage_1 = tab2[13]
        modelQPIGS.battery_voltage_fromm_scc = tab2[14]
        modelQPIGS.battery_discharge_current = tab2[15]
        modelQPIGS.device_status1 = tab2[16]
        modelQPIGS.battery_voltage_offset_for_fans_on = tab2[17]
        modelQPIGS.eeprom_version = tab2[18]
        modelQPIGS.pv_charging_power = tab2[19]
        modelQPIGS.device_status2 = tab2[20]
    }

    /** Est le modèle de la requête QPIRI et mets en mémoire les données transmis par l'onduleur */
    private fun modelQPIRI(tram: ByteArray) {
        val s = String(tram, java.nio.charset.StandardCharsets.US_ASCII)
        val tab2 = s.substring(1).split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        tab2[26] = tab2[26].substring(0, 1)

        modelQPIRI.grid_rating_voltage = tab2[0]
        modelQPIRI.grid_rating_current = tab2[1]
        modelQPIRI.aC_output_rating_voltage = tab2[2]
        modelQPIRI.aC_output_rating_frequency = tab2[3]
        modelQPIRI.aC_output_rating_current = tab2[4]
        modelQPIRI.aC_output_rating_apparent_power = tab2[5]
        modelQPIRI.aC_output_rating_active_power = tab2[6]
        modelQPIRI.battery_rating_voltage = tab2[7]
        modelQPIRI.battery_recharge_voltage = tab2[8]
        modelQPIRI.battery_under_voltage = tab2[9]
        modelQPIRI.battery_bulk_voltage = tab2[10]
        modelQPIRI.battery_float_voltage = tab2[11]
        modelQPIRI.battery_type = tab2[12]
        modelQPIRI.current_max_AC_charging_current = tab2[13]
        modelQPIRI.current_max_charging_current = tab2[14]
        modelQPIRI.input_voltage_range = tab2[15]
        modelQPIRI.output_source_priority = tab2[16]
        modelQPIRI.charger_source_priority = tab2[17]
        modelQPIRI.parallel_max_number = tab2[18]
        modelQPIRI.machine_type = tab2[19]
        modelQPIRI.topology = tab2[20]
        modelQPIRI.output_mode = tab2[21]
        modelQPIRI.battery_redischarge_voltage = tab2[22]
        modelQPIRI.pv_ok_condition_for_parallel = tab2[23]
        modelQPIRI.pv_power_balance = tab2[24]
        modelQPIRI.max_charging_time_at_CV_stage = tab2[25]
        modelQPIRI.operation_logic = tab2[26]
    }

    /** Est le modèle de la requête QPIWS et mets en mémoire chaque info envoyé par l'onduleur */
    private fun modelQPIWS(tram: ByteArray) {
        val s = String(tram, java.nio.charset.StandardCharsets.US_ASCII)

        val tab2 = s.substring(1)
        val tab3 = arrayOfNulls<String>(39)
        for (i in 0..<tab2.length) {
            if (tab2.length < 79) {
                tab3[i] = tab2.substring(i, i + 1)
            } else {
                tab3[i] = tab2.substring(i)
            }
        }

        modelQPIWS.a0 = tab3[0]
        modelQPIWS.a1 = tab3[1]
        modelQPIWS.a2 = tab3[2]
        modelQPIWS.a3 = tab3[3]
        modelQPIWS.a4 = tab3[4]
        modelQPIWS.a5 = tab3[5]
        modelQPIWS.a6 = tab3[6]
        modelQPIWS.a7 = tab3[7]
        modelQPIWS.a8 = tab3[8]
        modelQPIWS.a9 = tab3[9]
        modelQPIWS.a10 = tab3[10]
        modelQPIWS.a11 = tab3[11]
        modelQPIWS.a12 = tab3[12]
        modelQPIWS.a13 = tab3[13]
        modelQPIWS.a14 = tab3[14]
        modelQPIWS.a15 = tab3[15]
        modelQPIWS.a16 = tab3[16]
        modelQPIWS.a17 = tab3[17]
        modelQPIWS.a18 = tab3[18]
        modelQPIWS.a19 = tab3[19]
        modelQPIWS.a20 = tab3[20]
        modelQPIWS.a21 = tab3[21]
        modelQPIWS.a22 = tab3[22]
        modelQPIWS.a23 = tab3[23]
        modelQPIWS.a24 = tab3[24]
        modelQPIWS.a25 = tab3[25]
        modelQPIWS.a26 = tab3[26]
        modelQPIWS.a27 = tab3[27]
        modelQPIWS.a28 = tab3[28]
        modelQPIWS.a29 = tab3[29]
        modelQPIWS.a30 = tab3[30]
        modelQPIWS.a31 = tab3[31]
        modelQPIWS.a32 = tab3[32]
        modelQPIWS.a33 = tab3[33]
        modelQPIWS.a34 = tab3[34]
        modelQPIWS.a35 = tab3[35]
    }

    /**
     * Méthode appelée toute les 6s et qui sert à mettre la valeur de consommayion de sortie dans une array list
     */
    fun putDataInArray() {
        listACOutputApparentPower.add(modelQPIGS.getAC_output_apparent_power())
    }

    /**
     * Est la methode qui permet de faire la moyenne sur 1min de la consommation qui a été échantillonné toute les 6s
     */
    @Throws(SQLException::class)
    fun calculMoyenneEnergie() {
        var moyenneEnergie = 0.0
        if (!listACOutputApparentPower.isEmpty()) {
            if (listACOutputApparentPower.size <= 10) {
                for (s in listACOutputApparentPower) {
                    var valeur = 0
                    valeur = s.toInt()
                    moyenneEnergie += valeur.toDouble()
                }
            }
        }
        listACOutputApparentPower.clear()
        dataBaseRequest.insertData((moyenneEnergie / 10).toString())
    }

    /** Méthode qui va chercher le prix sur le serveur distant. */
    fun recupPrix() {
        retrofit.api.getPrix?.enqueue(object : retrofit2.Callback<PojoPrix?> {
            override fun onResponse(call: retrofit2.Call<PojoPrix?>?, response: retrofit2.Response<PojoPrix?>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        dataBaseRequest.updatetPrix(java.lang.String.valueOf(response.body()!!.prix))
                    } catch (e: SQLException) {
                        System.err.println(e.message)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<PojoPrix?>?, throwable: Throwable) {
                System.err.println(throwable.message)
            }
        })
    }

    /** Envoie les informations de l'onduleur, qui sont sa position et son @Mac */
    @Throws(SQLException::class)
    fun envoieInfoOnduleur() {
        val tab: Array<String?>? = dataBaseRequest.recupInfoOnduleur()
        val modelInfoOnduleur: ModelInfoOnduleur? = tab?.get(0)?.let {
            tab[1]?.let { it1 ->
                tab[2]?.let { it2 ->
                    ModelInfoOnduleur(
                        it,
                        it1, it2
                    )
                }
            }
        }
        retrofit.api.postInfo(modelInfoOnduleur)?.enqueue(object : retrofit2.Callback<Api_Retrofit?> {
            override fun onResponse(call: retrofit2.Call<Api_Retrofit?>?, response: retrofit2.Response<Api_Retrofit?>) {
                if (response.isSuccessful && response.body() != null) {
                    println(response.body())
                }
            }

            override fun onFailure(call: retrofit2.Call<Api_Retrofit?>?, throwable: Throwable) {
                System.err.println(throwable.message)
            }
        })
    }

    /** Envoie la conso de l'onduleur a la bdd distante. */
    @Throws(SQLException::class)
    fun envoieConsoOnduleur() {
        //récupère la conso
        val tabConso: Array<String?>? = dataBaseRequest.recupPostConso()
        //récupère les infos de l'onduleur, mais on n'utilise que l'@Mac
        val tabAddMac: Array<String?>? = dataBaseRequest.recupInfoOnduleur()
        val modelConsoOnduleur: ModelConsoOnduleur? = tabConso?.get(0)?.let {
            tabConso[1]?.let { it1 ->
                tabConso[2]?.let { it2 ->
                    tabAddMac?.get(2)?.let { it3 ->
                        ModelConsoOnduleur(
                            it,
                            it1, it2, it3
                        )
                    }
                }
            }
        }
        retrofit.api.postConso(modelConsoOnduleur)?.enqueue(object : retrofit2.Callback<Api_Retrofit?> {
            override fun onResponse(call: retrofit2.Call<Api_Retrofit?>?, response: retrofit2.Response<Api_Retrofit?>) {
                if (response.isSuccessful && response.body() != null) {
                    println(response.body())
                }
            }

            override fun onFailure(call: retrofit2.Call<Api_Retrofit?>?, throwable: Throwable) {
                System.err.println(throwable.message)
            }
        })
    }

    fun getModelQPIGS(): ModelQPIGS {
        return modelQPIGS
    }
}