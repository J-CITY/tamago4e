package com.shooter.game.physics.collision

import com.shooter.game.physics.Body
import com.shooter.game.physics.Manifold

class CollisionPolygontoCircle: Collision {
    override fun invoke(manifold: Manifold, _a: Body, _b: Body, idA: Int, idB: Int): Boolean {
        var bool = CollisionCircletoPolygon()(manifold, _b, _a, idB, idA)
        //Dispatch.dispatch[0][1](manifold, _a, _b)
        manifold.normal = -manifold.normal
        return bool
    }
    constructor() : super() {
    }
}
