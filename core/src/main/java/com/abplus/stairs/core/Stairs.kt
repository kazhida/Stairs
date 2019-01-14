package com.abplus.stairs.core

class Stairs {
    val units: Map<String, LogicUnit> = HashMap()

    fun add(name: String, unit: LogicUnit) {
        if (units is MutableMap) {
            units[name] = unit
        }
    }
}
