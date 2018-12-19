package com.shooter.game.physics.collision

import com.shooter.game.physics.Body
import com.shooter.game.physics.shape.Circle
import com.shooter.game.physics.Manifold
import com.shooter.game.physics.shape.Polygon
import com.shooter.game.physics.physicsMath.DistSqr
import com.shooter.game.physics.physicsMath.Dot
import com.shooter.game.physics.physicsMath.EPSILON

class CollisionCircletoPolygon: Collision {
    override fun invoke(manifold: Manifold, _a: Body, _b: Body, idA: Int, idB: Int): Boolean {
        var a: Circle = _a.shapes[idA] as Circle
        var b: Polygon = _b.shapes[idB] as Polygon

        manifold.contact_count = 0

        // Transform circle center to Polygon model space
        var center = _a.position + a.localPos
        center = b.u.Transpose() * (center - (_b.position + b.localPos))

        // Find edge with minimum penetration
        // Exact concept as using support points in Polygon vs Polygon
        var separation = -Float.MAX_VALUE
        var faceNormal = 0
        for(i in 0..b.m_vertexCount-1) {
            var s = Dot(b.m_normals[i], center - b.m_vertices[i])

            if(s > a.radius) {
                return if(manifold.contact_count == 0) false else true
            }

            if(s > separation) {
                separation = s
                faceNormal = i
            }
        }

        // Grab face's vertices
        var v1 = b.m_vertices[faceNormal]
        var i2 = if(faceNormal + 1 < b.m_vertexCount) faceNormal + 1 else 0
        var v2 = b.m_vertices[i2]

        // Check to see if center is within polygon
        if(separation < EPSILON) {
            manifold.contact_count = 1
            manifold.normal = -(b.u * b.m_normals[faceNormal])
            manifold.contacts[0] = manifold.normal * a.radius + _a.position + a.localPos
            manifold.penetration = a.radius
            return if(manifold.contact_count == 0) false else true
        }

        // Determine which voronoi region of the edge center of circle lies within
        var dot1 = Dot( center - v1, v2 - v1 );
        var dot2 = Dot( center - v2, v1 - v2 );
        manifold.penetration = a.radius - separation

        // Closest to v1
        if(dot1 <= 0.0f) {
            if(DistSqr( center, v1 ) > a.radius * a.radius) {
                return if(manifold.contact_count == 0) false else true
            }

            manifold.contact_count = 1
            var n = v1 - center
            n = b.u * n
            n.Normalize()
            manifold.normal = n
            v1 = b.u * v1 + _b.position + b.localPos
            manifold.contacts[0] = v1
        } else if(dot2 <= 0.0f) {// Closest to v2
            if(DistSqr( center, v2 ) > a.radius * a.radius) {
                return if(manifold.contact_count == 0) false else true
            }

            manifold.contact_count = 1
            var n = v2 - center
            v2 = b.u * v2 + _b.position + b.localPos
            manifold.contacts[0] = v2
            n = b.u * n;
            n.Normalize( );
            manifold.normal = n;
        } else {// Closest to face
            var n = b.m_normals[faceNormal]
            if (Dot(center - v1, n) > a.radius) {
                return if(manifold.contact_count == 0) false else true
            }
            n = b.u * n
            manifold.normal = -n
            manifold.contacts[0] = manifold.normal * a.radius + _a.position + a.localPos
            manifold.contact_count = 1
        }
        return if(manifold.contact_count == 0) false else true
    }
    constructor() : super() {
    }
}
