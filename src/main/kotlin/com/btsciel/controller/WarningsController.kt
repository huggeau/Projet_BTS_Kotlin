package com.btsciel.controller

import com.btsciel.models.ModelQPIWS
import javafx.stage.Stage

class WarningsController {
    private lateinit var scanStage: Stage
    private lateinit var modelQPIWS: ModelQPIWS


    fun setModelQPIWS(model: ModelQPIWS) {
        this.modelQPIWS = model
    }

    fun setScanStage(scanStage: Stage) {
        this.scanStage = scanStage
    }
}