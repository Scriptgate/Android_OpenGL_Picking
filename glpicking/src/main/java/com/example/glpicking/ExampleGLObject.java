package com.example.glpicking;

import android.opengl.Matrix;

import com.example.glpicking.common.Color;
import com.example.glpicking.common.Intersection;
import com.example.glpicking.common.Point3D;
import com.example.glpicking.program.AttributeVariable;
import com.example.glpicking.program.Program;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.example.glpicking.common.Color.MAGENTA;
import static com.example.glpicking.program.UniformVariable.MVP_MATRIX;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

public class ExampleGLObject {

    private FloatBuffer verticesBuffer;
    private List<Point3D> points;
    private float vertices[];

    private boolean selected = false;

    private Color color;
    private Point3D position;

    public ExampleGLObject(Color color, Point3D position) {

        this.color = color;
        this.position = position;

        points = new ArrayList<>();
        points.add(new Point3D(-0.5f, 0.5f, 0.0f));// top left
        points.add(new Point3D(0.5f, 0.5f, 0.0f));// top right
        points.add(new Point3D(-0.5f, -0.5f, 0.0f));// bottom left
        points.add(new Point3D(0.5f, -0.5f, 0.0f));// bottom right

        vertices = new float[]{
                points.get(0).x, points.get(0).y, points.get(0).z,
                points.get(2).x, points.get(2).y, points.get(2).z,
                points.get(1).x, points.get(1).y, points.get(1).z,
                points.get(2).x, points.get(2).y, points.get(2).z,
                points.get(3).x, points.get(3).y, points.get(3).z,
                points.get(1).x, points.get(1).y, points.get(1).z
        };

        verticesBuffer = allocateDirect(vertices.length * 4).order(nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
    }

    public void draw(Ray ray, Program program, float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] mvpMatrix) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        if (ray != null) {

            List<Point3D> convertedSquare = new ArrayList<>();
            float[] resultVector = new float[4];
            float[] inputVector = new float[4];

            for (int i = 0; i < points.size(); i++) {
                inputVector[0] = points.get(i).x;
                inputVector[1] = points.get(i).y;
                inputVector[2] = points.get(i).z;
                inputVector[3] = 1;
                Matrix.multiplyMV(resultVector, 0, mvpMatrix, 0, inputVector, 0);
                convertedSquare.add(new Point3D(
                        resultVector[0] / resultVector[3],
                        resultVector[1] / resultVector[3],
                        resultVector[2] / resultVector[3])
                );
            }

            Triangle t1 = new Triangle(
                    convertedSquare.get(0),
                    convertedSquare.get(2),
                    convertedSquare.get(3)
            );
            Triangle t2 = new Triangle(
                    convertedSquare.get(0),
                    convertedSquare.get(3),
                    convertedSquare.get(1)
            );

            Intersection intersects1 = Triangle.intersectRayAndTriangle(ray, t1);
            Intersection intersects2 = Triangle.intersectRayAndTriangle(ray, t2);

            selected = intersects1.isIntersect() || intersects2.isIntersect();
        }

        int colorHandle = program.getHandle(AttributeVariable.COLOR);
        glDisableVertexAttribArray(colorHandle);
        if (selected) {
            glVertexAttrib4fv(colorHandle, MAGENTA.toArray(), 0);
        } else {
            glVertexAttrib4fv(colorHandle, color.toArray(), 0);
        }

        int positionHandle = program.getHandle(AttributeVariable.POSITION);
        verticesBuffer.position(0);
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, verticesBuffer);
        glEnableVertexAttribArray(positionHandle);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        glUniformMatrix4fv(program.getHandle(MVP_MATRIX), 1, false, mvpMatrix, 0);

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}