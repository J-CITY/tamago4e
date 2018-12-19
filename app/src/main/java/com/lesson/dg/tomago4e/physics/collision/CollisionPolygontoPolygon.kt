package com.shooter.game.physics.collision

import com.shooter.game.physics.Body
import com.shooter.game.physics.Manifold
import com.shooter.game.physics.physicsMath.BiasGreaterThan
import com.shooter.game.physics.physicsMath.Dot
import com.shooter.game.physics.physicsMath.Vector2
import com.shooter.game.physics.shape.Polygon

class CollisionPolygontoPolygon: Collision {
    override fun invoke(manifold: Manifold, _a: Body, _b: Body, idA: Int, idB: Int): Boolean {

        manifold.contact_count = 0
        //for (__a in _a.shapes) {
        //    for (__b in _b.shapes) {



        //var a = __a as Polygon
        //var b = __b as Polygon
        var a = _a.shapes[idA] as Polygon
        var b = _b.shapes[idB] as Polygon


        // Check for a separating axis with A's face planes
        var faceA = 0
        var resA = FindAxisLeastPenetration(faceA, a, b)
        var penetrationA = resA[1] as Float
        faceA = resA[0] as Int
        //System.out.println(faceA.toString() + " " + penetrationA.toString())
        if(penetrationA >= 0.0f)
            return if(manifold.contact_count == 0) false else true

        // Check for a separating axis with B's face planes
        var faceB = 0
        var resB = FindAxisLeastPenetration(faceB, b, a)
        var penetrationB = resB[1] as Float
        faceB = resB[0] as Int
        //System.out.println(faceB.toString() + " " + penetrationB.toString())
        if(penetrationB >= 0.0f)
            return if(manifold.contact_count == 0) false else true

        var referenceIndex = 0
        var flip = false // Always point from a to b

        var RefPoly: Polygon // Reference
        var IncPoly: Polygon // Incident

        // Determine which shape contains reference face
        if(BiasGreaterThan(penetrationA, penetrationB)) {
            RefPoly = a
            IncPoly = b
            referenceIndex = faceA
            flip = false
        } else {
            RefPoly = b
            IncPoly = a
            referenceIndex = faceB
            flip = true
        }

        // World space incident face
        var incidentFace = Array(2, { Vector2() })
        FindIncidentFace(incidentFace, RefPoly, IncPoly, referenceIndex)


        //        y
        //        ^  ->n       ^
        //      +---c ------posPlane--
        //  x < | i |\
        //      +---+ c-----negPlane--
        //             \       v
        //              r
        //
        //  r : reference face
        //  i : incident poly
        //  c : clipped point
        //  n : incident normal

        // Setup reference face vertices
        var v1 = RefPoly.m_vertices[referenceIndex]
        referenceIndex = if(referenceIndex + 1 == RefPoly.m_vertexCount) 0 else referenceIndex + 1
        var v2 = RefPoly.m_vertices[referenceIndex]

        // Transform vertices to world space
        v1 = RefPoly.u * (v1 + RefPoly.localPos) + RefPoly.body.position
        v2 = RefPoly.u * (v2 + RefPoly.localPos) + RefPoly.body.position

        // Calculate reference face side normal in world space
        var sidePlaneNormal = (v2 - v1)
        sidePlaneNormal.Normalize()
        //sidePlaneNormal.y = sidePlaneNormal.x
        //sidePlaneNormal.x = 0f
        //System.out.println("Normal "+sidePlaneNormal.x+" "+ sidePlaneNormal.y)


        // Orthogonalize
        var refFaceNormal = Vector2(sidePlaneNormal.y, -sidePlaneNormal.x)

        // ax + by = c
        // c is distance from origin
        var refC = Dot(refFaceNormal, v1)
        var negSide = -Dot(sidePlaneNormal, v1)
        var posSide =  Dot(sidePlaneNormal, v2)

        // Clip incident face to reference face side planes
        if(Clip( -sidePlaneNormal, negSide, incidentFace ) < 2) {
            //System.out.println("###"+Clip( sidePlaneNormal, negSide, incidentFace ))
            return if(manifold.contact_count == 0) false else true
        }// Due to floating point error, possible to not have required points

        if(Clip(  sidePlaneNormal, posSide, incidentFace ) < 2) {
            //System.out.println("!!!"+Clip(  -sidePlaneNormal, posSide, incidentFace ))
            return if(manifold.contact_count == 0) false else true
        } // Due to floating point error, possible to not have required points

        // Flip
        manifold.normal = if(flip) -refFaceNormal else refFaceNormal

        // Keep points behind reference face
        var cp = 0; // clipped points behind reference face
        var separation = Dot( refFaceNormal, incidentFace[0] ) - refC
        //System.out.println(separation)
        if(separation <= 0.0f) {
            manifold.contacts[cp] = incidentFace[0]
            manifold.penetration = -separation
            cp += 1
        } else {
            manifold.penetration = 0f
        }
        separation = Dot( refFaceNormal, incidentFace[1] ) - refC
        //System.out.println("other"+separation)
        if(separation <= 0.0f) {
            manifold.contacts[cp].Set(incidentFace[1])

            manifold.penetration += -separation
            cp += 1

            // Average penetration
            manifold.penetration /= cp.toFloat()
        }

        manifold.contact_count += cp
        //    }
        //}
        //System.out.println("@@"+cp)
        return if(manifold.contact_count == 0) false else true
    }

    fun FindAxisLeastPenetration(faceIndex: Int, _a: Polygon, _b: Polygon): Array<Any> {
        var bestDistance = -Float.MAX_VALUE
        var bestIndex = 0

        for(i in 0.._a.m_vertexCount-1) {
            // Retrieve a face normal from A
            var n = _a.m_normals[i]
            var nw = _a.u * n

            // Transform face normal into B's model space
            var buT = _b.u.Transpose()
            n = buT * nw

            // Retrieve support point from B along -n
            var s = _b.GetSupport(-n)

            // Retrieve vertex on face from A, transform into
            // B's model space
            var v = _a.m_vertices[i]
            v = _a.u * (v+_a.localPos) + _a.body.position
            v = v - _b.body.position + _b.localPos
            v = buT * v;

            // Compute penetration distance (in B's model space)
            var d = Dot( n, s - v );

            // Store greatest distance
            if(d > bestDistance) {
                bestDistance = d;
                bestIndex = i;
            }
        }

        //faceIndex = bestIndex
        return arrayOf(bestIndex, bestDistance)
    }

    fun FindIncidentFace(v: Array<Vector2>, RefPoly: Polygon, IncPoly: Polygon, referenceIndex: Int) {
        var referenceNormal = RefPoly.m_normals[referenceIndex]

        // Calculate normal in incident's frame of reference
        referenceNormal = RefPoly.u * referenceNormal // To world space
        referenceNormal = IncPoly.u.Transpose() * referenceNormal // To incident's model space

        // Find most anti-normal face on incident polygon
        var incidentFace = 0
        var minDot = Float.MAX_VALUE
        for(i in 0..IncPoly.m_vertexCount-1) {
            var dot = Dot( referenceNormal, IncPoly.m_normals[i])
            if(dot < minDot) {
                minDot = dot
                incidentFace = i
            }
        }

        // Assign face vertices for incidentFace
        v[0] = IncPoly.u * (IncPoly.m_vertices[incidentFace]+IncPoly.localPos) + IncPoly.body.position
        incidentFace = if(incidentFace + 1 >= IncPoly.m_vertexCount) 0 else incidentFace + 1
        v[1] = IncPoly.u * (IncPoly.m_vertices[incidentFace]+IncPoly.localPos) + IncPoly.body.position

    }

    fun Clip(n: Vector2, c: Float, face: Array<Vector2>): Int {
        var sp = 0
        var out = arrayOf(face[0], face[1])

        // Retrieve distances from each endpoint to the line
        // d = ax + by - c
        var d1 = Dot( n, face[0] ) - c;
        var d2 = Dot( n, face[1] ) - c;

        // If negative (behind plane) clip
        if(d1 <= 0.0f)
            out[sp++].Set(face[0])
        if(d2 <= 0.0f)
            out[sp++].Set(face[1])

        // If the points are on different sides of the plane
        if(d1 * d2 < 0.0f) {// less than to ignore -0.0f
            // Push interesection point
            var alpha = d1 / (d1 - d2);
            out[sp] = face[0] + (face[1] - face[0]) * alpha
            sp += 1
        }

        // Assign our new converted values
        face[0] = out[0];
        face[1] = out[1];

        //assert(sp != 3)

        return sp
    }

    constructor() : super() {
    }
}