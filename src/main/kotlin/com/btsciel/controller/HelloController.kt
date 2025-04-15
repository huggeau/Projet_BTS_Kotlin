package com.btsciel.controller

import com.btsciel.RequeteBdd.DataBaseRequest
import com.btsciel.Utils.Wks
import com.btsciel.models.ModelQPIGS
import com.btsciel.Utils.TimerData
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
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
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.stage.Modality
import javafx.stage.Stage
import java.util.*

class HelloController : Initializable {

    private val absolutePositionPanel1 = .33
    private val absolutePositionPanel2 = .25
    private val timer_binding: Timer = Timer()
    private val series = XYChart.Series<String, Number>()
    private var gain = 0.0

    @FXML
    var anchorPane: AnchorPane? = null
    @FXML
    var TextFieldWarning: TextField? = null
    @FXML
    var labelConsoInstant: Label? = null
    @FXML
    var labelGainInstant: Label? = null
    @FXML
    var labelBatteryCapacity: Label? = null
    @FXML
    var labelPVInputForBattery: Label? = null
    @FXML
    var ButtonAdmin: Button? = null
    @FXML
    var idLineChart: LineChart<String, Number> ? = null
    @FXML
    var idXAxis: CategoryAxis? = null
    @FXML
    var idYAxis: NumberAxis? = null
    @FXML
    var splitplane1: SplitPane? = null
    @FXML
    var splitplane2: SplitPane? = null
    @FXML
    var comboBoxGraphe: ComboBox<String>? = null

    private var wks: Wks = Wks()
    private var timer: TimerData = TimerData(wks)
    var mQPIGS: ModelQPIGS = wks.getModelQPIGS()
    var data:SimpleStringProperty= SimpleStringProperty("")
    var batteryCapacity = SimpleStringProperty("")
    var PV_Input_for_battery = SimpleStringProperty("")
    var db: DataBaseRequest = DataBaseRequest()
    private var secondeData: SimpleStringProperty = SimpleStringProperty()
    private lateinit var condition: BooleanBinding


    override fun initialize(location: java.net.URL?, resources: ResourceBundle?) {
        blockPanel()

        comboBoxGraphe?.items!!.addAll("conso journalière", "conso mensuelle", "conso annuelle")

        comboBoxGraphe?.setOnAction {
            val selected = comboBoxGraphe?.value
            when(selected) {
                "conso journalière" -> updateChartJournalier()
                "conso mensuelle" -> updateChartMensuel()
                "conso annuelle" -> updateChartAnnuel()
            }
        }

        idYAxis?.apply {
            label = "euro"
        }
        idXAxis?.apply {
            label = "temps"
        }
        idLineChart?.apply {
            title = "Gain"
            data.add(series)
        }


        series.name = "donnée en temps réel"

        /*
        permet au binding d'éviter d'afficher et les valeurs null et les valeurs vide.
         */
        condition = data.isNotNull
            .and(data.isNotEmpty)
        labelConsoInstant!!.textProperty().bind(Bindings.`when`(condition)
            .then(data)
            .otherwise("")
        )

        condition = secondeData.isNotNull
            .and(secondeData.isNotEmpty)
        labelGainInstant!!.textProperty().bind(Bindings.`when`(condition)
            .then(secondeData)
            .otherwise("")
        )

        condition = batteryCapacity.isNotNull
            .and(batteryCapacity.isNotEmpty)
        labelBatteryCapacity!!.textProperty().bind(Bindings.`when`(condition)
            .then(batteryCapacity)
            .otherwise(""))

        condition = PV_Input_for_battery.isNotNull
            .and(PV_Input_for_battery.isNotEmpty)
        labelPVInputForBattery!!.textProperty().bind(Bindings.`when`(condition)
            .then(PV_Input_for_battery)
            .otherwise(""))


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
        timer.runThreadRouteParam()
        threadAffichageDynamique()
    }

    /**
     * Méthode permettant de modifier dynamiquement les différentes variables*/
    private fun threadAffichageDynamique(){
        val timerTaskConso: TimerTask = object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    data.set(mQPIGS.AC_output_active_powerProperty().value.toString())
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskConso, 0, 1000)

        val timerTaskGain = object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    val prix = db.recupPrix()
                    val conso = mQPIGS.getAC_output_active_power()

                    if(conso.isNotEmpty()){
                        gain =  prix * conso.toDouble()
                    }

                    secondeData.set("%.2f cts".format(gain))
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskGain, 0, 1000)

        val timerTaskBatteryCapacity = object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    batteryCapacity.set(mQPIGS.getBattery_capacity())
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskBatteryCapacity, 0, 1000)

        val timerTaskPVInputForBattery: TimerTask = object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    PV_Input_for_battery.set(mQPIGS.getPV_input_current_for_battery())
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskPVInputForBattery, 0, 1000)
    }

    /**
     * Voici les méthodes permettant de changer le graphe en fonction du gain à afficher
     * */
    private fun updateChartJournalier(){
        val timerTaskWarning = object : TimerTask() {
            override fun run() {
                Platform.runLater{
                    series.data.clear()
                    val newValue = gain
                    series.data.add(XYChart.Data(gain.toString(),newValue))

                    if(series.data.size > 50){
                        series.data.removeAt(0)
                    }
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskWarning, 0, 1000)
    }

    private fun updateChartMensuel(){
        val timerTaskWarning = object : TimerTask() {
            override fun run() {
                Platform.runLater{
                    series.data.clear()
                    val newValue = gain
                    series.data.add(XYChart.Data(gain.toString(),newValue))

                    if(series.data.size > 50){
                        series.data.removeAt(0)
                    }
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskWarning, 0, 1000)
    }

    private fun updateChartAnnuel(){
        val timerTaskWarning = object : TimerTask() {
            override fun run() {
                Platform.runLater{
                    series.data.clear()
                    val newValue = gain
                    series.data.add(XYChart.Data(gain.toString(),newValue))

                    if(series.data.size > 50){
                        series.data.removeAt(0)
                    }
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskWarning, 0, 1000)
    }

    /**Méthode ouvrant la fenêtre de login*/
    private fun ouvrirNouvelleFenetre(){
            val loader = FXMLLoader(javaClass.getResource("/com.btsciel/login-view.fxml"))
            val root = loader.load<Parent>()

            val stage = Stage()
            stage.title = "Login"
            stage.scene = Scene(root)
            stage.isResizable = false
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.show()

    }

    /**Méthode permettant de redimensionner de façon dynamique la scene principale ATTENTION pas fonctionnel*/
    private fun autoResizeScene(){
        val stage = anchorPane!!.scene.window as Stage
        stage.widthProperty().addListener { observable, oldValue, newValue ->
            anchorPane?.prefWidth = stage.width
        }
        stage.heightProperty().addListener { observable, oldValue, newValue ->
            anchorPane?.prefHeight = stage.height
        }

    }
}