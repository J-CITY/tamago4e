package com.shooter.game.physics.joint

import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.Matrix2
import com.shooter.game.physics.physicsMath.Project
import com.shooter.game.physics.physicsMath.Vector2

class JointPin: Joint {
    constructor() : super() {
    }

    override fun GetType(): TypeJoint {
        return TypeJoint.eJointPin
    }

    fun Add(_bA: Body,
            Anch: Vector2,
            D: Float = 0f,
            StiffPull: Float = 1f,
            stiffPush: Float = 1f) {

        bA = _bA
        bB = _bA

        AnchA = Anch
        AnchB = bA.position + Anch
        L = D
        StifPULL = StiffPull
        StifPUSH = stiffPush
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

        Axe = tAnchA

        vAprj = Project(bA.velocity * KDamp, Axe)

        //??
        RVA = Vector2()
        //RVA = Cross(bA.angularVelocity, tAnchA)
        //RVA = RVA * KDamp * bA.iI
        //??

        FIXa = bA.position + tAnchA + RVA
        N = FIXa - AnchB

        N = N + vAprj

        D = N.Len()
        if (D != 0f) {
            N = N * (1f / D)
        }
        D = (D - L) * bA.m * KMASS * 2

        if (D > 0) {
            D *= StifPULL
        } else {
            D *= StifPUSH
        }

        bA.ApplyImpulse(N * -D, tAnchA)
    }
}