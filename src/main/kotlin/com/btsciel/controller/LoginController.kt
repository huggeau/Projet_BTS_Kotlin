package com.btsciel.controller

import com.btsciel.Main
import com.btsciel.models.ModelLoginAdmin
import com.btsciel.retrofit.Api_Retrofit
import com.sun.javafx.scene.control.skin.FXVK
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.Modality
import javafx.stage.Stage
import retrofit2.Call
import retrofit2.Response
import java.net.URL
import java.util.*

class LoginController : Initializable {
    @FXML
    var buttonValidateLogin: Button? = null
    @FXML
    var buttonCancelLogin: Button? = null
    @FXML
    var txtFieldPassword: TextField? = null
    @FXML
    var txtFieldUsername: TextField? = null


    override fun initialize(location: URL?, resources: ResourceBundle?) {



        buttonCancelLogin?.setOnAction { event ->
            val stage = buttonCancelLogin!!.scene.window as Stage
            stage.close()
        }

        buttonValidateLogin?.setOnAction { event ->
            loginValidating()
        }
    }

    private fun loginValidating(){
        val retrofit = Api_Retrofit()
        val modelLoginAdmin = ModelLoginAdmin(txtFieldUsername!!.text, txtFieldPassword!!.text)
        retrofit.api.postLogin(modelLoginAdmin)?.enqueue(object : retrofit2.Callback<Api_Retrofit?> {
            override fun onResponse(call: Call<Api_Retrofit?>, response: Response<Api_Retrofit?>) {
                if (response.isSuccessful && response.body() != null){
                    Platform.runLater{
                        val stageLogin = buttonValidateLogin!!.scene.window as Stage
                        stageLogin.close()

                        val loader = FXMLLoader(javaClass.getResource("/com.btsciel/Parametres-view.fxml"))
                        val root = loader.load<Parent>()
                        val stage = Stage()
                        stage.title = "Paramètres onduleur"
                        stage.scene = Scene(root)
                        stage.isResizable = false
                        stage.initModality(Modality.APPLICATION_MODAL)
                        stage.show()
                    }
                }
                else{
                    Platform.runLater {
                        txtFieldUsername!!.text = "Login Failed"
                        txtFieldPassword!!.text = "Password Failed"
                    }
                }
            }
            override fun onFailure(call: Call<Api_Retrofit?>, throwable: Throwable) {
                System.err.println(throwable)

            }
        })
    }
}