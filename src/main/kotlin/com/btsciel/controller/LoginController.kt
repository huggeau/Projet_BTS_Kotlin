package com.btsciel.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import java.net.URL
import java.util.*

class LoginController : Initializable{
    @FXML
    var buttonValidate: Button? = null
    @FXML
    var buttonCancel: Button? = null
    @FXML
    var txtFieldPassword: TextField? = null
    @FXML
    var txtFieldUsername: TextField? = null


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        TODO("Not yet implemented")


    }


}