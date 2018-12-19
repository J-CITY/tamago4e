package com.shooter.game.physics.physicsMath

class Vector2 {
    var x: Float = 0f
    var y: Float = 0f

    constructor() {

    }

    constructor(_x: Float, _y: Float) {
        x = _x
        y = _y
    }

    constructor(v: Vector2) {
        x = v.x
        y = v.y
    }

    fun Set(_x: Float, _y: Float) {
        x = _x
        y = _y
    }

    fun Set(v: Vector2) {
        x = v.x
        y = v.y
    }

    fun UMin() {
        x = -x
        y = -y
    }

    operator fun unaryMinus() = Vector2(-x, -y)

    operator fun minus(v: Vector2): Vector2 {
        return Vector2(x-v.x, y-v.y)
    }

    operator fun minus(v: Float): Vector2 {
        return Vector2(x-v, y-v)
    }

    operator fun plus(v: Vector2): Vector2 {
        return Vector2(x+v.x, y+v.y)
    }

    operator fun plus(v: Float): Vector2 {
        return Vector2(x+v, y+v)
    }

    operator fun times(v: Float): Vector2 {
        return Vector2(x*v, y*v)
    }

    operator fun times(v: Vector2): Vector2 {
        return Vector2(x*v.x, y*v.y)
    }

    operator fun div(v: Float): Vector2 {
        return Vector2(x/v, y/v)
    }

    fun LenSqr(): Float {
        return x * x + y * y;
    }

    fun Len(): Float {
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    fun Rotate(radians: Float) {
        var c = Math.cos(radians.toDouble()).toFloat()
        var s = Math.sin(radians.toDouble()).toFloat()

        var xp = x * c - y * s;
        var yp = x * s + y * c;

        x = xp;
        y = yp;
    }

    fun Normalize() {
        var len = Len( );

        if(len > EPSILON) {
            var invLen = 1.0f / len;
            x *= invLen;
            y *= invLen;
        }
    }

    fun Project(v: Vector2): Vector2 {
        var D = v.x*v.x+v.y*v.y
        assert(D == 0f)

        D /= Math.sqrt(D.toDouble()).toFloat()

        var K = (x * v.x + y*v.y)*D

        return Vector2(v.x*D*K, v.y*D*K)
    }

    fun RightPerp(): Vector2 {
        return Vector2(-x, y)
    }
}
