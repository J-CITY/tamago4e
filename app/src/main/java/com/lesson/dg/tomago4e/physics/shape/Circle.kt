package com.shooter.game.physics.shape

import com.shooter.game.physics.physicsMath.Vector2



class Circle: Shape {
    override fun LocalPos(_lp: Vector2) {
        localPos = _lp
    }

    constructor(r: Float) : super() {
        radius = r
    }

    override fun Clone(): Shape {
        var c = Circle(radius)
        c.localPos = localPos
        return c
    }
    override fun Initialize(density: Float) {
        ComputeMass(density)
    }

    override fun ComputeMass(density: Float) {
        body.m = Math.PI.toFloat() * radius * radius * density
        body.im = if (body.m != 0f) 1.0f / body.m else 0.0f
        body.I = body.m * radius * radius
        body.iI = if (body.I != 0f) 1.0f / body.I else 0.0f
    }
    override fun SetOrient(radians: Float) {
    }

    override fun GetType(): Type {
        return Type.eCircle
    }


}