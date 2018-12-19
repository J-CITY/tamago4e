package com.shooter.game.physics.joint

import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.Matrix2
import com.shooter.game.physics.physicsMath.Project
import com.shooter.game.physics.physicsMath.Vector2

class JointPin2: Joint {
    constructor() : super() {
    }

    override fun GetType(): TypeJoint {
        return TypeJoint.eJoint2Pins
    }

    fun Add(_bA: Body,
            _AnchA: Vector2,
            _bB: Body,
            _AnchB: Vector2,
            _D: Float,
            _StiffPull: Float = 1f,
            _stiffPush: Float = 1f) {

        bA = _bA
        bB = _bB
        AnchA = _AnchA
        AnchB = _AnchB
        L = _D
        StifPULL = _StiffPull
        StifPUSH = _stiffPush
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
        var vBprj   :Vector2
        var RVA     :Vector2
        var RVB     :Vector2
        var Axe     :Vector2
        var Tmass   :Int
        var ORI = Matrix2()
        var RotDiff :Int

        ORI.Set(bA.orient + bA.angularVelocity)

        tAnchA = ORI * AnchA

        FIXa = bA.position + tAnchA

        ORI.Set(bB.orient + bB.angularVelocity)

        tAnchB = ORI * AnchB

        FIXb = bB.position + tAnchB

        Axe = FIXb - FIXa

        vAprj = Project(bA.velocity * KDamp, Axe)
        vBprj = Project(bB.velocity * KDamp, Axe)


        N = FIXa - FIXb

        N = vAprj - vBprj + N

        D = N.Len()
        if (D != 0f)
            N = N * 1f / D
        D = (D - L) * (bA.m + bB.m) * KMASS
        if (D > 0f) {
            D *= StifPULL
        } else {
            D *= StifPUSH
        }

        bA.ApplyImpulse(N * -D, tAnchA)
        bB.ApplyImpulse(N * D, tAnchB)
    }
}