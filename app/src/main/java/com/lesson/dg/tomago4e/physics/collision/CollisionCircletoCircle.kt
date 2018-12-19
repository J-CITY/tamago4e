package com.shooter.game.physics.collision

import com.shooter.game.physics.Body
import com.shooter.game.physics.shape.Circle
import com.shooter.game.physics.Manifold
import com.shooter.game.physics.physicsMath.Vector2

class CollisionCircletoCircle: Collision {
    override fun invoke(manifold: Manifold, _a: Body, _b: Body, idA: Int, idB: Int): Boolean {
        var a: Circle = _a.shapes[idA] as Circle
        var b: Circle = _b.shapes[idB] as Circle

        // Calculate translational vector, which is normal
        var normal = _b.position + b.localPos - _a.position - a.localPos

        var dist_sqr = normal.LenSqr( )
        var radius = a.radius + b.radius

        // Not in contact
        if(dist_sqr >= radius * radius) {
            manifold.contact_count = 0
            return false
        }

        var distance = Math.sqrt(dist_sqr.toDouble()).toFloat()

        manifold.contact_count = 1

        if(distance == 0.0f) {
            manifold.penetration = a.radius
            manifold.normal = Vector2(1f, 0f)
            manifold.contacts[0] = _a.position + a.localPos
        } else {
            manifold.penetration = radius - distance
            manifold.normal = normal / distance
            // Faster than using Normalized since we already performed sqrt
            manifold.contacts[0] = manifold.normal * a.radius + _a.position + a.localPos
        }
        return if(manifold.contact_count == 0) false else true
    }
    constructor() : super() {
    }
}
