package com.shooter.game.physics

import com.shooter.game.physics.collision.Dispatch
import com.shooter.game.physics.physicsMath.*

class Manifold {
    var a: Body
    var b: Body

    var penetration = 0f     // Depth of penetration from collision
    var normal = Vector2()          // From A to B
    var contacts = Array(2, {Vector2()})    // Points of contact during collision
    var contact_count = 0 // Number of contacts that occured during collision
    var e: Float = 0f               // Mixed restitution
    var df: Float = 0f              // Mixed dynamic friction
    var sf: Float = 0f

    constructor(_a: Body, _b: Body) {
        a = _a
        b = _b
    }
    var idI = -1
    var idJ = -1
    fun Solve() {
        //var i = 0
        //var j = 0
        //for (s1 in a.shapes) {
        //    j = 0
        //    for (s2 in b.shapes) {
                var bool = Dispatch.dispatch[a.shapes[idI].GetType().e][b.shapes[idJ].GetType().e](this, a, b, idI, idJ)
        //        if (bool) {
        //            idI = i
        //            idJ = j
        //            return
        //        }
        //        j++
        //    }
        //    i++
        //}
    }

    fun Initialize() {
        // Calculate average restitution
        e = Math.min(a.material.restitution, b.material.restitution)

        // Calculate static and dynamic friction
        sf = Math.sqrt((a.material.staticFriction * b.material.staticFriction).toDouble()).toFloat()
        df = Math.sqrt((a.material.dynamicFriction * b.material.dynamicFriction).toDouble()).toFloat()

        for(i in 0..contact_count-1) {
            // Calculate radii from COM to contact
            var ra = contacts[i]
            var rb = contacts[i]


            var rv = b.velocity + Cross(b.angularVelocity, rb) -
                    a.velocity - Cross(a.angularVelocity, ra)


            // Determine if we should perform a resting collision or not
            // The idea is if the only thing moving this object is gravity,
            // then the collision should be performed without any restitution
            if(rv.LenSqr( ) < (Word.gravity * Word.dt).LenSqr() + EPSILON) {
                e = 0.0f
            }
        }
    }

    fun ApplyImpulse() {
        // Early out and positional correct if both objects have infinite mass
        if(Equal(a.im + b.im, 0f)) {
            InfiniteMassCorrection()
            return
        }

        for(i in 0..contact_count-1) {
            // Calculate radii from COM to contact
            var ra = contacts[i] - a.position
            var rb = contacts[i] - b.position

            // Relative velocity
            var rv = b.velocity + Cross(b.angularVelocity, rb) -
                    a.velocity - Cross(a.angularVelocity, ra)

            // Relative velocity along the normal
            var contactVel = Dot(rv, normal)

            // Do not resolve if velocities are separating
            if(contactVel > 0) {
                return
            }

            var raCrossN = Cross( ra, normal );
            var rbCrossN = Cross( rb, normal );
            var invMassSum = a.im + b.im + Sqr(raCrossN) * a.iI + Sqr(rbCrossN) * b.iI

            // Calculate impulse scalar
            var j = -(1.0f + e) * contactVel
            j /= invMassSum;
            j /= contact_count.toFloat()

            // Apply impulse
            var impulse = normal * j
            a.ApplyImpulse(-impulse, ra)
            b.ApplyImpulse(impulse, rb)

            // Friction impulse
            rv = b.velocity + Cross(b.angularVelocity, rb) -
                    a.velocity - Cross(a.angularVelocity, ra)

            var t = rv - (normal * Dot( rv, normal ))
            t.Normalize( )

            // j tangent magnitude
            var jt = -Dot( rv, t )
            jt /= invMassSum
            jt /= contact_count.toFloat()

            // Don't apply tiny friction impulses
            if(Equal( jt, 0.0f )) {
                return
            }

            // Coulumb's law
            var tangentImpulse = Vector2()
            if(Math.abs( jt ) < j * sf)
                tangentImpulse = t * jt;
            else
                tangentImpulse = t * -j * df

            // Apply friction impulse
            a.ApplyImpulse(-tangentImpulse, ra)
            b.ApplyImpulse(tangentImpulse, rb)
        }
    }

    fun PositionalCorrection() {
        val k_slop = 0.05f; // Penetration allowance
        val percent = 0.4f; // Penetration percentage to correct
        val correction = normal * percent * (Math.max(penetration - k_slop, 0.0f) /
                (a.im + b.im))
        a.position -= correction * a.im
        b.position += correction * b.im
    }

    fun InfiniteMassCorrection() {
        a.velocity.Set(0f, 0f)
        b.velocity.Set(0f, 0f)
    }
}
