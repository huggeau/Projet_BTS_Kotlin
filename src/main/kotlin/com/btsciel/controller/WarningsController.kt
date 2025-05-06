package com.btsciel.controller

import com.btsciel.models.ModelQPIWS
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.net.URL
import java.util.*
import kotlin.reflect.full.memberProperties

class WarningsController : Initializable {

    private lateinit var scanStage: Stage
    private lateinit var modelQPIWS: ModelQPIWS

    // Lier ce VBox à votre FXML avec fx:id="root"
    @FXML
    private lateinit var root: VBox

    val warnings = FXCollections.observableArrayList<String>()

    fun setModelQPIWS(model: ModelQPIWS) {
        this.modelQPIWS = model
        if (this::root.isInitialized) {
            populateWarnings()
        }
    }

    fun setScanStage(scanStage: Stage) {
        this.scanStage = scanStage
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Si le modèle a déjà été défini, afficher immédiatement les erreurs
        if (this::modelQPIWS.isInitialized) {
            populateWarnings()
        }
    }

    private fun populateWarnings() {
        root.children.clear()
        warnings.clear()

        if (!::modelQPIWS.isInitialized) return

        val errorLabels = mapOf(
            "a0" to "PV loss",
            "a1" to "Inverter fault",
            "a2" to "Bus Over",
            "a3" to "Bus Under",
            "a4" to "Bus Soft Fail",
            "a5" to "LINE_FAIL",
            "a6" to "OPVShort",
            "a7" to "Inverter voltage too low",
            "a8" to "Inverter voltage too high",
            "a9" to "Over temperature",
            "a10" to "Fan locked",
            "a11" to "Battery voltage high",
            "a12" to "Battery low alarm",
            "a13" to "Reserved",
            "a14" to "Battery under shutdown",
            "a15" to "Battery derating",
            "a16" to "Over load",
            "a17" to "Eeprom fault",
            "a18" to "Inverter Over Current",
            "a19" to "Inverter Soft Fail",
            "a20" to "SelfTest Fail",
            "a21" to "OP DC Voltage Over",
            "a22" to "Battery Open",
            "a23" to "Current Sensor Fail",
            "a24" to "Battery Short",
            "a25" to "Power limit",
            "a26" to "PV voltage high",
            "a27" to "MPPT overload fault",
            "a28" to "MPPT overload warning",
            "a29" to "Battery too low to charge",
            "a30" to "DC/DC Over Current",
            "a31" to "D",
            "a32" to "D",
            "a33" to "Low PV energy",
            "a34" to "High AC input during BUS soft start",
            "a35" to "Battery equalization"
        )

        ModelQPIWS::class.memberProperties
            .filter { it.name.startsWith("a") }
            .sortedBy { it.name.removePrefix("a").toIntOrNull() }
            .forEach { prop ->
                val value = prop.get(modelQPIWS)
                if (value == "1") {
                    val warningText = errorLabels[prop.name] ?: "Erreur inconnue sur ${prop.name}"
                    warnings.add(warningText)

                    val label = Label(warningText)
                    label.maxWidth = Double.MAX_VALUE
                    label.alignment = Pos.CENTER
                    root.children.add(label)
                }
            }
    }
}
