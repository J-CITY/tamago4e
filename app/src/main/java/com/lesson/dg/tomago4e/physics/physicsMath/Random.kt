package com.shooter.game.physics.physicsMath

fun Random(min: Float, max: Float): Float {
    return ((max - min) * Math.random() + min).toFloat()
}

fun Random(min: Int, max: Int): Int {
    return ((max - min + 1) * Math.random() + min).toInt()
}

fun rollDice(N: Int, S: Int): Int {
    var value = 0
    for (i in 0..N-1) {
        value += Random(0, S)
    }
    return value
}

fun rollDice(N: Int, S: Float): Float {
    var value = 0f
    for (i in 0..N-1) {
        value += Random(0f, S)
    }
    return value
}

private var ready = false
private var second = 0.0f

fun Gauss(mean: Float, dev: Float): Float {
    if (ready) {
        ready = false;
        return second * dev + mean
    } else {
        var u: Float
        var v: Float
        var s: Float
        do {
            u = 2.0f * Math.random().toFloat() - 1.0f
            v = 2.0f * Math.random().toFloat() - 1.0f
            s = u * u + v * v
        } while (s > 1.0f || s == 0.0f)
        var r = Math.sqrt(-2.0 * Math.log(s.toDouble()) / s).toFloat()
        second = r * u;
        ready = true;
        return r * v * dev + mean;
    }
}
