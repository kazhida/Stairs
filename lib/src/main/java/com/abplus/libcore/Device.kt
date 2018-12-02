package com.abplus.libcore

/**
 * PLCを構成するデバイス（接点やメモリ、タイマ、カウンタ）
 */
@Suppress("unused")
sealed class Device(
    val name: String,
    val address: Int
) {
    /**
     * 入力デバイスは、出力として使用できないので、それを区別するためのインターフェース
     */
    interface Outable {
        fun out(condition: ()->Circuit)
    }

    /**
     * 論理値のデバイス
     */
    sealed class Bit(name: String, address: Int) : Device(name, address) {

        /**
         * PLCではtrue/falseの代わりにON/OFFを使うのが普通
         * ま、true/falseに慣れてくれた方がいいんだけど
         */
        companion object {
            const val ON: Boolean = true
            const val OFF: Boolean = false
        }

        /**
         * 入力接点
         * 出力の対象にはできない
         */
        class Input(name: String, address: Int) : Bit(name, address) {

            class Allocator(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Input>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word?): Input = Input(name, address)
            }
        }

        /**
         * 入力以外の接点
         */
        class Contact(name: String, address: Int) : Bit(name, address), Outable {

            private var condition: (()->Circuit)? = null

            override fun out(condition: ()->Circuit) {
                this.condition = condition
            }

            class Allocator(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Contact>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word?): Contact = Contact(name, address)
            }
        }

        /**
         * メモリの値比較（疑似接点）
         */
        class Compare(val left: Word.Memory, val type: Type, val right: Word.Memory) : Bit("", 0) {
            enum class Type {
                EQ,
                NE,
                LT,
                LE,
                GT,
                GE
            }
        }

        /**
         * メモリのビット位置を接点とみなす
         * todo: 実際にはMemoryデバイスと絡んだもう少し複雑なものになるはず
         *
         */
        class BitOf(@Suppress("unused") val memory: Word.Memory, val bit: Int) : Bit(memory.name, memory.address), Outable {

            private var condition: (()->Circuit)? = null

            override fun out(condition: ()->Circuit) {
                this.condition = condition
            }
        }

        /**
         * タイマの接点
         * todo: 実際にはTimerデバイスと絡んだもう少し複雑なものになるはず
         */
        class TimerContact(val parent: Device, name: String, address: Int) : Bit(name, address), Outable {
            private var condition: (()->Circuit)? = null

            override fun out(condition: ()->Circuit) {
                this.condition = condition
            }
        }

        /**
         * カウンタの接点
         * todo: 実際にはCounterデバイスと絡んだもう少し複雑なものになるはず
         */
        class CounterContact(val parent: Device, name: String, address: Int) : Bit(name, address), Outable {
            private var condition: (()->Circuit)? = null

            override fun out(condition: ()->Circuit) {
                this.condition = condition
            }
        }
    }

    /**
     * 値を持つデバイス
     */
    sealed class Word(name: String, address: Int) : Device(name, address) {

        /**
         * メモリ
         */
        class Memory(name: String, address: Int) : Word(name, address) {

            class Allocator(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Memory>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word?): Memory = Memory(name, address)
            }
        }

        /**
         * 定数
         */
        class Constant(val value: Int, name: String = "") : Word(name, 0)

        /**
         * タイマの現在値
         * todo: 実際にはTimerデバイスと絡んだもう少し複雑なものになるはず
         */
        class TimerCurrent(val parent: Device, name: String, address: Int) : Word(name, address)

        /**
         * カウンタの現在値
         * todo: 実際にはCounterデバイスと絡んだもう少し複雑なものになるはず
         */
        class CounterCurrent(val parent: Device, name: String, address: Int) : Word(name, address)
    }

    /**
     * タイマ
     */
    class Timer(name: String, address: Int, val target: Word) : Device(name, address), Outable {

        private var condition: (()->Circuit)? = null
        private var start: Long = 0

        fun out(now: Long, condition: ()->Circuit) {
            start = now
            out(condition)
        }

        override fun out(condition: ()->Circuit) {
            this.condition = condition
        }

        // todo: 実際にはもっと複雑なものになるはず
        val contact: Bit get() = Bit.TimerContact(this, name, address)
        val current: Word get() = Word.TimerCurrent(this, name, address)

        class Allocator(name: String, size: Int, private val target: Word, origin: Int = 0) : AbstractAllocator<Timer>(name, size, origin) {
            override fun create(name: String, address: Int, target: Word?): Timer = Timer(name, address, target!!)
        }
    }

    /**
     * カウンタ
     */
    class Counter(name: String, address: Int, val target: Word) : Device(name, address), Outable {

        private var condition: (()->Circuit)? = null

        override fun out(condition: ()->Circuit) {
            this.condition = condition
        }

        // todo: 実際にはもっと複雑なものになるはず
        val contact: Bit get() = Bit.TimerContact(this, name, address)
        val current: Word get() = Word.TimerCurrent(this, name, address)

        class Allocator(name: String, size: Int, private val target: Word, origin: Int = 0) : AbstractAllocator<Counter>(name, size, origin) {
            override fun create(name: String, address: Int, target: Word?): Counter = Counter(name, address, target!!)
        }
    }

    // TimerとCounterを分ける意味があるのか？
    // そもそも、カウンタの用途ってあるのか？

    abstract class AbstractAllocator<T : Device>(val name: String, size: Int, origin: Int) {

        class OverFlowError(name: String): Error("Too many allocate for '$name'")

        private val max = size + origin
        private var idx = origin

        fun allocate(target: Word? = null): T {
            if (idx < max) {
                return create(name, idx++, target)
            } else {
                throw OverFlowError(name)
            }
        }

        fun allocate(skipTo: Int, target: Word? = null): T {
            if (idx < skipTo && skipTo < max) {
                idx = skipTo
                return create(name, idx++, target)
            } else {
                throw OverFlowError(name)
            }
        }

        protected abstract fun create(name: String, address: Int, target: Word?): T
    }
}
