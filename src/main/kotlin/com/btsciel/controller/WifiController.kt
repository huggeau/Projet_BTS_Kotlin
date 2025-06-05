package com.btsciel.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.beans.EventHandler
import java.net.URL
import java.util.ResourceBundle

class WifiController : Initializable {
    @FXML
    private var txtfieldNomWifi : TextField? = null
    @FXML
    private var txtfieldMdP : TextField? = null
    @FXML
    private var buttonValidating : Button? = null
    @FXML
    private var buttonCanceling : Button? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        buttonCanceling?.setOnAction {
            val stage = buttonCanceling!!.scene.window as Stage
            stage.close()
        }

        buttonValidating?.setOnAction {
            val stage = buttonValidating!!.scene.window as Stage
            stage.close()
        }
    }
}