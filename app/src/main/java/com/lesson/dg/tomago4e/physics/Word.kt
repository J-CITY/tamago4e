package com.shooter.game.physics

import com.shooter.game.physics.intersection.GetIntersectionOfPolygons
import com.shooter.game.physics.joint.Joint
import com.shooter.game.physics.physicsMath.Cross
import com.shooter.game.physics.physicsMath.Dot
import com.shooter.game.physics.physicsMath.EPSILON
import com.shooter.game.physics.physicsMath.Vector2
import com.shooter.game.physics.shape.Polygon
import com.shooter.game.physics.shape.Shape

class Word {
    companion object {
        var gravityScaleX = 50.0f
        var gravityScaleY = 50.0f
        var gravity = Vector2(0f * gravityScaleX, 0.0f * gravityScaleY)
        var dt = 1.0f / 60f
    }

    fun setGravity(x: Float, y: Float) {
        gravity = Vector2(x * gravityScaleX, y * gravityScaleY)
    }

    var m_dt: Float
    var m_iterations: Int

    var bodies = mutableListOf<Body>()

    var joints = mutableListOf<Joint>()

    var contacts = mutableListOf<Manifold>()

    constructor(dt: Float, iterations: Int) {
        m_dt = dt
        m_iterations = iterations
    }

    // Acceleration
//    F = mA
// => A = F * 1/m

// Explicit Euler
// x += v * dt
// v += (1/m * F) * dt

// Semi-Implicit (Symplectic) Euler
// v += (1/m * F) * dt
// x += v * dt

    fun IntegrateForces(b: Body, dt: Float) {
        if(b.im == 0.0f) {
            return
        }

        b.velocity += (b.force * b.im + gravity) * (dt / 2.0f);
        b.angularVelocity += b.torque * b.iI * (dt / 2.0f);
    }

    fun ApplyTorque(b: Body, t: Float) {
        if(b.im == 0.0f) {
            return
        }
        b.torque += t*b.iI
    }

    fun GetMaxVel(): Float  {
        if (gravity.x > gravity.y)
            return -gravity.y/gravityScaleY
        else
            return -gravity.x/gravityScaleX
    }

    var MAX_VELCCITY = GetMaxVel()

    fun ApplyForce(b: Body, force: Vector2 = Vector2(), centr: Vector2 = Vector2(), isImp: Int = 0) {
        if(b.im == 0.0f) {
            return
        }
        if (isImp == 0) {
            b.force += force
        } else {
            if (b.velocity.x < MAX_VELCCITY && b.velocity.y < MAX_VELCCITY) {
                b.velocity += force * b.im
            }
        }
        if (centr == b.position)
            return
        var radiusVector = centr
        var radiusVectorNormal = radiusVector.RightPerp()
        radiusVectorNormal.Normalize()
        var torque = Dot(force, radiusVectorNormal) * radiusVector.Len()
        ApplyTorque(b, torque)
    }

    fun pnpoly(water: Body, polygon: Body): MutableList<Vector2> {
        var wPoins = mutableListOf<Vector2>()
        var pPoins = mutableListOf<Vector2>()

        var res = mutableListOf<Vector2>()
        for (pp in pPoins) {
            var c = false
            var j = wPoins.size-1
            for (i in 0..wPoins.size-1) {
                if ((((wPoins[i].y<=pp.y) && (pp.y<wPoins[j].y)) || ((wPoins[j].y<=pp.y) && (pp.y<wPoins[i].y))) &&
                        (pp.x > (wPoins[j].x - wPoins[i].x) * (pp.y - wPoins[i].y) / (wPoins[j].y - wPoins[i].y) + wPoins[i].x))
                    c = !c
                j = i
            }

            if (c) {
                res.add(pp)
            }
        }


        return res
    }
    val countCirclePolygon = 20
    fun PolygonVolume(_b1: Body,_b2: Body, idI: Int, idJ: Int): Array<Any> {
        //var p_Vertices = inp_Vertices.toMutableList()
        var _bbml = mutableListOf<Vector2>()
        var _bb2ml = mutableListOf<Vector2>()
        var bbml = mutableListOf<Vector2>()
        var bb2ml = mutableListOf<Vector2>()

        if (_b1.shapes[idI].GetType() == Shape.Type.eCircle) {
            var alpha = 0f
            var alphaStep = 360f/countCirclePolygon
            var radius = _b1.shapes[idI].radius

            for (i in 1..countCirclePolygon) {
                var point = Vector2(
                        radius*Math.cos(alpha.toDouble()*Math.PI/180).toFloat(),
                        radius*Math.sin(alpha.toDouble()*Math.PI/180).toFloat()
                )
                bbml.add(point +_b1.position + _b1.shapes[idI].localPos)
                alpha += i*alphaStep
            }
        }
        if (_b2.shapes[idJ].GetType() == Shape.Type.eCircle) {
            var alpha = 0f
            var alphaStep = 360f/countCirclePolygon
            var radius = _b2.shapes[idJ].radius

            for (i in 1..countCirclePolygon) {
                var point = Vector2(
                        radius*Math.cos(alpha.toDouble()*Math.PI/180).toFloat(),
                        radius*Math.sin(alpha.toDouble()*Math.PI/180).toFloat()
                )
                bbml.add(point +_b2.position + _b2.shapes[idI].localPos)
                alpha += alphaStep
            }
        }

        if (_b1.shapes[idI].GetType() == Shape.Type.ePoly) {
            var bb = _b1.shapes[idI] as Polygon
            _bbml = bb.m_vertices.toMutableList()
            for (i in 0..bb.m_vertexCount - 1) {
                bbml.add(bb.u * _bbml[i] + _b1.position + _b1.shapes[idI].localPos)
            }
        }
        if (_b2.shapes[idJ].GetType() == Shape.Type.ePoly) {
            var bb2 = _b2.shapes[idJ] as Polygon //water
            _bb2ml = bb2.m_vertices.toMutableList()
            for (i in 0..bb2.m_vertexCount - 1) {
                bb2ml.add(bb2.u * _bb2ml[i] + _b2.position + _b2.shapes[idJ].localPos)
            }
        }
        val p_Vertices = GetIntersectionOfPolygons(bbml, bb2ml)


        var count = p_Vertices.size
        assert(count >= 3)
        if (count < 3) {
            return arrayOf<Any>(0f, Vector2(0f,0f))
        }

        //System.out.println("COUNT "+count)
        var c = Vector2(0.0f, 0.0f);
        var area = 0.0f
        var pRef = _b1.position
        val inv3 = 1.0f / 3.0f
        ///
        var m_vertexCount = 0
        var rightMost = 0;
        var highestXCoord = p_Vertices[0].x
        for(i in 1..count-1) {
            var x = p_Vertices[i].x;
            if(x > highestXCoord) {
                highestXCoord = x;
                rightMost = i;
            } else if(x == highestXCoord) { // If matching x then take farthest negative y
                if (p_Vertices[i].y < p_Vertices[rightMost].y) {
                    rightMost = i
                }
            }
        }

        var hull = Array(p_Vertices.size, {0})
        var outCount = 0
        var indexHull = rightMost

        while (true) {
            hull[outCount] = indexHull
            var nextHullIndex = 0
            for(i in 1..count-1) {
                if(nextHullIndex == indexHull) {
                    nextHullIndex = i
                    continue;
                }
                var e1 = p_Vertices[nextHullIndex] - p_Vertices[hull[outCount]]
                var e2 = p_Vertices[i] - p_Vertices[hull[outCount]]
                var c = Cross( e1, e2 )
                if(c < 0.0f)
                    nextHullIndex = i
                if(c == 0.0f && e2.LenSqr() > e1.LenSqr()) {
                    nextHullIndex = i
                }
            }
            outCount += 1
            indexHull = nextHullIndex
            if(nextHullIndex == rightMost) {
                m_vertexCount = outCount
                break
            }
        }
        var _p_Vertices = mutableListOf<Vector2>()
        for(i in 0..m_vertexCount-1) {
            _p_Vertices.add(p_Vertices[hull[i]])
        }

        ///
        for (i in 0.._p_Vertices.size-1) {
            // Triangle vertices.
            var p1 = pRef;
            var p2 = _p_Vertices[i]

            var p3 = if(i + 1 < _p_Vertices.size)  _p_Vertices[i+1] else _p_Vertices[0]

            var e1 = p2 - p1
            var e2 = p3 - p1

            var D = Cross(e1, e2);

            var triangleArea = 0.5f * D
            area += triangleArea

            // Area weighted centroid
            c += (p1 + p2 + p3)*triangleArea * inv3
        }

        // Centroid
        if (area > EPSILON)
            c *= 1.0f / area
        else
            area = 0f
        return arrayOf<Any>(area, c)
    }

    fun IntegrateVelocity(id: Int, b: Body, dt: Float) {
        if(b.im == 0.0f) {
            return
        }
        b.position += b.velocity * dt
        b.orient += b.angularVelocity * dt
        b.SetOrient(id, b.orient)
        IntegrateForces(b, dt)
    }
    ///////
    fun Step() {
        MAX_VELCCITY = GetMaxVel()
        // Generate new collision info
        contacts.clear()
        for(i in 0..bodies.size-1) {
            var a = bodies[i]

            for(j in 0..bodies.size-1) {
                if (i == j) {
                    continue
                }
                var b = bodies[j]
                if(a.im == 0f && b.im == 0f) {
                    continue
                }

                if (a.bodyLevel != b.bodyLevel) {
                    continue
                }

                var needContinue = false
                for (jt in joints) {
                    needContinue = false
                    if (!jt.isCollision) {
                        if (a.jointLevel.indexOf(jt.ID) != -1 &&
                                b.jointLevel.indexOf(jt.ID) != -1) {
                            needContinue = true
                            break
                        }
                    }
                }
                if (needContinue) {
                    continue
                }

                var ii = 0
                var jj = 0
                var isContact = false
                for (s1 in a.shapes) {
                    jj = 0
                    for (s2 in b.shapes) {

                        var m = Manifold(a, b)
                        m.idI = ii
                        m.idJ = jj
                        m.Solve()
                        if(m.contact_count != 0) {
                            contacts.add(m)
                            isContact = true
                            //break
                        }

                        jj++
                    }
                    if (isContact) {
                        isContact = !isContact
                        //break
                    }
                    ii++
                }
            }
        }

        // Integrate forces
        for(i in 0..bodies.size-1) {
            IntegrateForces(bodies[i], m_dt)
        }

        // Initialize collision
        for(i in 0..contacts.size-1) {
            if (!contacts[i].a.isWater && !contacts[i].b.isWater)
                contacts[i].Initialize()
        }



        for(j in 0..m_iterations-1) {
            for (i in 0..contacts.size - 1) {
                if (!contacts[i].a.isWater && !contacts[i].b.isWater)
                    contacts[i].ApplyImpulse()
            }
        }

        for(i in 0..bodies.size-1) {
            for (s in 0..bodies[i].shapes.size-1) {
                IntegrateVelocity(s, bodies[i], m_dt)
            }
        }

        // Clear all forces
        for(i in 0..bodies.size-1) {
            var b = bodies[i];
            b.force.Set(0f, 0f)
            b.torque = 0f
        }
        // Solve collisions
        //for(j in 0..m_iterations-1) {
            for (i in 0..contacts.size - 1) {
                if (contacts[i].a.isWater) {
                    var res = PolygonVolume(contacts[i].b, contacts[i].a, contacts[i].idJ, contacts[i].idI)
                    var area = res[0] as Float
                    var centroid = res[1] as Vector2


                    ApplyForce(contacts[i].b, -contacts[i].b.velocity * 0.001f * area,
                            centroid)
                    ApplyForce(contacts[i].b, -contacts[i].b.velocity * 0.05f * area,
                            contacts[i].b.position)
                    ApplyForce(contacts[i].b, -(gravity) * area * contacts[i].b.material.density*0.1f,
                            centroid, 1) //impulse
                    ApplyTorque(contacts[i].b, (area * -contacts[i].b.angularVelocity * 0.003f))
                }
                if (contacts[i].b.isWater) {
                    var res = PolygonVolume(contacts[i].a, contacts[i].b, contacts[i].idI, contacts[i].idJ)
                    var area = res[0] as Float
                    var centroid = res[1] as Vector2


                    ApplyForce(contacts[i].a, -contacts[i].a.velocity * 0.00001f * area,
                            centroid)
                    ApplyForce(contacts[i].a, -contacts[i].a.velocity * 0.0005f * area,
                            contacts[i].a.position)
                    ApplyForce(contacts[i].a, -(gravity) * area * contacts[i].a.material.density*0.1f,
                            centroid, 1) //impulse
                    ApplyTorque(contacts[i].a, (area * -contacts[i].a.angularVelocity * 0.03f))
                }
            }
       // }

        // Correct positions
        for(i in 0..contacts.size-1) {
            if (!contacts[i].a.isWater&&!contacts[i].b.isWater)
                contacts[i].PositionalCorrection()
        }
        for(i in 0..bodies.size-1) {
            var b = bodies[i];
            b.force.Set(0f, 0f)
            b.torque = 0f
        }
        for (j in joints) {
            j.Resolve()
        }

    }

    fun Add(shape: Shape, x: Int, y: Int, m: Material): Body {
        //assert( shape )
        var b = Body(shape, x, y, m.density, m.restitution, m.dynamicFriction, m.staticFriction)
        bodies.add(b)
        return b
    }

    fun Add(b: Body) {
        //assert( shape )
        bodies.add(b)
        //return b
    }

    fun AddJoint(j: Joint) {
        j.ID = joints.size
        j.bA.jointLevel.add(j.ID)
        j.bB.jointLevel.add(j.ID)
        joints.add(j)
    }

    fun DelJoint(j: Joint) {
        j.bA.jointLevel.remove(j.ID)
        j.bB.jointLevel.remove(j.ID)
        joints.remove(j)
    }

    fun Clear() {
        bodies.clear()
    }

    fun ClearDynamic() {
        var sz = bodies.size-1
        var ii = 0
        for (i in 0..sz) {
            if (bodies[ii].isStatic) {
                ii++
                continue
            }
            bodies.removeAt(ii)
        }
    }
}