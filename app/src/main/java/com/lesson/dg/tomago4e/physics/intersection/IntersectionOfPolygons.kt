package com.shooter.game.physics.intersection

import com.shooter.game.physics.physicsMath.Vector2


fun GetIntersectionOfPolygons(poly1: MutableList<Vector2>, poly2: MutableList<Vector2>): MutableList<Vector2> {
    val clippedCorners = mutableListOf<Vector2>()

    //Add  the corners of poly1 which are inside poly2
    for (i in 0..poly1.size-1) {
        if (IsPointInsidePoly(poly1[i], poly2))
            AddPoints(clippedCorners, mutableListOf(poly1[i]))
    }

    //Add the corners of poly2 which are inside poly1
    for (i in 0..poly2.size-1) {
        if (IsPointInsidePoly(poly2[i], poly1))
            AddPoints(clippedCorners, mutableListOf(poly2[i]))
    }

    //Add  the intersection points
    var i = 0
    var next = 1
    while (i < poly1.size-1) {
        AddPoints(clippedCorners, GetIntersectionPoints(poly1[i], poly1[next], poly2))
        i++
        next = if (i + 1 == poly1.size) 0 else i + 1
    }

    return OrderClockwise(clippedCorners)
}

val EquityTolerance = 0.00001
fun IsEqual(d1: Float, d2: Float): Boolean {
    return Math.abs(d1 - d2) <= EquityTolerance
}

fun AddPoints(pool: MutableList<Vector2>, newpoints: MutableList<Vector2>) {
    for (np in newpoints) {
        var found = false
        for (p in pool) {
            if (IsEqual(p.x, np.x) && IsEqual(p.y, np.y)) {
                found = true;
                break;
            }
        }
        if (!found) pool.add(np);
    }
}

fun GetIntersectionPoints(l1p1: Vector2, l1p2: Vector2, poly: MutableList<Vector2>): MutableList<Vector2> {
    var intersectionPoints = mutableListOf<Vector2>()
    for (i in 0..poly.size-1) {
        var next = if (i + 1 == poly.size) 0 else i + 1

        var ip = GetIntersectionPoint(l1p1, l1p2, poly[i], poly[next])

        if (ip != null) intersectionPoints.add(ip);
    }

    return intersectionPoints
}

fun IsPointInsidePoly(test: Vector2, poly: MutableList<Vector2>): Boolean {
    var j = poly.size - 1
    var result = false;
    for (i in 0..poly.size-1) {
        if ((poly[i].y > test.y) != (poly[j].y > test.y) &&
                (test.x < (poly[j].x - poly[i].x) *
                        (test.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x)) {
            result = !result
        }
        j = i
    }
    return result
}

fun GetIntersectionPoint(l1p1: Vector2, l1p2: Vector2, l2p1: Vector2, l2p2: Vector2): Vector2? {
    var A1 = l1p2.y - l1p1.y
    var B1 = l1p1.x - l1p2.x
    var C1 = A1 * l1p1.x + B1 * l1p1.y

    var A2 = l2p2.y - l2p1.y
    var B2 = l2p1.x - l2p2.x
    var C2 = A2 * l2p1.x + B2 * l2p1.y

    //lines are parallel
    var det = A1 * B2 - A2 * B1;
    if (IsEqual(det, 0f)) {
        return null
    } else {
        var x = (B2 * C1 - B1 * C2) / det;
        var y = (A1 * C2 - A2 * C1) / det;
        var online1 = ((Math.min(l1p1.x, l1p2.x) < x || IsEqual(Math.min(l1p1.x, l1p2.x), x))
                && (Math.max(l1p1.x, l1p2.x) > x || IsEqual(Math.max(l1p1.x, l1p2.x), x))
                && (Math.min(l1p1.y, l1p2.y) < y || IsEqual(Math.min(l1p1.y, l1p2.y), y))
                && (Math.max(l1p1.y, l1p2.y) > y || IsEqual(Math.max(l1p1.y, l1p2.y), y))
        )
        var online2 = ((Math.min(l2p1.x, l2p2.x) < x || IsEqual(Math.min(l2p1.x, l2p2.x), x))
                && (Math.max(l2p1.x, l2p2.x) > x || IsEqual(Math.max(l2p1.x, l2p2.x), x))
                && (Math.min(l2p1.y, l2p2.y) < y || IsEqual(Math.min(l2p1.y, l2p2.y), y))
                && (Math.max(l2p1.y, l2p2.y) > y || IsEqual(Math.max(l2p1.y, l2p2.y), y))
        )

        if (online1 && online2)
            return Vector2(x, y)
    }
    return null; //intersection is at out of at least one segment.
}

fun OrderClockwise(points: MutableList<Vector2>): MutableList<Vector2> {
    var mX = 0f
    var my = 0f
    for (p in points){
        mX += p.x
        my += p.y
    }
    mX /= points.size
    my /= points.size

    return points.sortedWith(CompareObjects).toMutableList()
}

class CompareObjects {

    companion object : Comparator<Vector2> {

        override fun compare(a: Vector2, b: Vector2): Int {
            return if (Math.atan2((a.y - b.y).toDouble(), (a.x - b.x).toDouble()).toFloat() > 0) 1 else -1
        }
    }
}