package com.interedes.agriculturappv3.libs.eventbus_rx

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


object Rx_Bus {
    private val publisher= PublishSubject.create<Any>()

    fun publish(event:Any){
       publisher.onNext(event)
    }
    fun <T> listen(eventType:Class<T>): Observable<T> = publisher.ofType(eventType)
}