package com.btsciel.controller

import com.btsciel.RequeteBdd.DataBaseRequest
import com.btsciel.models.ModelInfoOnduleur
import com.btsciel.retrofit.Api_Retrofit
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.Stage
import retrofit2.Call
import retrofit2.Response
import java.net.URL
import java.util.*

class ParametresController : Initializable {
    @FXML
    var buttonCancelParam: Button? = null
    @FXML
    var buttonValidateParam: Button? = null
    @FXML
    var textFieldLatitude: TextField? = null
    @FXML
    var textFieldAddServeur: TextField? = null
    @FXML
    var textFieldLongitude: TextField? = null
    @FXML
    var textFieldMAC: TextField? = null

    var dataBaseRequest: DataBaseRequest? = DataBaseRequest()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        textFieldMAC?.text = dataBaseRequest?.recupInfoOnduleur()?.get(2)

        buttonCancelParam?.setOnAction { event ->
            val stage = buttonCancelParam!!.scene.window as Stage
            stage.close()
            println("boutton cancer clique")
        }

        buttonValidateParam?.setOnAction { event ->
            val retrofit = Api_Retrofit()
            val modelInfoOnduleur = ModelInfoOnduleur(textFieldLatitude!!.text, textFieldLongitude!!.text, textFieldMAC!!.text)
            retrofit.api.postInfo(modelInfoOnduleur)?.enqueue(object : retrofit2.Callback<Api_Retrofit?> {

                override fun onResponse(p0: Call<Api_Retrofit?>, p1: Response<Api_Retrofit?>) {
                    // TODO: don't need anything in return
                }

                override fun onFailure(p0: Call<Api_Retrofit?>, p1: Throwable) {
                    System.err.println(p1.message)
                }
            })
        }
    }
}