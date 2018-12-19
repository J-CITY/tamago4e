package com.shooter.game.physics.joint

import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.Vector2

abstract class Joint {
    enum class TypeJoint(val e: Int) {
        eJointDistance(0),
        eJoint2Pins(1),
        eJointPin(2),
        eJointRotor1(3),
        eJointRotor2(4)
    }

    var ID = 0
    var isCollision = true

    //open abstract fun Add()
    open abstract fun Resolve()
    open abstract fun GetType(): TypeJoint

    lateinit var bA: Body
    lateinit var bB: Body
    var L       = 0f
    var AnchA   = Vector2()
    var AnchB   = Vector2()
    var tAnchA  = Vector2()
    var tAnchB  = Vector2()
    var StifPULL = 0f
    var StifPUSH = 0f

    val KMASS = 0.1f
    val KDamp = 1f

    var pos = Vector2()
}











