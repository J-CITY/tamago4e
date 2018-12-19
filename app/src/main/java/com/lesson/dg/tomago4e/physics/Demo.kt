package com.shooter.game.physics

import com.shooter.game.physics.joint.JointPin
import com.shooter.game.physics.joint.JointPin2
import com.shooter.game.physics.joint.JointRotor2

import com.shooter.game.physics.physicsMath.Random
import com.shooter.game.physics.physicsMath.Vector2
import com.shooter.game.physics.shape.Circle
import com.shooter.game.physics.shape.Polygon

class Demo {
    //ANIMATION
    var canAnim = false
    lateinit var anim: Animation
    var time = 0f


    var scene = Word(1.0f / 60.0f, 10)

    fun ReloadAnim() {
        canAnim = !canAnim
        time = 0f
        t=0
    }


    fun AddPolygon(x: Int, y: Int) {
        var poly = Polygon()
        var count = Random(3, 30) // count
        var vertices = Array<Vector2>(count, { Vector2() })
        var e = Random(10f, 50f)
        for(i in 0..count-1) {
            var a = Random(-e, e)
            var b = Random(-e, e)
            vertices[i].Set(a, b)
        }
        poly.Set(vertices, count)

        var m = Material(0.8f, 0.2f, 0.6f, 0.6f)

        var bp = scene.Add(poly, x, y, m)
        bp.SetOrient(0,0f)
        //bp.isWater = true
    }

    fun AddBox(x: Int, y: Int) {
        var poly = Polygon()

        var hw = Random(10f, 50f)
        var hh = Random(10f, 50f)

        poly.SetBox(hw, hh)
        var m = Material(0.8f, 0.2f, 0.6f, 0.6f)
        var bp = scene.Add(poly, x, y, m)
        bp.SetOrient(0,0f)
        bp.material.restitution = 0.8f;
        bp.material.dynamicFriction = 0.2f
        bp.material.staticFriction = 0.6f
        bp.material.density = 0.6f
    }

    fun AddCircle(x: Int, y: Int) {
        var r = Random(10f, 20f)
        var cc = Circle(r)
        var m = Material(0.35f, 0.2f, 0.2f, 0.4f)
        var bb = scene.Add(cc, x, y, m)

        bb.material.restitution = 0.3f;
        bb.material.dynamicFriction = 0.2f
        bb.material.staticFriction = 0.4f
        bb.material.density = 0.8f

    }
    lateinit var bc1: Body
    lateinit var bc2: Body
    fun Init() {

        var arr = mutableListOf<AnimationStep>()
        arr.add(AnimationStep(Vector2(1f,0f), Vector2(1.8f,0f), 0.5f, AnimationStep.StepType.eCubic))
        arr.add(AnimationStep(Vector2(1.8f,0f), Vector2(1f,0f), 0.5f, AnimationStep.StepType.eCubic))
        anim = Animation(arr, 2)
        anim.isAllDone = true

        var m = Material(0.1f, 0.3f, 0.3f, 0.4f)

        var poly = Polygon()
        poly.SetBox(200.0f, 50.0f)
        var b = scene.Add(poly, 290, 20,m)
        b.SetStatic()
        b.SetOrient(0,0f)
        //b.isWater = true

        var w0 = Polygon()
        w0.SetBox(200f, 10f)
        var bw0 = scene.Add(w0, 290, -50,m)
        bw0.SetStatic()
        bw0.SetOrient(0,0f)

        var w1 = Polygon()
        w1.SetBox(10f, 200f)
        var bw1 = scene.Add(w1, 100, 200,m)
        bw1.SetStatic()
        bw1.SetOrient(0,0f)

        var w2 = Polygon()
        w2.SetBox(10f, 200f)
        var bw2 = scene.Add(w2, 480, 200,m)
        bw2.SetStatic()
        bw2.SetOrient(0,0f)


        ///////////////HARD BODY//////////////////////
        
        var poly3 = Polygon()
        var hw3 = 15f
        var hh3 = 15f
        poly3.LocalPos(Vector2(-10f, 0f))
        poly3.SetBox(hw3, hh3)
        var bp3 = Body(poly3, 240, 240)
        bp3.SetOrient(0,0f)
        bp3.material.restitution = 0.2f;
        bp3.material.dynamicFriction = 0.2f
        bp3.material.staticFriction = 0.4f
        bp3.material.density = 1f
        poly3.body = bp3

        var poly4 = Polygon()
        poly4.LocalPos(Vector2(15f, 0f))

        var count = 4 // count
        var vertices = Array<Vector2>(count, {Vector2()})
        vertices[0].Set(10f, 10f)
        vertices[1].Set(20f, 10f)
        vertices[2].Set(20f, 5f)
        vertices[3].Set(5f, 5f)

        //poly4.Set(vertices, count)
        poly4.SetBox(hw3-5, hh3-5)


        bp3.AddShape(poly4)


        var cc = Circle(10f)
        cc.LocalPos(Vector2(0f, 25.0f))
        bp3.AddShape(cc)


        scene.Add(bp3)

        ///////////////////////////////

        //joints
        for (i in 0..5) {
            var poly1 = Polygon()
            poly1.SetBox(10f, 10f)
            var bp1 = scene.Add(poly1, 200 + 25*i, 250,m)
            bp1.SetOrient(0,0f)
            bp1.material.restitution = 0.2f;
            bp1.material.dynamicFriction = 0.2f
            bp1.material.staticFriction = 0.4f
            bp1.material.density = 1f
            //bp1.bodyLevel = 1

            var jPin1 = JointPin()
            var jPin2 = JointPin()
            jPin1.Add(bp1, Vector2(-5f, 0f), 0f, 0.6f, 0.6f)
            jPin2.Add(bp1, Vector2(5f, 0f), 0f, 0.6f, 0.6f)
            scene.AddJoint(jPin1)
            scene.AddJoint(jPin2)
        }

        var c1 = Circle(20f)
        bc1 = scene.Add(c1, 280, 100,m)
        bc1.SetOrient(0,0f)
        var c2 = Circle(20f)
        bc2 = scene.Add(c2, 330, 100,m)
        bc2.SetOrient(0,0f)
        var c3 = Polygon()
        c3.SetBox(60f, 20f)
        var bc3 = scene.Add(c3, 300, 105,m)
        bc3.SetOrient(0,0f)
        var jRot1 = JointRotor2()
        var jRot2 = JointRotor2()
        jRot1.Add(bc1, Vector2(0f, 0f), bc3, Vector2(-20f, 0f))
        scene.AddJoint(jRot1)
        jRot2.Add(bc2, Vector2(0f, 0f), bc3, Vector2(20f, 0f))
        scene.AddJoint(jRot2)

        var j2Pin = JointPin2()
        j2Pin.Add(bc1,Vector2(0f, 0f),bc1,Vector2(0f, 0f), Math.sqrt(20.0*20.0+20.0*20.0).toFloat())
    }

    var dir = 0
    var t = 0


}