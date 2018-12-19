package com.shooter.game.physics.utils

import java.util.*

class Clock {
    var start = 0.0.toLong()
    var stop = 0.0.toLong()
    var current = 0.0.toLong()
    var freq = 0.0.toLong()

    inline val Date.time get() = getTime().toLong()

    constructor() {
        Start()
        Stop()
    }

    fun Start() {
        start = Date().time
    }

    fun Stop() {
        stop = Date().time
    }

    fun Elapsed(): Long {
        current = Date().time
        return (current - start)
    }

    fun Difference(): Long {
        return stop - start
    }

    fun Current(): Long {
        current = Date().time
        return current
    }

}