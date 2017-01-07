package com.example.glpicking;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.example.glpicking.common.Point3D;

public class Ray {

    Point3D P0;
    Point3D P1;

    public Ray(GL10 gl, int width, int height, float xTouch, float yTouch) {
        MatrixGrabber matrixGrabber = new MatrixGrabber();
        matrixGrabber.getCurrentState(gl);

        int[] viewport = {0, 0, width, height};

        Point3D nearCoOrds = new Point3D();
        Point3D farCoOrds = new Point3D();
        float[] temp = new float[4];
        float[] temp2 = new float[4];
        // get the near and far ords for the click

        float winX = xTouch;
        float winY = (float) viewport[3] - yTouch;

//        Log.d(TAG, "modelView is =" + Arrays.toString(matrixGrabber.modelView));
//        Log.d(TAG, "projection view is =" + Arrays.toString( matrixGrabber.projection ));

        //Read more here: http://myweb.lmu.edu/dondi/share/cg/unproject-explained.pdf
        int result = GLU.gluUnProject(winX, winY, 1.0f, matrixGrabber.modelView, 0, matrixGrabber.projection, 0, viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, matrixGrabber.modelView, 0, temp, 0);
        if (result == GL10.GL_TRUE) {
            nearCoOrds = new Point3D(
                    temp2[0] / temp2[3],
                    temp2[1] / temp2[3],
                    temp2[2] / temp2[3]);

        }

        result = GLU.gluUnProject(winX, winY, 0, matrixGrabber.modelView, 0, matrixGrabber.projection, 0, viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, matrixGrabber.modelView, 0, temp, 0);
        if (result == GL10.GL_TRUE) {
            farCoOrds = new Point3D(
                    temp2[0] / temp2[3],
                    temp2[1] / temp2[3],
                    temp2[2] / temp2[3]);
        }
        this.P0 = farCoOrds;
        this.P1 = nearCoOrds;
    }
}
