package com.shooter.game.physics.physicsMath

val EPSILON = 0.0001f;
val PI2 = 6.2831854820251465f

fun Min(a: Vector2, b: Vector2): Vector2 {
    return Vector2(Math.min(a.x, b.x), Math.min( a.y, b.y));
}

fun Max(a: Vector2, b: Vector2): Vector2 {
    return Vector2(Math.max(a.x, b.x ), Math.max( a.y, b.y))
}

fun Dot(a: Vector2, b: Vector2): Float {
    return a.x * b.x + a.y * b.y
}

fun DistSqr(a: Vector2, b: Vector2 ): Float {
    var c = a - b
    return Dot(c, c)
}

fun Cross(v: Vector2, a: Float): Vector2 {
    return Vector2(a * v.y, -a * v.x)
}

fun Cross(a: Float, v: Vector2): Vector2 {
    return Vector2( -a * v.y, a * v.x );
}

fun Cross(a: Vector2, b: Vector2): Float {
    return a.x * b.y - a.y * b.x
}

// Comparison with tolerance of EPSILON
fun Equal(a: Float, b: Float): Boolean {
    // <= instead of < for NaN comparison safety
    return StrictMath.abs(a - b) <= EPSILON;
}

fun Sqr(a: Float): Float {
    return a * a;
}

fun Clamp(min: Float, max: Float, a: Float): Float {
    if (a < min) return min;
    if (a > max) return max;
    return a;
}

fun Round(a: Float): Int {
    return (a + 0.5f).toInt()
}

fun BiasGreaterThan(a: Float, b: Float): Boolean {
    val k_biasRelative = 0.95f
    val k_biasAbsolute = 0.01f
    return a >= b * k_biasRelative + a * k_biasAbsolute;
}

fun Project(v1: Vector2, v2: Vector2): Vector2 {
    var D = v2.x*v2.x+v2.y*v2.y
    //assert(D == 0f)

    D = 1f / Math.sqrt(D.toDouble()).toFloat()

    var K = (v1.x * v2.x + v1.y*v2.y)*D

    return Vector2(v2.x*D*K, v2.y*D*K)
}

fun AngleDiff(a1: Float, a2: Float): Float {
    var res = a1 - a2
    while (res < - Math.PI) {
        res += PI2
    }
    while (res > Math.PI) {
        res -= PI2
    }
    return  res
}

