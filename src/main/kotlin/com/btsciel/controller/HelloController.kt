package com.btsciel.controller

import com.btsciel.Utils.Wks
import com.btsciel.models.ModelQPIGS
import com.btsciel.Utils.TimerData
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.stage.Modality
import javafx.stage.Stage
import java.util.*

class HelloController : Initializable {

    private val absolutePositionPanel1 = .33
    private val absolutePositionPanel2 = .25
    private val timer_binding: Timer = Timer()

    @FXML
    var TextFieldWarning: TextField? = null
    @FXML
    var labelConsoInstant: javafx.scene.control.Label? = null
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

    private var wks: Wks = Wks()
    private var timer: TimerData = TimerData(wks)
    var mQPIGS: ModelQPIGS = wks.getModelQPIGS()
    var data:SimpleStringProperty= SimpleStringProperty()


    override fun initialize(location: java.net.URL?, resources: ResourceBundle?) {
        blockPanel()

        /*
        permet au binding d'éviter d'afficher et les valeurs null et les valeurs vide.
         */
        val condition = data.isNotNull
            .and(data.isNotEmpty)
        labelConsoInstant!!.textProperty().bind(Bindings.`when`(condition)
            .then(data)
            .otherwise("")
        )


        ButtonAdmin!!.onAction =
            javafx.event.EventHandler { event: javafx.event.ActionEvent? ->
                ouvrirNouvelleFenetre()
            }

        launchTimers()


    }

    /**
     * Méthode servant à bloquer les séparateurs des splits panels.
     */
    private fun blockPanel() {
        splitplane1?.dividers?.first()?.positionProperty()
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
        timer.runThreadRoutePrix()
        timer.runThreadParamOnduleur()
        timer.runThreadWarningOnduleur()
        timer.runThreadEnvoieWarningBddDistante()
        threadAffichageDynamique()
    }

    private fun threadAffichageDynamique(){
        val timerTaskConso: TimerTask = object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    data.set(mQPIGS.AC_output_apparent_powerProperty().value.toString())
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskConso, 0, 1000)

        val timerTaskGraph = object : TimerTask() {
            override fun run() {
                Platform.runLater {

                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskGraph, 0, 1000)
    }

    private fun ouvrirNouvelleFenetre(){
            val loader = FXMLLoader(javaClass.getResource("/com.btsciel/login-view.fxml"))
            val root = loader.load<Parent>()

            val stage = Stage()
            stage.title = "Login"
            stage.scene = Scene(root)
            stage.isResizable = false
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show()

    }


}