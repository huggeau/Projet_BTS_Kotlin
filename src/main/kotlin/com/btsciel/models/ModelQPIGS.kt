package com.btsciel.models

import javafx.beans.property.SimpleStringProperty

class ModelQPIGS {
    var grid_voltage: String? = null
    var grid_frequency: String? = null
    var aC_output_voltage: String? = null
    var aC_output_frequency: String? = null
    private val AC_output_apparent_power = SimpleStringProperty(" ")
    var aC_output_active_power: String? = null
    var output_load_percent: String? = null
    var bus_voltage: String? = null
    var battery_voltage: String? = null
    var battery_charging_current: String? = null
    var battery_capacity: String? = null
    var inverter_heat_sink_temperature: String? = null
    var pv_input_current_for_battery: String? = null
    var pv_input_voltage_1: String? = null
    var battery_voltage_fromm_scc: String? = null
    var battery_discharge_current: String? = null
    var device_status1: String? = null
    var battery_voltage_offset_for_fans_on: String? = null
    var eeprom_version: String? = null
    var pv_charging_power: String? = null
    var device_status2: String? = null


    /** Celles qui sont utiles */
    fun getAC_output_apparent_power(): String {
        return AC_output_apparent_power.value
    }

    fun setAC_output_apparent_power(AC_output_apparent_power: String?) {
        this.AC_output_apparent_power.set(AC_output_apparent_power)
    }

    fun AC_output_apparent_powerProperty(): SimpleStringProperty {
        return AC_output_apparent_power
    }
    /** Fin de celles qui sont utiles */
}