package com.btsciel.controller

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
    val buttonCancel: Button? = null
    @FXML
    val buttonValidate: Button? = null
    @FXML
    val textFieldLattitude: TextField? = null
    @FXML
    val textFieldAddServeur: TextField? = null
    @FXML
    val textFieldLongitude: TextField? = null
    @FXML
    val textFieldMAC: TextField? = null


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        buttonCancel?.setOnAction { event ->
            val stage = buttonCancel.scene.window as Stage
            stage.close()
        }

        buttonValidate?.setOnAction { event ->
            val retrofit = Api_Retrofit()
            val modelInfoOnduleur = ModelInfoOnduleur(textFieldLattitude!!.text, textFieldLongitude!!.text, textFieldMAC!!.text)
            retrofit.api.postInfo(modelInfoOnduleur)?.enqueue(object : retrofit2.Callback<Api_Retrofit?> {

                override fun onResponse(p0: Call<Api_Retrofit?>, p1: Response<Api_Retrofit?>) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(p0: Call<Api_Retrofit?>, p1: Throwable) {
                    TODO("Not yet implemented")
                }
            })

        }
    }
}