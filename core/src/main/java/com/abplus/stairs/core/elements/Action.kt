package com.abplus.stairs.core.elements

@Suppress("unused")
interface Action {

    class Output(val device: Device.Outable) : Action
    class Set(val device: Device.Outable) : Action
    class Reset(val device: Device.Outable) : Action

    class SetValue<M: Device.MemorySize>(val device: Device.Outable, val value: Device.Word<M>) : Action

    class Interlocked(val interlock: Condition, val action: Action) : Action by action
}
