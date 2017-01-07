package com.example.glpicking;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.example.glpicking.common.Color;
import com.example.glpicking.common.Point3D;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;

public class ExampleGLObject {
    private FloatBuffer verticesBuffer;
    private short[] indices;
    private ShortBuffer indicesBuffer;
    private float vertices[];

    private String name;

    private Color color;
    private Point3D position;

    public ExampleGLObject(String name, Color color, Point3D position) {
        this.name = name;

        this.color = color;
        this.position = position;

        vertices = new float[]{
                -0.5f, 0.5f, 0.0f,   // top left
                -0.5f, -0.5f, 0.0f,   // bottom left
                0.5f, -0.5f, 0.0f,   // bottom right
                0.5f, 0.5f, 0.0f  // top right
        };

        indices = new short[]{0, 1, 2, 0, 2, 3};

        verticesBuffer = allocateDirect(vertices.length * 4).order(nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);

        indicesBuffer = allocateDirect(indices.length * 2).order(nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
    }

    public void draw(GL10 gl, Ray ray) {
        gl.glLoadIdentity();

        Point3D eye = new Point3D(0.0f, 0.0f, 5.0f);
        Point3D center = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);

        GLU.gluLookAt(gl, eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);

        gl.glTranslatef(position.x, position.y, position.z);

        if (ray != null) {

            MatrixGrabber matrixGrabber = new MatrixGrabber();
            matrixGrabber.getCurrentState(gl);

            int coordCount = vertices.length;
            float[] convertedSquare = new float[coordCount];
            float[] resultVector = new float[4];
            float[] inputVector = new float[4];

            for (int i = 0; i < coordCount; i = i + 3) {
                inputVector[0] = vertices[i];
                inputVector[1] = vertices[i + 1];
                inputVector[2] = vertices[i + 2];
                inputVector[3] = 1;
                Matrix.multiplyMV(resultVector, 0, matrixGrabber.mModelView, 0, inputVector, 0);
                convertedSquare[i] = resultVector[0] / resultVector[3];
                convertedSquare[i + 1] = resultVector[1] / resultVector[3];
                convertedSquare[i + 2] = resultVector[2] / resultVector[3];
            }

            Triangle t1 = new Triangle(
                    new float[]{convertedSquare[0], convertedSquare[1], convertedSquare[2]},
                    new float[]{convertedSquare[3], convertedSquare[4], convertedSquare[5]},
                    new float[]{convertedSquare[6], convertedSquare[7], convertedSquare[8]});
            Triangle t2 = new Triangle(
                    new float[]{convertedSquare[0], convertedSquare[1], convertedSquare[2]},
                    new float[]{convertedSquare[6], convertedSquare[7], convertedSquare[8]},
                    new float[]{convertedSquare[9], convertedSquare[10], convertedSquare[11]});

            float[] point1 = new float[3];
            int intersects1 = Triangle.intersectRayAndTriangle(ray, t1, point1);
            float[] point2 = new float[3];
            int intersects2 = Triangle.intersectRayAndTriangle(ray, t2, point2);

            if (intersects1 == 1 || intersects1 == 2) {
                Log.d("test", "touch!: " + name);
            } else if (intersects2 == 1 || intersects2 == 2) {
                Log.d("test", "touch!: " + name);
            }
        }

        gl.glColor4f(color.red, color.green, color.blue, color.alpha);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glFrontFace(GL10.GL_CW);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);

        gl.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES10.GL_UNSIGNED_SHORT, indicesBuffer);
    }
}