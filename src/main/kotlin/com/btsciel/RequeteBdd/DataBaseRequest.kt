package com.btsciel.RequeteBdd

import java.sql.*


class DataBaseRequest {
    /** Chemin vers la bdd pour le connecteur (à modifier, car chemin absolu sur windows pas linux) */ //todo
    //     private String connector = "jdbc:sqlite:/home/install/BddLocal.sqlite";
    private val connector = "jdbc:sqlite:C:\\Users\\hugo\\OneDrive\\Projet\\BddLocal\\BddLocal.sqlite"

    /** Vérifie si la bdd est joignable */
    var conn: Connection? = DriverManager.getConnection(connector)

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /** Sert à insérer les data receptionné puis calculé dans la bdd locale. */
    @Throws(SQLException::class)
    fun insertData(energie: String) {
        val timestamp = Timestamp(System.currentTimeMillis())

        if (conn != null) {
            val query = "UPDATE Data SET Tarif = ?, Energie = ?, horodatage = ? WHERE id=4"
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
            val query = "UPDATE Prix SET prix=? WHERE id=1"
            val ps = conn!!.prepareStatement(query)
            ps.setDouble(1, prix!!.toDouble())
            ps.executeUpdate()
        }
    }

    /** Récupère les info de l'onduleur stocké dans la bdd. */
    @Throws(SQLException::class)
    fun recupInfoOnduleur(): Array<String?>? {
        val tabInfo = arrayOfNulls<String>(3)
        if (conn != null) {
            val query = "SELECT latitude, longitude, AddMac FROM information WHERE id=1"
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
}