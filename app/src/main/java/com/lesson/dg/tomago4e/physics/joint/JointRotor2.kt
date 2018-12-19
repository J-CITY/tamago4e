package com.shooter.game.physics.joint

import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.AngleDiff
import com.shooter.game.physics.physicsMath.Matrix2
import com.shooter.game.physics.physicsMath.Project
import com.shooter.game.physics.physicsMath.Vector2

class JointRotor2: Joint {
    constructor() : super() {
    }

    override fun GetType(): TypeJoint {
        return TypeJoint.eJointRotor1
    }

    fun Add(_bA: Body,
            _AnchA: Vector2,
            _bB: Body,
            _AnchB: Vector2,
            StiffPull: Float = 1f,
            stiffPush: Float = 1f) {
        bA = _bA
        bB = _bB
        AnchA = _AnchA
        AnchB = _AnchB
        L = 0f
        //StifPULL = StiffPull
        //StifPUSH = stiffPush
    }

    override fun Resolve() {
        var I       :Int
        var J       :Int
        var D       :Float
        var N       = Vector2()
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
        var RotDiff: Float



        ORI.Set(bA.orient + bA.angularVelocity)
        tAnchA = ORI * AnchA
        FIXa = bA.position + tAnchA

        ORI.Set(bB.orient + bB.angularVelocity)
        tAnchB = ORI * AnchB

        FIXb = bB.position + tAnchB

        Axe = FIXb - FIXa

        vAprj = Project((bA.velocity * KDamp), Axe)
        vBprj = Project((bB.velocity * KDamp), Axe)

        N = FIXa - FIXb
        N = N +vAprj - vBprj

        D = N.Len()
        if (D != 0f) {
            N = N * 1f / D
        }
        D = (D - L) * (bA.m + bB.m) * KMASS
        if (D > 0f)  {
            D *=  0.5f
        } else {
            D *= 0.5f
        }


        bA.ApplyImpulse(N * -D, tAnchA)
        bB.ApplyImpulse(N * D, tAnchB)

        RotDiff = AngleDiff((Math.PI * 0.8 * Math.cos(0.0008) + bB.orient + bB.angularVelocity).toFloat(),
                bA.orient + bA.angularVelocity)

        if (Math.abs(RotDiff) > 1) {
            RotDiff = Math.signum(RotDiff) * 1
        }

        ORI.Set(bA.orient + bA.angularVelocity)
        tAnchA = ORI * AnchA
        tAnchA.x = -tAnchA.x
        tAnchA.y = -tAnchA.y
        N.x = -tAnchA.y
        N.y = tAnchA.x
        N = N * RotDiff
        bA.ApplyImpulse(N, tAnchA + bA.velocity)


        ORI.Set(bB.orient + bB.angularVelocity)
        tAnchB = ORI * AnchB
        tAnchB.x = -tAnchB.x
        tAnchB.y = -tAnchB.y
        N.x = -tAnchB.y
        N.y = tAnchB.x
        N = N * -RotDiff
        bB.ApplyImpulse(N, tAnchB + bB.velocity)
    }
}