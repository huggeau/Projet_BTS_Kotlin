package com.btsciel.RequeteBdd

import java.sql.*


class DataBaseRequest {
    /** Chemin vers la bdd pour le connecteur */
    // TODO: changer le chemin d'accès a la bdd local
//    private val connector = "jdbc:sqlite:/home/install/BddLocal.sqlite";
    private val connector = "jdbc:sqlite:C:\\Users\\hugo\\OneDrive\\Projet\\BddLocal\\BddLocal.sqlite"

    /** Vérifie si la bdd est joignable */
    private var conn: Connection? = DriverManager.getConnection(connector)

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /** Sert à insérer les data receptionné puis calculé dans la bdd locale. */
    @Throws(SQLException::class)
    fun insertData(energie: String) {
        val timestamp = Timestamp(System.currentTimeMillis())

        if (conn != null) {
            val query = "UPDATE Data SET Tarif = ?, Energie = ?, horodatage = ?"
            val ps = conn!!.prepareStatement(query)
            ps.setDouble(1, recupPrix())
            ps.setDouble(2, energie.toDouble())
            ps.setString(3, timestamp.toString())
            ps.executeUpdate()
        }
    }

    /** Sert à récupérer le prix du kwh dans la bdd */
    @Throws(SQLException::class)
    fun recupPrix(): Double {
        var prix: String? = null
        if (conn != null) {
            val query = "SELECT prix FROM Prix"
            val ps = conn!!.prepareStatement(query)
            val rs = ps.executeQuery()
            while (rs.next()) {
                prix = rs.getString(1)
            }
            checkNotNull(prix)
            return prix.toDouble()
        }
        return 0.0
    }

    /** Sert à mettre à jour le prix dans la bdd. */
    @Throws(SQLException::class)
    fun updatetPrix(prix: String?) {
        if (conn != null) {
            val query = "UPDATE Prix SET prix=?"
            val ps = conn!!.prepareStatement(query)
            ps.setDouble(1, prix!!.toDouble())
            ps.executeUpdate()
        }
    }

    /** Récupère les infos de l'onduleur stocké dans la bdd. */
    @Throws(SQLException::class)
    fun recupInfoOnduleur(): Array<String?>? {
        val tabInfo = arrayOfNulls<String>(3)
        if (conn != null) {
            val query = "SELECT latitude, longitude, AddMac FROM information"
            val ps = conn!!.prepareStatement(query)
            val rs = ps.executeQuery()
            while (rs.next()) {
                tabInfo[0] = rs.getString(1)
                tabInfo[1] = rs.getString(2)
                tabInfo[2] = rs.getString(3)
            }
            return tabInfo
        }
        return null
    }

    /** Sert à récupérer la conso de la bdd, afin de l'envoyer à la bdd distante. */
    @Throws(SQLException::class)
    fun recupPostConso(): Array<String?>? {
        val tabInfo = arrayOfNulls<String>(3)
        if (conn != null) {
            val query = "SELECT Tarif, Energie, horodatage FROM Data"
            val ps = conn!!.prepareStatement(query)
            val rs = ps.executeQuery()
            while (rs.next()) {
                tabInfo[0] = rs.getString(1)
                tabInfo[1] = rs.getString(2)
                tabInfo[2] = rs.getString(3)
            }
            return tabInfo
        }
        return null
    }

    /** Sert à mettre à jour les info de l'onduleur*/
    @Throws(SQLException::class)
    fun insertParam(latitude: String?, longitude: String?, addMac: String?){
        if (conn != null) {
            val query = """
                UPDATE information SET  latitude=?, longitude=?, AddMac=?
                 """.trimIndent()

            val ps = conn!!.prepareStatement(query)
            ps.setDouble(1, latitude!!.toDouble())
            ps.setDouble(2, longitude!!.toDouble())
            ps.setString(3, addMac)
            ps.executeUpdate()
        }
    }

    fun updateGainJournalier(gain: String) {
        if(conn != null) {
            val query = "UPDATE gain_journalier SET  conso=?"
            val ps = conn!!.prepareStatement(query)
            ps.setString(1, gain)
            ps.executeUpdate()
        }
    }

    fun getGainJournalier(): Double {
        if(conn != null) {
            val query = "SELECT conso FROM gain_journalier"
            val ps = conn!!.prepareStatement(query)
            val rs = ps.executeQuery()
            return rs.getDouble("conso")
        }
        return 0.0
    }

    fun updateGainMensuel(gain: String) {
        if(conn != null) {
            val query = "UPDATE gain_mensuel SET  conso=?"
            val ps = conn!!.prepareStatement(query)
            ps.setString(1, gain)
            ps.executeUpdate()
        }
    }

    fun getGainMensuel(): Double {
        if(conn != null) {
            val query = "SELECT conso FROM gain_mensuel"
            val ps = conn!!.prepareStatement(query)
            val rs = ps.executeQuery()
            return rs.getDouble("conso")
        }
        return 0.0
    }

    fun updateGainAnnuel(gain: String) {
        if(conn != null) {
            val query = "UPDATE gain_annuel SET  gain=?"
            val ps = conn!!.prepareStatement(query)
            ps.setString(1, gain)
            ps.executeUpdate()
        }
    }

    fun getGainAnnuel(): Double {
        if(conn != null) {
            val query = "SELECT gain FROM gain_annuel"
            val ps = conn!!.prepareStatement(query)
            val rs = ps.executeQuery()
            return rs.getDouble("conso")
        }
        return 0.0
    }
}