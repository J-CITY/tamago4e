package com.shooter.game.physics

import com.shooter.game.physics.physicsMath.Cross
import com.shooter.game.physics.physicsMath.Random
import com.shooter.game.physics.physicsMath.Vector2
import com.shooter.game.physics.shape.Shape

class Body {
    var shapes = mutableListOf<Shape>()

    var I: Float = 0f
    var iI: Float = 0f
    var m: Float = 0f
    var im: Float = 0f

    var isWater = false

    var force: Vector2 = Vector2(0f, 0f)

    //var staticFriction: Float = 0f
    //var dynamicFriction: Float = 0f
    //var restitution: Float = 0f
    //var density: Float = 0.3f
    var material = Material()

    var position: Vector2 = Vector2(0f, 0f)
    var velocity: Vector2 = Vector2(0f, 0f)

    var angularVelocity: Float = 0f
    var torque: Float = 0f
    var orient: Float = 0f // radians


    var isStatic = false

    var bodyLevel = 0

    var jointLevel = mutableSetOf<Int>()


    constructor(_shape: Shape, x: Int, y: Int,
                density: Float= 0.3f,
                restitution: Float = 0.2f,
                dynamicFriction: Float = 0.3f,
                staticFriction: Float = 0.5f
                ) {
        var s = _shape.Clone()
        s.body = this
        shapes.add(s)
        position = Vector2(x.toFloat(), y.toFloat())
        velocity = Vector2(0f,0f)
        angularVelocity = 0f
        torque = 0f

        orient = Random(-Math.PI.toFloat(), Math.PI.toFloat())
        force = Vector2(0f, 0f)
        material.Set(density, restitution, dynamicFriction, staticFriction)
        shapes[shapes.size-1].Initialize(material.density)
    }

    fun AddShape(_shape: Shape) {
        var s = _shape.Clone()
        s.body = this
        shapes.add(s)
        shapes[shapes.size-1].Initialize(material.density)
    }

    fun ApplyForce(f: Vector2) {
        force += f
    }
    fun ApplyImpulse(impulse: Vector2, contactVector: Vector2) {
        velocity += impulse * im
        angularVelocity += iI * Cross(contactVector, impulse)
    }
    fun SetStatic() {
        I = 0.0f
        iI = 0.0f
        m = 0.0f
        im = 0.0f

        isStatic = true
    }
    fun SetOrient(id: Int, radians: Float) {
        orient = radians
        shapes[id].SetOrient(radians)
    }
}