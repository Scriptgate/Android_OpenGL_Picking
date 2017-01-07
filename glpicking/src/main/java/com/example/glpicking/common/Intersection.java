package com.example.glpicking.common;


public class Intersection {


    private enum IntersectionType {
        DEGENERATE, // triangle is degenerate (a segment or point)
        DISJOINT, // disjoint (no intersect)
        INTERSECT, // intersect in unique point I1<br/>
        SAME_PLANE // are in the same plane
    }

    public static Intersection degenerate() {
        return new Intersection(IntersectionType.DEGENERATE);
    }

    public static Intersection disjoint() {
        return new Intersection(IntersectionType.DISJOINT);
    }

    public static Intersection intersect(Point3D intersectionPoint) {
        return new Intersection(IntersectionType.INTERSECT, intersectionPoint);
    }

    public static Intersection samePlane() {
        return new Intersection(IntersectionType.SAME_PLANE);
    }

    private IntersectionType type;

    private final Point3D point;

    private Intersection(IntersectionType type) {
        this(type, null);
    }

    private Intersection(IntersectionType type, Point3D point) {
        this.type = type;
        this.point = point;
    }

    public boolean isIntersect() {
        return type == IntersectionType.INTERSECT || type == IntersectionType.SAME_PLANE;
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "type=" + type +
                ", point=" + point +
                '}';
    }


}
