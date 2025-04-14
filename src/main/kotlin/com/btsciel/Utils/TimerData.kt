package com.btsciel.Utils

import jssc.SerialPortException
import java.sql.SQLException
import java.util.*

class TimerData(wks: Wks) {
    private var timer: Timer = Timer()
    var wks: Wks = wks

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

    /**Thread qui permet d'envoyer la requête QPIRI a l'onduleur.*/
    fun runThreadParamOnduleur(){
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                wks.requeteQPIRI()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 1000, 6000)
    }

    /**Thread qui permet d'envoyer la requête QPIWS à l'onduleur*/
    fun runThreadWarningOnduleur(){
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                wks.requeteQPIWS()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 2000, 6000)
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
                try {
                    wks.envoieConsoOnduleur()
                } catch (e: SQLException) {
                    System.err.println(e.message)
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 600000, 600000)
    }

    fun runThreadEnvoieWarningBddDistante(){
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                wks.envoieWarningOnduleur()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 2500, 60000)
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

    fun runThreadRouteParam(){
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                wks.getWarningOnduleur()
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 6000)
    }
}