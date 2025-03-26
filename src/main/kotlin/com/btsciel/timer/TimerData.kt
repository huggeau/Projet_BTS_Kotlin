package com.btsciel.timer

import com.btsciel.Utils.Wks
import com.btsciel.models.ModelQPIGS
import javafx.application.Platform
import javafx.beans.property.StringProperty
import jssc.SerialPortException
import java.lang.String
import java.sql.SQLException
import java.util.*

class TimerData(wks: Wks) {
    var timer: Timer = Timer()
    var wks: Wks = wks
    var mQpigs: ModelQPIGS? = null


    /** Test si le connecteur est bien installé et se connecte à la bdd. */
    init {
        try {
            // Initialise les infos du port pour la liaison série.
            this.wks.initCom("com5")

            //voici la ligne qu'il faudra mettre a la place du com5 quand l'appli sera sur le linux.
//             wks.initCom("/dev/ttyUSB0");
            //todo
            this.wks.configurerParametres(2400, 8, 0, 1)
        } catch (e: SerialPortException) {
            System.err.println(e.message)
        }
    }

    /** Répète la capture de donnée toute les 1000ms (soit 1s) d'intervalle. */
    fun runThreadRecupDataOnduleur() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                wks.requeteQPIGS()
                wks.putDataInArray()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 6000)
    }

    /** Thread qui permet de faire la moyenne des données reçue avant de les mettre dans la bdd. */
    fun runThreadMoyenneEnergie() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                try {
                    wks.calculMoyenneEnergie()
                } catch (e: SQLException) {
                    System.err.println(e.message)
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 60000, 60000)
    }

    /**
     * Thread qui permet d'envoyer les données a la bdd distante.
     */
    fun runThreadEnvoieBddDistante() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                //todo
                try {
                    wks.envoieConsoOnduleur()
                } catch (e: SQLException) {
                    System.err.println(e.message)
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 600000, 600000)
    }

    fun runThreadModifData(mQpigs: ModelQPIGS) {
        this.mQpigs = mQpigs
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                //todo
                val data: StringProperty = mQpigs.AC_output_apparent_powerProperty()
                Platform.runLater {
                    if (data != null) {
                        data.set(
                            String.valueOf(mQpigs.AC_output_apparent_powerProperty())
                        )
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 6000)
    }

    /** Thread qui va chercher le prix du kWh sur la bdd distante. */
    fun runThreadRoutePrix() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                //todo
                wks.recupPrix()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 300000)
    }
}