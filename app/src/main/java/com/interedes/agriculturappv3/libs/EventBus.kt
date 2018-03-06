package com.interedes.agriculturappv3.libs

interface EventBus {

    fun register(subscriber: Any)
    fun unregister(subscriber: Any)
    fun post(event: Any)
}