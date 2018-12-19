package com.shooter.game.physics.shape

import com.shooter.game.physics.physicsMath.Cross
import com.shooter.game.physics.physicsMath.Dot
import com.shooter.game.physics.physicsMath.EPSILON
import com.shooter.game.physics.physicsMath.Vector2

class Polygon: Shape {
    constructor() : super() {

    }
    override fun LocalPos(_lp: Vector2) {
        localPos = _lp
    }

    var MaxPolyVertexCount = 64

    var m_vertexCount = 0
    var m_vertices = Array(MaxPolyVertexCount, {Vector2()})
    var m_normals = Array(MaxPolyVertexCount, {Vector2()})

    override fun Clone(): Shape {
        var poly = Polygon()
        poly.u = u
        for (i in 0..m_vertexCount-1) {
            poly.m_vertices[i] = m_vertices[i]
            poly.m_normals[i] = m_normals[i]
        }
        poly.m_vertexCount = m_vertexCount
        poly.localPos = localPos
        return poly
    }

    override fun Initialize(density: Float) {
        ComputeMass(density)
        ComputeMass(density)
    }

    override fun ComputeMass(density: Float) {
        var c = Vector2(0.0f, 0.0f)// centroid
        var area = 0.0f;
        var I = 0.0f;

        val k_inv3 = 1.0f / 3.0f;

        for (i1 in 0..m_vertexCount-1) {
            // Triangle vertices, third vertex implied as (0, 0)
            var p1 = Vector2(m_vertices[i1])
            var i2 = if (i1 + 1 < m_vertexCount) i1 + 1 else 0
            var p2 = Vector2(m_vertices[i2])

            var D = Cross(p1, p2)
            var triangleArea = 0.5f * D

            area += triangleArea

            // Use area to weight the centroid average, not just vertex position
            c += (p1 + p2) * (triangleArea * k_inv3)

            var intx2 = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x
            var inty2 = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y
            I += (0.25f * k_inv3 * D) * (intx2 + inty2)
        }

        c *= (1.0f / area)

        // Translate vertices to centroid (make the centroid (0, 0)
        // for the polygon in model space)
        // Not really necessary, but I like doing this anyway
        for (i in 0..m_vertexCount-1) {
            m_vertices[i] -= c
        }
        m = density * area
        body.m = density * area
        body.im = if(body.m != 0f) 1.0f / body.m else 0.0f
        body.I = I * density;
        body.iI = if(body.I != 0f) 1.0f / body.I else 0.0f;
    }

    override fun SetOrient(radians: Float) {
        u.Set(radians)
    }

    override fun GetType(): Type {
        return Type.ePoly
    }

    // Half width and half height
    fun SetBox(hw: Float, hh: Float) {
        print("LOCAL")
        print(localPos.x)
        m_vertexCount = 4;
        m_vertices[0].Set(-hw, -hh)
        m_vertices[1].Set( hw, -hh)
        m_vertices[2].Set( hw,  hh)
        m_vertices[3].Set(-hw,  hh)
        m_normals[0].Set(  0.0f,  -1.0f )
        m_normals[1].Set(  1.0f,   0.0f )
        m_normals[2].Set(  0.0f,   1.0f )
        m_normals[3].Set( -1.0f,   0.0f )
    }

    fun Set(vertices: Array<Vector2>, count: Int) {
        // No hulls with less than 3 vertices (ensure actual polygon)

        assert( count > 2 && count <= MaxPolyVertexCount)
        //count = Math.min(count, MaxPolyVertexCount)
        // Find the right most point on the hull
        var rightMost = 0;
        var highestXCoord = vertices[0].x;
        for(i in 1..count-1) {
            var x = vertices[i].x;
            if(x > highestXCoord) {
                highestXCoord = x;
                rightMost = i;
            } else if(x == highestXCoord) { // If matching x then take farthest negative y
                if (vertices[i].y < vertices[rightMost].y) {
                    rightMost = i
                }
            }
        }

        var hull = Array(MaxPolyVertexCount, {0})
        var outCount = 0
        var indexHull = rightMost

        while (true) {
            hull[outCount] = indexHull

            // Search for next index that wraps around the hull
            // by computing cross products to find the most counter-clockwise
            // vertex in the set, given the previos hull index
            var nextHullIndex = 0
            for(i in 1..count-1) {
                // Skip if same coordinate as we need three unique
                // points in the set to perform a cross product
                if(nextHullIndex == indexHull) {
                    nextHullIndex = i
                    continue;
                }

                // Cross every set of three unique vertices
                // Record each counter clockwise third vertex and add
                // to the output hull
                // See : http://www.oocities.org/pcgpe/math2d.html
                var e1 = vertices[nextHullIndex] - vertices[hull[outCount]]
                var e2 = vertices[i] - vertices[hull[outCount]]
                var c = Cross( e1, e2 )
                if(c < 0.0f)
                    nextHullIndex = i

                // Cross product is zero then e vectors are on same line
                // therefor want to record vertex farthest along that line
                if(c == 0.0f && e2.LenSqr() > e1.LenSqr()) {
                    nextHullIndex = i
                }
            }

            outCount += 1
            indexHull = nextHullIndex;

            // Conclude algorithm upon wrap-around
            if(nextHullIndex == rightMost) {
                m_vertexCount = outCount
                break
            }
        }

        // Copy vertices into shape's vertices
        for(i in 0..m_vertexCount-1) {
            m_vertices[i] = vertices[hull[i]]
        }

        for (v in 0..m_vertices.size-1) {
            //localPos.Rotate(body.orient)
            m_vertices[v] += localPos
        }
        // Compute face normals
        for(i1 in 0..m_vertexCount-1) {
            var i2 = if(i1 + 1 < m_vertexCount) i1 + 1 else 0
            var face = m_vertices[i2] - m_vertices[i1]

            // Ensure no zero-length edges, because that's bad
            assert( face.LenSqr( ) > EPSILON * EPSILON );

            // Calculate normal with 2D cross product between vector and scalar
            m_normals[i1] = Vector2(face.y, -face.x)
            m_normals[i1].Normalize()
        }

    }

    // The extreme point along a direction within a polygon
    fun GetSupport(dir: Vector2): Vector2 {
        var bestProjection = -java.lang.Float.MAX_VALUE;
        var bestVertex = Vector2()

        for(i in 0..m_vertexCount-1) {
            var v = m_vertices[i];
            var projection = Dot( v, dir );

            if(projection > bestProjection) {
                bestVertex = v;
                bestProjection = projection;
            }
        }

        return bestVertex;
    }


}
