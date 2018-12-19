package com.shooter.game.physics.physicsMath

class Matrix2 {
    var m00: Float = 0f
    var m01: Float = 0f
    var m10: Float = 0f
    var m11: Float = 0f

    constructor() {}

    constructor(_m00: Float, _m01: Float, _m10: Float, _m11: Float) {
        Set(_m00,_m01,_m10,_m11)
    }

    constructor(radians: Float) {
        Set(radians)
    }

    fun Set(_m00: Float, _m01: Float, _m10: Float, _m11: Float) {
        m00 = _m00
        m01 = _m01
        m10 = _m10
        m11 = _m11
    }

    fun Set(m: Matrix2) {
        m00 = m.m00
        m01 = m.m01
        m10 = m.m10
        m11 = m.m11
    }

    fun Set(radians: Float) {
        val c = StrictMath.cos(radians.toDouble()).toFloat()
        val s = StrictMath.sin(radians.toDouble()).toFloat()

        m00 = c
        m01 = -s
        m10 = s
        m11 = c
    }

    fun Abs(): Matrix2 {
        return Matrix2(StrictMath.abs(m00), StrictMath.abs(m01), StrictMath.abs(m10), StrictMath.abs(m11))
    }

    fun AxisX(): Vector2 {
        return Vector2( m00, m10 )
    }

    fun AxisY(): Vector2 {
        return Vector2(m01, m11)
    }

    fun Transpose(): Matrix2 {
        return Matrix2( m00, m10, m01, m11 )
    }

    operator fun times(r: Vector2): Vector2 {
        return Vector2(m00 * r.x + m01 * r.y, m10 * r.x + m11 * r.y)
    }

    operator fun times(r: Matrix2): Matrix2 {
        // [00 01]  [00 01]
        // [10 11]  [10 11]

        return Matrix2(
                m00 * r.m00 + m01 * r.m10,
                m00 * r.m01 + m01 * r.m11,
                m10 * r.m00 + m11 * r.m10,
                m10 * r.m01 + m11 * r.m11
        )
    }

    operator fun unaryMinus() = Matrix2(-m00, -m01, -m10, -m11)

    fun Inv(): Matrix2 {
        var det = m00*m11-m01*m10
        return Matrix2(
                m11/det,
                -m01/det,
                -m10/det,
                m00/det
        )
    }
}