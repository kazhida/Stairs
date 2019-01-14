package com.abplus.stairs.core.elements

interface Action {

    class Output(val device: Device.Outable) : Action

    class SetValue<M: Device.MemorySize>(val device: Device.Outable, value: Device.Word<M>) : Action

    class Interlocked(val interlock: Condition, val action: Action) : Action by action
}
