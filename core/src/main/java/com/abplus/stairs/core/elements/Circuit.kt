package com.abplus.stairs.core.elements

data class Circuit(
    val condition: Condition,
    val actions: List<Action>
)

typealias DefCondition = ()->Condition
typealias DefAction = ()->Action
typealias DefActions = ()->List<Action>

infix fun DefCondition.then(action: DefAction): Circuit = Circuit(this.invoke(), listOf(action.invoke()))
infix fun DefCondition.thenParallel(actions: DefActions): Circuit = Circuit(this.invoke(), actions.invoke())
