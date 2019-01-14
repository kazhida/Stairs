package com.abplus.stairs.core

import com.abplus.stairs.core.elements.Circuit
import com.abplus.stairs.core.elements.Device

class LogicUnit(
    val devices: Array<Device.AbstractAllocator<Device>>
) {
    val circuits: List<Circuit> = ArrayList()

    fun circuit(circuit: Circuit) {
        if (circuits is MutableList) {
            circuits.add(circuit)
        }
    }

    fun circuit(circuit: ()->Circuit) {
        circuit(circuit())
    }
}
