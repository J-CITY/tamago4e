package com.shooter.game.physics.joint

import com.shooter.game.physics.Body
import com.shooter.game.physics.physicsMath.Matrix2
import com.shooter.game.physics.physicsMath.Vector2

class JointRotor: Joint {
    constructor() : super() {
    }

    override fun GetType(): TypeJoint {
        return TypeJoint.eJointRotor1
    }

    fun Add(_bA: Body, Leva: Vector2, Speed: Float) {
        bA = _bA
        bB = _bA
        AnchA = Leva
        L = Speed
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
        N.x = -tAnchA.y
        N.y = tAnchA.x
        RotDiff = L - bA.angularVelocity
        N = N * RotDiff

        if (Math.signum(RotDiff) == Math.signum(L)) {
            bA.ApplyImpulse(N, (tAnchA + bA.velocity))
        }
    }
}