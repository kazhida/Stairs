package com.abplus.stairs.core.elements

/**
 * 回路の条件に使用する要素のインターフェース
 */
interface Condition {

    infix fun and(other: Condition): Condition = And(this, other)
    infix fun or(other: Condition): Condition = Or(this, other)
    val not: Condition

    class And(conditions: List<Condition>) : Condition {

        constructor(c0: Condition, c1: Condition) : this(listOf(c0, c1))

        private val conditions: List<Condition> = ArrayList<Condition>().apply {
            addAll(conditions)
        }

        private fun flatten(): Condition {
            val list = conditions.firstOrNull { it is And }

            return if (list == null) {
                this
            } else {
                conditions.flatMap { it: Condition ->
                    if (it is And) {
                        it.conditions
                    } else {
                        listOf(it)
                    }
                }.let {
                    And(it)
                }
            }
        }

        override infix fun and(other: Condition): Condition = And(this, other).flatten()

        override infix fun or(other: Condition): Condition = Or(this, other)

        override val not: Condition
            get() = conditions.map {
                it.not
            }.let {
                And(it)
            }
    }

    class Or(conditions: List<Condition>) : Condition {

        constructor(c0: Condition, c1: Condition) : this(listOf(c0, c1))

        val conditions: List<Condition> = ArrayList<Condition>().apply {
            addAll(conditions)
        }

        private fun flatten(): Condition {
            val list = conditions.firstOrNull { it is Or }

            return if (list == null) {
                this
            } else {
                conditions.flatMap { it: Condition ->
                    if (it is Or) {
                        it.conditions
                    } else {
                        listOf(it)
                    }
                }.let {
                    And(it)
                }
            }
        }

        override infix fun and(other: Condition): Condition = And(this, other)

        override infix fun or(other: Condition): Condition = Or(this, other).flatten()

        override val not: Condition
            get() = conditions.map {
                it.not
            }.let {
                And(it)
            }
    }
}
