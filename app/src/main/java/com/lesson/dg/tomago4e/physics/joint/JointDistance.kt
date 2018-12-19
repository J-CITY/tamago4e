package com.shooter.game.physics.joint

import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.Matrix2
import com.shooter.game.physics.physicsMath.Vector2

class JointDistance: Joint {
    constructor() : super() {
    }

    override fun GetType(): TypeJoint {
        return TypeJoint.eJointDistance
    }

    fun Add(_bA: Body,
            _bB: Body,
            _D: Float,
            StiffPull: Float = 1f,
            stiffPush: Float = 1f) {

        bA = _bA
        bB = _bB
        L = _D
        StifPULL = StiffPull
        StifPUSH = stiffPush

        pos = bB.position-bA.position
    }

    override fun Resolve() {
        var I       :Int
        var J       :Int
        var D       :Float
        var N       :Vector2
        var Center  :Vector2
        var FIXa    :Vector2
        var FIXb    :Vector2
        var vAprj   :Vector2
        var vBprj   : Vector2
        var RVA     :Vector2
        var RVB     :Vector2
        var Axe     :Vector2
        var Tmass   :Int
        var ORI     : Matrix2
        var RotDiff :Int

        N = bA.position - bB.position
        N = ((bA.velocity - bB.velocity) * KDamp + N)
        D = N.Len()
        N = N * 1f / D
        D = (D - L) * (bA.m + bB.m) * KMASS

        if (D > 0) {
            D *=  StifPULL
        } else {
            D *= StifPUSH
        }

        bA.ApplyImpulse((N*(-D)), Vector2(0f, 0f))
        bB.ApplyImpulse((N*D), Vector2(0f, 0f))
    }
}