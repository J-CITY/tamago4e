package com.shooter.game.physics

class Material {
    var density: Float = 0f
    var restitution: Float = 0f
    var dynamicFriction: Float = 0f
    var staticFriction: Float = 0f

    constructor() {}

    constructor(_d: Float, _r: Float, _df: Float, _sf: Float) {
        Set(_d, _r, _df, _sf)
    }

    fun Set(_d: Float, _r: Float, _df: Float, _sf: Float) {
        density = _d
        restitution = _r
        dynamicFriction = _df
        staticFriction = _sf
    }

    fun SetDensity(_d: Float) {
        density = _d
    }

    fun SetDynamicFriction(_df: Float) {
        dynamicFriction = _df
    }

    fun SetStaticFriction(_sf: Float) {
        staticFriction = _sf
    }

    fun SetRestitution(_r: Float) {
        restitution = _r
    }

    fun SetRock() {
        density = 0.6f
        restitution = 0.1f
        staticFriction = 0.8f
        dynamicFriction = 0.8f
    }
    fun SetWood() {
        density = 0.3f
        restitution = 0.2f
        staticFriction = 0.6f
        dynamicFriction = 0.6f
    }
    fun SetMetal() {
        density = 1.2f
        restitution = 0.05f
        staticFriction = 0.3f
        dynamicFriction = 0.3f
    }
    fun SetGlass() {
        density = 1f
        restitution = 0.5f
        staticFriction = 0.6f
        dynamicFriction = 0.6f
    }
    fun SetPlastic() {
        density = 1f
        restitution = 0.7f
        staticFriction = 0.4f
        dynamicFriction = 0.4f
    }
    fun SetRubber() {
        density = 1f
        restitution = 0.9f
        staticFriction = 0.9f
        dynamicFriction = 0.9f
    }

    fun SetBouncyBall() {
        density = 0.3f
        restitution = 0.8f
        staticFriction = 0.2f
        dynamicFriction = 0.2f
    }
    fun SetSuperBall() {
        density = 0.3f
        restitution = 0.95f
        staticFriction = 0.2f
        dynamicFriction = 0.2f
    }
    fun SetPillow() {
        density = 0.1f
        restitution = 0.2f
        staticFriction = 0.2f
        dynamicFriction = 0.2f
    }
    fun SetStatic() {
        density = 0f
        restitution = 0.4f
        staticFriction = 0.2f
        dynamicFriction = 0.2f
    }
}