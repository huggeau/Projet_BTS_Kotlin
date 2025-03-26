package com.btsciel

import com.btsciel.Utils.Wks
import com.btsciel.models.ModelQPIGS
import com.btsciel.timer.TimerData
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import java.sql.SQLException
import java.util.*

class HelloController : Initializable {
    private val absolutePositionPanel1 = .33
    private val absolutePositionPanel2 = .25

    @FXML
    var labelConsoInstant: javafx.scene.control.Label? = null
    @FXML
    var TxtFieldWarning: javafx.scene.control.TextField? = null
    @FXML
    var ButtonAdmin: javafx.scene.control.Button? = null
    @FXML
    var idLineChart: LineChart<Number, Number>? = null
    @FXML
    var idXAxis: CategoryAxis? = null
    @FXML
    var idYAxis: NumberAxis? = null
    @FXML
    var fenetreMere: AnchorPane? = null
    @FXML
    var splitplane1: SplitPane? = null
    @FXML
    var splitplane2: SplitPane? = null

    var wks: Wks = Wks()
    var timer: TimerData = TimerData(wks)
    var mQPIGS: ModelQPIGS = wks.getModelQPIGS()


    override fun initialize(location: java.net.URL?, resources: ResourceBundle?) {
        blockPanel()

        labelConsoInstant!!.textProperty().bind(
            javafx.beans.binding.Bindings.`when`(mQPIGS.AC_output_apparent_powerProperty().isNotNull())
                .then(mQPIGS.getAC_output_apparent_power())
                .otherwise("valeur null")
        )

        ButtonAdmin!!.onAction =
            javafx.event.EventHandler { event: javafx.event.ActionEvent? ->
                try {
                    wks.envoieInfoOnduleur()
                } catch (e: SQLException) {
                    System.err.println(e.message)
                }
            }

        launchTimers()
    }

    /**
     * Méthode servant à bloquer les séparateurs des splits panels.
     */
    private fun blockPanel() {
        splitplane1?.getDividers()?.first()?.positionProperty()
            ?.addListener { observable: ObservableValue<out Number?>?, oldValue: Number?, newValue: Number? ->
                splitplane1!!.setDividerPositions(absolutePositionPanel1)
            }
        splitplane2?.getDividers()?.first()?.positionProperty()
            ?.addListener { observable: ObservableValue<out Number?>?, oldValue: Number?, newValue: Number? ->
                splitplane2!!.setDividerPositions(absolutePositionPanel2)
            }
    }

    /** Méthode servant à lancer tous les timers. */
    private fun launchTimers() {
        timer.runThreadRecupDataOnduleur()
        timer.runThreadMoyenneEnergie()
        timer.runThreadEnvoieBddDistante()
        timer.runThreadModifData(mQPIGS)
        timer.runThreadRoutePrix()
    }
}