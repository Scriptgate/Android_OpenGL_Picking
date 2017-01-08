package com.example.glpicking;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.example.glpicking.common.Point3D;

public class Ray {

    private static final float NEAR = 1.0f;
    private static final float FAR = 0.0f;

    Point3D P0;
    Point3D P1;

    public Ray(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] mvpMatrix, int width, int height, float xTouch, float yTouch) {

        int[] viewport = {0, 0, width, height};

        float winX = xTouch;
        float winY = (float) viewport[3] - yTouch;

        //Read more here: http://myweb.lmu.edu/dondi/share/cg/unproject-explained.pdf
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        this.P0 = screenCoordinatesToViewCoordinates(winX, winY, FAR, mvpMatrix, projectionMatrix, viewport);
        this.P1 = screenCoordinatesToViewCoordinates(winX, winY, NEAR, mvpMatrix, projectionMatrix, viewport);
    }

    private Point3D screenCoordinatesToViewCoordinates(float winX, float winY, float winZ, float[] mvMatrix, float[] projectionMatrix, int[] viewport) {
        float[] temp = new float[4];
        Point3D farCoordinates = new Point3D();

        int result = GLU.gluUnProject(winX, winY, winZ, mvMatrix, 0, projectionMatrix, 0, viewport, 0, temp, 0);
        Matrix.multiplyMV(temp, 0, mvMatrix, 0, temp, 0);
        if (result == GL10.GL_TRUE) {
            farCoordinates = new Point3D(
                    temp[0] / temp[3],
                    temp[1] / temp[3],
                    temp[2] / temp[3]);
        }
        return farCoordinates;
    }
}
