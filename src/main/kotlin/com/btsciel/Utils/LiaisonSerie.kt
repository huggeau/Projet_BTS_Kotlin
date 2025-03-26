package com.btsciel.Utils

import jssc.*


/**
 * @author Michael
 */
open class LiaisonSerie : SerialPortEventListener {
    var serialPort: SerialPort? = null

    /**
     * Methode qui stocke les noms des ports disponibles dans un ArrayList
     *
     * @return le ArrayList
     */
    fun listerLesPorts(): ArrayList<*> {
        val liste = ArrayList<String>()
        val listePorts = SerialPortList.getPortNames()
        for (i in listePorts.indices) {
            liste.add(listePorts[i].toString())
        }
        return liste
    }


    //******************************************************************************
    //******************************************************************************
    override fun serialEvent(event: SerialPortEvent) {
    }


    //******************************************************************************
    //******************************************************************************
    /**
     * Methode qui initialise l'objet serialPort avec en paramétre
     * le portDeTravail
     */
    @Throws(SerialPortException::class)
    fun initCom(portDeTravail: String?) {
        this.serialPort = SerialPort(portDeTravail)
    }


    //******************************************************************************
    //******************************************************************************
    /**
     * Méthode de configuration et ouvre la liaison série
     *
     * @param vitesse
     * @param donnees
     * @param parite
     * @param stop
     */
    fun configurerParametres(vitesse: Int, donnees: Int, parite: Int, stop: Int) {
        try {
            serialPort!!.openPort() //ouvre port com.btsciel
            serialPort!!.setParams(vitesse, donnees, stop, parite)
            val mask = SerialPort.MASK_RXCHAR
            //this.serialPort.setEventsMask(mask);//Set mask
            serialPort!!.addEventListener(this, mask) //Add SerialPortEventListener
        } catch (ex: SerialPortException) {
            println(ex)
        }
    }

    //******************************************************************************
    //******************************************************************************
    /**
     * Méthode fermant le port série ouvert et les flux
     */
    fun fermerPort() {
        try {
            // fermeture du port
            if (serialPort!!.isOpened) {
                serialPort!!.removeEventListener()
                serialPort!!.closePort()
            }
        } catch (ex: SerialPortException) {
            println(ex)
        }
    }

    //******************************************************************************
    //******************************************************************************
    /**
     * Méthode qui lit dans le buffer de réception le nbs de bytes recus
     *
     */
    fun detecteSiReception(): Int {
        var nbsOctect = 0
        try {
            nbsOctect = serialPort!!.inputBufferBytesCount
            //    System.out.println("getInputBufferBytesCount="+nbsOctect);
        } catch (ex: SerialPortException) {
            println(ex)
        }
        return nbsOctect
    }

    //******************************************************************************
    //******************************************************************************
    /**
     * Methode qui lit un octet sur le port
     *
     */
    fun lire(): Byte {
        val octetLu: ByteArray
        try {
            octetLu = serialPort!!.readBytes(1)
            //System.out.println(new String(octetLu));
            return octetLu[0]
        } catch (ex: SerialPortException) {
            println(ex)
        }
        return 0
    }

    //******************************************************************************
    //******************************************************************************
    /**
     * Methode qui lit une trame d'octet sur le port
     *
     */
    fun lireTrame(longeur: Int): ByteArray? {
        try {
            var trameLue: ByteArray? = ByteArray(longeur)
            trameLue = serialPort!!.readBytes(longeur)
            return trameLue
        } catch (ex: SerialPortException) {
            println(ex)
        }
        return null
    }
    //******************************************************************************
    //******************************************************************************
    /**
     * Methode qui écrit un octet sur le port
     *
     */
    fun ecrire(b: ByteArray?) {
        try {
            serialPort!!.writeBytes(b)
        } catch (ex: SerialPortException) {
            println(ex)
        }
    }

    fun ecrire(b: Byte) {
        try {
            serialPort!!.writeByte(b)
        } catch (ex: SerialPortException) {
            println(ex)
        }
    }
}
