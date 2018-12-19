package com.shooter.game.physics

import com.shooter.game.physics.physicsMath.Vector2

class AnimationStep {
    enum class StepType(val e: Int) {
        eLiner(0),
        eQuadric(1),
        eQuadricInv(2),
        eCubic(3)
    }

    var X0 = Vector2()
    var X1 = Vector2()
    var T = 0f
    lateinit var type: StepType
    constructor(){}
    constructor(_X0: Vector2, _X1: Vector2, _T: Float, _type: StepType) {
        X0 = _X0
        X1 = _X1
        T = _T
        type = _type
    }
}

class Animation {
    var steps = mutableListOf<AnimationStep>()
    var stepsI = 0
    var countBase = 0 // -1 is inf
    var count = 0 // -1 is inf


    var isDone = false
    var isAllDone = false

    constructor(_steps: MutableList<AnimationStep>, _count: Int) {
        steps = _steps
        stepsI = _steps.size
        count = _count
        countBase = _count
        isDone = false
        isAllDone = false
    }

    fun Restart() {
        stepsI = 0
        count = countBase
        isDone = false
        isAllDone = false
    }

    fun DoStep(time: Float): Vector2? {
        if (isAllDone) {
            return null
        }
        var s = steps[stepsI]
        var res = Vector2()
        when(s.type) {
            AnimationStep.StepType.eLiner -> {
                res = moveLinear(s.X0, s.X1, s.T, time)
            }
            AnimationStep.StepType.eQuadric -> {
                res = moveQuadratic(s.X0, s.X1, s.T, time)
            }
            AnimationStep.StepType.eQuadricInv -> {
                res = moveQuadraticInv(s.X0, s.X1, s.T, time)
            }
            AnimationStep.StepType.eCubic -> {
                res = moveCubic(s.X0, s.X1, s.T, time)
            }
        }
        if (isDone) {
            isDone = !isDone
            stepsI = if (stepsI + 1 < steps.size) stepsI+1 else 0
            if (stepsI == 0 && count > 0) {
                count--
                if (count == 0) {
                    isAllDone = true
                }
            }
        }
        return res
    }

    // линейный интерполятор - просто едем из X0 в X1 за время T
    fun moveLinear(X0: Vector2, X1: Vector2, T: Float, t: Float): Vector2 {
        var f = t / T;
        // обрежем f по диапазону 0-1
        if (f < 0) {f = 0f; isDone = true}
        if (f > 1) {f = 1f; isDone = true}
        // сама интерполяция
        return X0 * (1f-f) + X1 * f;
    }

    // квадратичный интерполятор - едем из X0 в X1 за время T с разгоном
    fun moveQuadratic(X0: Vector2, X1: Vector2, T: Float, t: Float): Vector2 {
        var f = t / T;
        // обрежем f по диапазону 0-1
        if (f < 0) {f = 0f; isDone = true}
        if (f > 1) {f = 1f; isDone = true}
        // сама интерполяция
        f = f*f;
        return X0 * (1-f) + X1 * f;
    }

    // квадратичный интерполятор - едем из X0 в X1 за время T с торможением
    fun moveQuadraticInv(X0: Vector2, X1: Vector2, T: Float, t: Float): Vector2 {
        var f = t / T;
        // обрежем f по диапазону 0-1
        if (f < 0) {f = 0f; isDone = true}
        if (f > 1) {f = 1f; isDone = true}
        // сама интерполяция
        f = 1 - f
        f = f*f;
        return X0 * f + X1 * (1-f)
    }

    // кубический интерполятор - едем из X0 в X1 за время T с плавным разгоном и плавным торможением
    fun moveCubic(X0: Vector2, X1: Vector2, T: Float, t: Float): Vector2 {
        var f = t / T;
        // обрежем f по диапазону 0-1
        if (f < 0) {f = 0f; isDone = true}
        if (f > 1) {f = 1f; isDone = true}
        // сама интерполяция
        f = f * f * (3 - 2 * f);
        return X0 * (1-f) + X1 * f;
    }
}
