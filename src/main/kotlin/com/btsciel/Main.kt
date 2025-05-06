package com.btsciel

import com.btsciel.controller.HelloController
import com.btsciel.controller.WarningsController
import com.btsciel.models.ModelQPIWS
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import org.comtel2000.keyboard.control.KeyBoardPopup
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder
import java.io.IOException
import java.util.*

class Main : Application() {
    private lateinit var primaryStage: Stage

    @Throws(IOException::class)
    override fun start(stage: Stage) {
        primaryStage = stage
        rootView()
    }

    private fun rootView(){
        val fxmlLoader = FXMLLoader(javaClass.getResource("/com.btsciel/hello-view.fxml"))
        val scene = Scene(fxmlLoader.load())
        primaryStage.title = "Onduleur"
        primaryStage.scene = scene
        primaryStage.isResizable = false

        val popup : KeyBoardPopup = KeyBoardPopupBuilder.create().initLocale(Locale.getDefault()).build()
        popup.registerScene(scene)
        popup.addGlobalFocusListener()
        popup.addGlobalDoubleClickEventFilter()

        primaryStage.show()

        val controller:HelloController = fxmlLoader.getController()
        controller.setMainApp(this)
    }

    fun scanView(model : ModelQPIWS){
        val loader = FXMLLoader(javaClass.getResource("/com.btsciel/warnings-view.fxml"))
        val scene = Scene(loader.load())
        val stage = Stage()
        stage.title = "Warnings"
        stage.scene = scene
        stage.isResizable = false
        stage.width = 150.0
        stage.height = 150.0

        val scanController = loader.getController<WarningsController>()
        scanController.setScanStage(stage)

        scanController.setModelQPIWS(model)

        stage.showAndWait()
    }
}

fun main(args: Array<String>){
    Application.launch(Main::class.java, *args)
}