package com.btsciel.Utils

import com.btsciel.models.JsonConfigServeur
import com.google.gson.Gson
import java.io.File

class ConfigServeur {
    val configFile = File("config.json")
    val gson = Gson()

    fun loadConfig(): JsonConfigServeur {
        return if (configFile.exists()){
            gson.fromJson(configFile.readText(), JsonConfigServeur::class.java)
        }
        else{
            val defaultConfig = JsonConfigServeur()
            saveConfig(defaultConfig)
            defaultConfig
        }
    }

    fun saveConfig(config: JsonConfigServeur) {
        configFile.writeText(gson.toJson(config))
    }
}