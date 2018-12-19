package com.shooter.game.physics.shape

import android.graphics.Bitmap
import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.Matrix2
import com.shooter.game.physics.physicsMath.Vector2

abstract class Shape() {
    enum class Type(val e: Int) {
        eCircle(0),
        ePoly(1),
        eCount(2),
        ePolyshape(3)
    }

    lateinit var body: Body
    var radius: Float = 0f
    var u = Matrix2() // Orientation matrix from model to world

    var localPos = Vector2()

    var m = 0f

    open abstract fun Clone(): Shape
    open abstract fun Initialize(density: Float)
    open abstract fun ComputeMass(density: Float)
    open abstract fun SetOrient( radians: Float)
    open abstract fun GetType(): Type
    open abstract fun LocalPos(_lp: Vector2)

    var texture: Bitmap ?= null
}
