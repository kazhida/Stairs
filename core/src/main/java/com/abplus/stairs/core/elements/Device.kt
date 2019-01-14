package com.abplus.stairs.core.elements

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
    interface Outable

    /**
     * PLCのメモリサイズ
     */
    enum class MemorySize {
        Int16,
        Int32,
        Int64,
        Float16,
        Float32,
        Float64
    }

    /**
     * 論理値のデバイス
     */
    sealed class Bit(name: String, address: Int, val inverse: Boolean = false) : Device(name, address), Condition {

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
        class Input(name: String, address: Int, inverse: Boolean) : Bit(name, address, inverse) {

            override val not: Condition get() = Input(name, address, !inverse)

            class Allocator(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Input>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word<MemorySize>?): Input = Input(name, address, false)
            }
        }

        /**
         * 入力以外の接点
         */
        class Contact(name: String, address: Int, inverse: Boolean) : Bit(name, address, inverse), Outable {

            override val not: Condition get() = Contact(name, address, !inverse)

            class Allocator(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Contact>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word<MemorySize>?): Contact = Contact(name, address, false)
            }
        }

        /**
         * メモリの値比較（疑似接点）
         */
        class Compare<M: MemorySize>(val left: Word<M>, val type: Type, val right: Word<M>) : Bit("", 0, false) {
            
            enum class Type {
                EQ, // ==
                NE, // !=
                LT, // <
                LE, // <=
                GT, // >
                GE  // >=
            }

            override val not: Condition get() = when (type) {
                Type.EQ -> Compare(left, Type.NE, right)
                Type.NE -> Compare(left, Type.EQ, right)
                Type.LT -> Compare(left, Type.GE, right)
                Type.LE -> Compare(left, Type.GT, right)
                Type.GT -> Compare(left, Type.LE, right)
                Type.GE -> Compare(left, Type.LT, right)
            }
        }

        /**
         * メモリのビット位置を接点とみなす
         */
        class BitOf<M: MemorySize>(val memory: Word.Memory<M>, val bit: Int, inverse: Boolean = false) : Bit(memory.name, memory.address, inverse), Outable {
            override val not: Condition get() = BitOf(memory, bit, !inverse)
        }
    }

    /**
     * 値を持つデバイス
     */
    sealed class Word<M: MemorySize>(name: String, address: Int) : Device(name, address) {

        /**
         * メモリ
         */
        class Memory<M: MemorySize>(name: String, address: Int) : Word<M>(name, address) {

            class Allocator<M: MemorySize>(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Memory<M>>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word<MemorySize>?): Memory<M> = Memory(name, address)
            }
        }

        /**
         * 定数
         */
        class Constant<M: MemorySize>(val value: Int, name: String = "") : Word<M>(name, 0)

        /**
         * タイマ
         */
        class Timer<M: MemorySize>(name: String, address: Int, val target: Word<M>) : Device.Word<M>(name, address), Outable {

            val contact: Bit get() = Bit.Compare(this, Bit.Compare.Type.GE, target)

            class Allocator<Timer>(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Device>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word<MemorySize>?): Device = Timer(name, address, target!!)
            }
        }

        /**
         * カウンタ
         */
        class Counter<M: MemorySize>(name: String, address: Int, val target: Word<M>) : Device.Word<M>(name, address), Outable {

            val contact: Bit get() = Bit.Compare(this, Bit.Compare.Type.GE, target)

            class Allocator<Counter>(name: String, size: Int, origin: Int = 0) : AbstractAllocator<Device>(name, size, origin) {
                override fun create(name: String, address: Int, target: Word<MemorySize>?): Device = Counter(name, address, target!!)
            }
        }
    }

    /**
     * 文字列を保持する疑似デバイス
     * 機種依存が激しいので、かなり抽象化している
     */
    class StringBuffer(name: String, address: Int, val wordCount: Int, initial: String = "") : Device(name, address) {
        var text: String = initial
    }

    abstract class AbstractAllocator<D : Device>(val name: String, size: Int, origin: Int, val target: Device.Word<MemorySize>? = null) {

        class OverFlowError(name: String): Error("Too many allocate for '$name'")

        private val max = size + origin
        private var idx = origin

        fun allocate(): D {
            if (idx < max) {
                return create(name, idx++, target)
            } else {
                throw OverFlowError(name)
            }
        }

        fun allocate(skipTo: Int): D {
            if (skipTo in (idx + 1)..(max - 1)) {
                idx = skipTo
                return create(name, idx++, target)
            } else {
                throw OverFlowError(name)
            }
        }

        protected abstract fun create(name: String, address: Int, target: Word<MemorySize>?): D
    }
}
