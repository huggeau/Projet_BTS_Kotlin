package com.btsciel.controller

import com.btsciel.Main
import com.btsciel.RequeteBdd.DataBaseRequest
import com.btsciel.Utils.TimerData
import com.btsciel.Utils.Wks
import com.btsciel.models.*
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

    private val absolutePositionPanel2 = .25
    private val timer_binding: Timer = Timer()
    private val series = XYChart.Series<String, Number>()
    private var gain = 0.0
    private lateinit var application : Main

    @FXML
    var anchorPane: AnchorPane? = null
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
    var splitplane2: SplitPane? = null
    @FXML
    var comboBoxGraphe: ComboBox<String>? = null
    @FXML
    var ButtonWarnings: Button? = null

    private var wks: Wks = Wks()
    private var timer: TimerData = TimerData(wks)
    var mQPIGS: ModelQPIGS = wks.getModelQPIGS()
    var mQPIWS: ModelQPIWS = wks.getModelQPIWS()
    var data:SimpleStringProperty= SimpleStringProperty("")
    var batteryCapacity = SimpleStringProperty("")
    var PV_Input_for_battery = SimpleStringProperty("")
    var db: DataBaseRequest = DataBaseRequest()
    private var secondeData: SimpleStringProperty = SimpleStringProperty()
    private lateinit var condition: BooleanBinding


    override fun initialize(location: java.net.URL?, resources: ResourceBundle?) {
        blockPanel()

        comboBoxGraphe?.items!!.addAll("gain journalière", "gain mensuelle", "gain annuelle")

        comboBoxGraphe?.setOnAction {
            val selected = comboBoxGraphe?.value
            when(selected) {
                "gain journalière" -> updateChartJournalier()
                "gain mensuelle" -> updateChartMensuel()
                "gain annuelle" -> updateChartAnnuel()
            }
        }


        idYAxis?.apply {
            label = "centimes d'euro"
        }
        idLineChart?.apply {
            title = "Gain"
            data.add(series)
        }
        series.name = "donnée en temps réel"

        bindingLabel()

        ButtonAdmin!!.setOnAction { event ->
                ouvrirFenetreLogin()
        }

        ButtonWarnings!!.setOnAction { event ->
            ouvrirFenetreWarnings()
        }

        launchTimers()
    }
    /**
     * Méthode servant à bloquer les séparateurs des splits panels.
     */
    private fun blockPanel() {
        splitplane2?.dividers?.first()?.positionProperty()
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
        val gainJournalier = db.getGainJournalier()
        val timerTaskGainJournalier = object : TimerTask() {
            override fun run() {
                Platform.runLater{
                    series.data.clear()
                    val newValue = calculGain(gainJournalier, gain)
                    db.updateGainJournalier(gain.toString())

                    val model = db.recupInfoOnduleur()?.get(2)?.let { ModelGainJournalier(newValue, it) }

                    wks.postGainJournalier(model)
                    series.data.add(XYChart.Data(gain.toString(),newValue))

                    if(series.data.size > 50){
                        series.data.removeAt(0)
                    }
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskGainJournalier, 0, 86400000)
    }

    private fun updateChartMensuel(){
        val gainMensuel = db.getGainMensuel()
        val timerTaskGainMensuel = object : TimerTask() {
            override fun run() {
                Platform.runLater{
                    series.data.clear()
                    val newValue = calculGain(gainMensuel, gain)
                    db.updateGainMensuel(gain.toString())

                    val model = db.recupInfoOnduleur()?.get(2)?.let { ModelGainMensuel(newValue, it) }
                    wks.postGainMensuel(model)

                    series.data.add(XYChart.Data(gain.toString(),newValue))

                    if(series.data.size > 50){
                        series.data.removeAt(0)
                    }
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskGainMensuel, 0, 2592000000)
    }

    private fun updateChartAnnuel(){
        val gainAnnuler = db.getGainAnnuel()
        val timerTaskGainAnnuel = object : TimerTask() {
            override fun run() {
                Platform.runLater{
                    series.data.clear()
                    val newValue = calculGain(gainAnnuler, gain)
                    db.updateGainAnnuel(gain.toString())

                    val model = db.recupInfoOnduleur()?.get(2)?.let { ModelGainAnnuel(newValue, it) }
                    wks.postGainAnnuel(model)

                    series.data.add(XYChart.Data(gain.toString(),newValue))

                    if(series.data.size > 50){
                        series.data.removeAt(0)
                    }
                }
            }
        }
        timer_binding.scheduleAtFixedRate(timerTaskGainAnnuel, 0, 315360000000)
    }

    private fun calculGain(newestGain: Double, latestGain: Double) : Double {
        return newestGain - latestGain
    }
    /**Méthode ouvrant la fenêtre de login*/
    private fun ouvrirFenetreLogin(){
        val loader = FXMLLoader(javaClass.getResource("/com.btsciel/login-view.fxml"))
        val root = loader.load<Parent>()

        val stage = Stage()
        stage.title = "Login"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.show()
    }

    private fun ouvrirFenetreWarnings(){
        application.scanView(mQPIWS)
    }
    /**permet au binding d'éviter d'afficher et les valeurs null et les valeurs vide.*/
    private fun bindingLabel(){

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
            .otherwise("")
        )

        condition = PV_Input_for_battery.isNotNull
            .and(PV_Input_for_battery.isNotEmpty)
        labelPVInputForBattery!!.textProperty().bind(Bindings.`when`(condition)
            .then(PV_Input_for_battery)
            .otherwise("")
        )
    }

    fun setMainApp(main: Main) {
        application = main

    }
}