package com.shooter.game.physics.collision

import com.shooter.game.physics.Body
import com.shooter.game.physics.Manifold

object Dispatch {
    val dispatch: Array<Array<Collision>> =
            arrayOf(arrayOf(CollisionCircletoCircle(), CollisionCircletoPolygon()),
                    arrayOf(CollisionPolygontoCircle(), CollisionPolygontoPolygon()))
}

abstract class Collision() {
    abstract operator fun invoke(manifold: Manifold, a: Body, b: Body, idA: Int, idB: Int): Boolean
    //abstract operator fun invoke(manifold: Manifold, _as: Shape, _bs: Shape, _a: Body, _b: Body)
}
