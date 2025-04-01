package com.btsciel.controller

import com.btsciel.models.ModelLoginAdmin
import com.btsciel.retrofit.Api_Retrofit
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
    var buttonValidate: Button? = null
    @FXML
    var buttonCancel: Button? = null
    @FXML
    var txtFieldPassword: TextField? = null
    @FXML
    var txtFieldUsername: TextField? = null


    override fun initialize(location: URL?, resources: ResourceBundle?) {

        buttonCancel?.setOnAction { event ->
            val stage = buttonCancel!!.scene.window as Stage
            stage.close()
        }

        buttonValidate?.setOnAction { event ->
            val loader = FXMLLoader(javaClass.getResource("/com.btsciel/Parametres-view.fxml"))
            val root = loader.load<Parent>()
            val stage = Stage()
            stage.title = "Parametres onduleur"
            stage.scene = Scene(root)
            stage.isResizable = false
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.show()

            val stageLogin = buttonValidate!!.scene.window as Stage
            stageLogin.close()
            loginValidating()
        }
    }

    fun loginValidating(){
        val retrofit = Api_Retrofit()
        val modelLoginAdmin = ModelLoginAdmin(txtFieldUsername!!.text, txtFieldPassword!!.text)
        retrofit.api.postLogin(modelLoginAdmin)?.enqueue(object : retrofit2.Callback<Api_Retrofit?> {
            override fun onResponse(call: Call<Api_Retrofit?>, response: Response<Api_Retrofit?>) {
                if (response.isSuccessful && response.body() != null){

                    val loader = FXMLLoader(javaClass.getResource("/com.btsciel/Parametres-view.fxml"))
                    val root = loader.load<Parent>()
                    val stage = Stage()
                    stage.title = "Parametres onduleur"
                    stage.scene = Scene(root)
                    stage.isResizable = false
                    stage.initModality(Modality.APPLICATION_MODAL)
                    stage.show()

                    val stageLogin = buttonValidate!!.scene.window as Stage
                    stageLogin.close()
                }
            }
            override fun onFailure(call: Call<Api_Retrofit?>, throwable: Throwable) {
                System.err.println(throwable)

            }
        })
    }
}