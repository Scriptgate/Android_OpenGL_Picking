package com.example.glpicking;

import com.example.glpicking.common.Point3D;

public class Triangle {

    private static final float SMALL_NUM = 0.00000001f; // anything that avoids division overflow

    private Point3D V0;
    private Point3D V1;
    private Point3D V2;

    public Triangle(Point3D V0, Point3D V1, Point3D V2) {
        this.V0 = V0;
        this.V1 = V1;
        this.V2 = V2;
    }

    /**
     * @param R a ray R
     * @param T a triangle T
     * @param I intersection point (when it exists)
     * @return
     * -1 = triangle is degenerate (a segment or point)<br/>
     * 0 = disjoint (no intersect)<br/>
     * 1 = intersect in unique point I1<br/>
     * 2 = are in the same plane
     */
    public static int intersectRayAndTriangle(Ray R, Triangle T, Point3D I) {
        Point3D u, v, n;             // triangle vectors
        Point3D dir, w0, w;          // ray vectors
        float r, a, b;             // params to calc ray-plane intersect

        // get triangle edge vectors and plane normal
        u = Point3D.minus(T.V1, T.V0);
        v = Point3D.minus(T.V2, T.V0);
        n = Point3D.crossProduct(u, v);             // cross product

        if (n.isZero()) {           // triangle is degenerate
            return -1;                 // do not deal with this case
        }
        dir = Point3D.minus(R.P1, R.P0);             // ray direction vector
        w0 = Point3D.minus(R.P0, T.V0);
        a = -Point3D.dot(n, w0);
        b = Point3D.dot(n, dir);
        if (Math.abs(b) < SMALL_NUM) {     // ray is parallel to triangle plane
            if (a == 0) {                // ray lies in triangle plane
                return 2;
            } else {
                return 0;             // ray disjoint from plane
            }
        }

        // get intersect point of ray with triangle plane
        r = a / b;
        if (r < 0.0f) {                   // ray goes away from triangle
            return 0;                  // => no intersect
        }
        // for a segment, also test if (r > 1.0) => no intersect

        Point3D tempI = Point3D.addition(R.P0, Point3D.scalarProduct(r, dir));           // intersect point of ray and plane
        I.x = tempI.x;
        I.y = tempI.y;
        I.z = tempI.z;

        // is I inside T?
        float uu, uv, vv, wu, wv, D;
        uu = Point3D.dot(u, u);
        uv = Point3D.dot(u, v);
        vv = Point3D.dot(v, v);
        w = Point3D.minus(I, T.V0);
        wu = Point3D.dot(w, u);
        wv = Point3D.dot(w, v);
        D = (uv * uv) - (uu * vv);

        // get and test parametric coords
        float s, t;
        s = ((uv * wv) - (vv * wu)) / D;
        if (s < 0.0f || s > 1.0f) {
            // I is outside T
            return 0;
        }
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0f || (s + t) > 1.0f) {
            // I is outside T
            return 0;
        }
        return 1;                      // I is in T
    }
}