package com.abplus.stairs.stairing

import com.abplus.stairs.core.Stair

/**
 * Stair.Driverのリファレンス実装
 *
 * ソフトウェア・シーケンサでもある
 */
@Suppress("unused")
class Driver : Stair.Driver {

//    class Input(address: Int): Device.Bit.Input("X", address, false) {
//        var value: Boolean = false
//    }
//
//    class Output(address: Int): Device.Bit.Contact("Y", address, false) {
//        var value: Boolean = false
//    }
//
//    class Coil(address: Int): Device.Bit.Contact("M", address, false) {
//        var value: Boolean = false
//    }
//
//    class Register(address: Int): Device.Word.Memory<Device.MemorySize.Int32>("D", address) {
//        var value: Short = 0
//    }

//    abstract class BitValue<D: Device.Bit>(
//        val device: D
//    ) {
//        abstract val value: Boolean
//    }
//
//    enum class Mnemonic {
//        LOAD,
//        AND,
//        OR,
//        AND_BI,
//        OR_BI,
//        COMPARE,
//        MOVE,
//        BLOCK_MOVE,
//        FILL_MOVE
//    }
//
//    data class Command(
//        val operator: Mnemonic,
//        val not: Boolean = false,
//        val operands: Array<BitValue<Device.Bit>>? = null
//    ) : Stair.Command {
//
//        override fun step(stacks: Stair.Stacks) {
//            val value = if (operands == null || operands.size < 1) {
//                false
//            } else if (not) {
//                operands[0].value.not()
//            } else {
//                operands[0].value
//            }
//
//            when (operator) {
//                Mnemonic.LOAD -> stacks.stack.push(value)
//                Mnemonic.AND -> stacks.stack.push(stacks.stack.pop() && value)
//                Mnemonic.OR -> stacks.stack.push(stacks.stack.pop() || value)
//                Mnemonic.AND_BI -> stacks.stack.push(stacks.stack.pop() && stacks.stack.pop())
//                Mnemonic.OR_BI -> stacks.stack.push(stacks.stack.pop() || stacks.stack.pop())
//                Mnemonic.COMPARE -> stacks.stack.push()
//                Mnemonic.MOVE ->
//                Mnemonic.BLOCK_MOVE ->
//                Mnemonic.FILL_MOVE ->
//
//            }
//        }
//    }

    override fun compile(source: Stair): List<Stair.Command> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun run(commands: List<Stair.Command>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
