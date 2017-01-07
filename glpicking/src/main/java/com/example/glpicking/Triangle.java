package com.example.glpicking;

import com.example.glpicking.common.Intersection;
import com.example.glpicking.common.Point3D;

import static com.example.glpicking.common.Intersection.degenerate;
import static com.example.glpicking.common.Intersection.disjoint;
import static com.example.glpicking.common.Intersection.intersect;
import static com.example.glpicking.common.Intersection.samePlane;
import static com.example.glpicking.common.Point3D.addition;
import static com.example.glpicking.common.Point3D.crossProduct;
import static com.example.glpicking.common.Point3D.dot;
import static com.example.glpicking.common.Point3D.minus;
import static com.example.glpicking.common.Point3D.scalarProduct;
import static java.lang.Math.abs;

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
     * @return -1 = triangle is degenerate (a segment or point)<br/>
     * 0 = disjoint (no intersect)<br/>
     * 1 = intersect in unique point I1<br/>
     * 2 = are in the same plane
     */
    public static Intersection intersectRayAndTriangle(Ray R, Triangle T) {
        // triangle vectors
        Point3D u, v, normal;
        // ray vectors
        Point3D dir, w0, w;
        // params to calc ray-plane intersect
        float r, a, b;

        // get triangle edge vectors and plane normal
        u = minus(T.V1, T.V0);
        v = minus(T.V2, T.V0);
        normal = crossProduct(u, v);

        if (normal.isZero()) {
            // triangle is degenerate, do not deal with this case
            return degenerate();
        }
        // ray direction vector
        dir = minus(R.P1, R.P0);
        w0 = minus(R.P0, T.V0);
        a = -dot(normal, w0);
        b = dot(normal, dir);
        if (abs(b) < SMALL_NUM) {
            // ray is parallel to triangle plane
            if (a == 0) {
                // ray lies in triangle plane
                return samePlane();
            } else {
                // ray disjoint from plane
                return disjoint();
            }
        }

        // get intersect point of ray with triangle plane
        r = a / b;
        if (r < 0.0f) {
            // ray goes away from triangle => no intersect
            return disjoint();
        }
        //TODO: for a segment, also test if (r > 1.0) => no intersect

        // intersect point of ray and plane
        Point3D intersectionPoint = addition(R.P0, scalarProduct(r, dir));

        // is the intersection point inside the triangle?
        float uu, uv, vv, wu, wv, D;
        uu = dot(u, u);
        uv = dot(u, v);
        vv = dot(v, v);
        w = minus(intersectionPoint, T.V0);
        wu = dot(w, u);
        wv = dot(w, v);
        D = (uv * uv) - (uu * vv);

        // get and test parametric coords
        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.0f || s > 1.0f) {
            // intersection point is outside triangle
            return disjoint();
        }
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0f || (s + t) > 1.0f) {
            // intersection point is outside triangle
            return disjoint();
        }
        // intersection point is in triangle
        return intersect(intersectionPoint);
    }
}