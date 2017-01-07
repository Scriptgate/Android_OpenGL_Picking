package com.example.glpicking;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.example.glpicking.common.Point3D;

public class Ray {

    Point3D P0;
    Point3D P1;

    public Ray(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] mvpMatrix, int width, int height, float xTouch, float yTouch) {

        int[] viewport = {0, 0, width, height};

        float[] temp = new float[4];
        float[] temp2 = new float[4];
        // get the near and far ords for the click

        float winX = xTouch;
        float winY = (float) viewport[3] - yTouch;

        //Read more here: http://myweb.lmu.edu/dondi/share/cg/unproject-explained.pdf
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Point3D nearCoords = new Point3D();
        int result = GLU.gluUnProject(winX, winY, 1.0f, mvpMatrix, 0, projectionMatrix, 0, viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, mvpMatrix, 0, temp, 0);
        if (result == GL10.GL_TRUE) {
            nearCoords = new Point3D(
                    temp2[0] / temp2[3],
                    temp2[1] / temp2[3],
                    temp2[2] / temp2[3]);

        }
        this.P1 = nearCoords;

        Point3D farCoords = new Point3D();
        result = GLU.gluUnProject(winX, winY, 0, mvpMatrix, 0, projectionMatrix, 0, viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, mvpMatrix, 0, temp, 0);
        if (result == GL10.GL_TRUE) {
            farCoords = new Point3D(
                    temp2[0] / temp2[3],
                    temp2[1] / temp2[3],
                    temp2[2] / temp2[3]);
        }
        this.P0 = farCoords;
    }
}
