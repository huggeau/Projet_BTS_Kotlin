package com.btsciel

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.IOException

class Main : Application() {
    @Throws(IOException::class)
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/com.btsciel/hello-view.fxml"))
        val scene = Scene(fxmlLoader.load())
        stage.title = "Onduleur"
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }
}

fun main(args: Array<String>){
    Application.launch(Main::class.java, *args)
}