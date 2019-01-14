package com.abplus.stairs.core

import com.abplus.stairs.core.elements.Circuit
import com.abplus.stairs.core.elements.Device


/**
 * PLCのCPU1台に相当するクラス
 */
@Suppress("unused")
open class Stair(
    val devices: Array<Device.DeviceAllocator<Device>>
) {
    val circuits: List<Circuit> = ArrayList()

    /**
     * 回路を追加する処理
     */
    fun circuit(circuit: Circuit) {
        if (circuits is MutableList) {
            circuits.add(circuit)
        }
    }

    /**
     * 回路を追加する処理
     */
    fun circuit(circuit: ()-> Circuit) {
        circuit(circuit())
    }

    /**
     * スタック
     */
    data class Stacks(
        val stack: MutableList<Boolean> = ArrayList(),
        val subStack: MutableList<Boolean> = ArrayList()
    )

    /**
     * PLCのILの抽象的表現
     *
     * ターゲットに応じて、内部表現も異なってくるので、
     * ここでは、step()によって、スタックを操作するということだけを決めている。
     */
    interface Command {
        /**
         * スタックを操作する処理
         */
        fun step(stacks: Stacks)

        /**
         * スタック操作のためのユーティリティ
         */
        fun MutableList<Boolean>.push(value: Boolean) = add(value)
        fun MutableList<Boolean>.pop(): Boolean = removeAt(size - 1)
        val MutableList<Boolean>.top: Boolean? get() = if (size > 0) get(size - 1) else null
    }

    /**
     * PLCを動作させるための抽象表現
     */
    interface Driver {

        /**
         * Stairの情報を元にコマンド列にコンパイルする処理
         */
        fun compile(source: Stair): List<Command>

        /**
         * コマンド列を実行する処理
         */
        fun run(commands: List<Command>)
    }
}
