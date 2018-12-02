package com.abplus.libcore

sealed class Circuit constructor (private val origin: Device.Bit) {

    private val serials = ArrayList<Parallel>()
    private val parallels = ArrayList<Device.Bit>()
    private var reserved: Device.Bit? = null

    abstract infix fun and(circuit: Circuit): Circuit
    abstract infix fun or(circuit: Circuit): Circuit
    abstract infix fun and(contact: Device.Bit): Circuit
    abstract infix fun or(contact: Device.Bit): Circuit

    private class Parallel(origin: Device.Bit) : Circuit(origin) {

        override infix fun and(circuit: Circuit): Circuit = apply {
            // todo
        }

        override infix fun or(circuit: Circuit): Circuit = apply {
            // todo
        }

        override infix fun and(contact: Device.Bit): Circuit = apply {
            // todo
        }

        override infix fun or(contact: Device.Bit): Circuit = apply {
            // todo
        }
    }

    private class Serial(origin: Device.Bit) : Circuit(origin) {

        override infix fun and(circuit: Circuit): Circuit = apply {
            // todo
        }

        override infix fun or(circuit: Circuit): Circuit = apply {
            // todo
        }

        override infix fun and(contact: Device.Bit): Circuit = apply {
            // todo
        }

        override infix fun or(contact: Device.Bit): Circuit = apply {
            // todo
        }
    }

    infix fun Device.Bit.and(circuit: Circuit): Circuit {
        TODO()
    }

    infix fun Device.Bit.or(circuit: Circuit): Circuit {
        TODO()
    }
}
