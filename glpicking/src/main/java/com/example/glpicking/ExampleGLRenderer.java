package com.example.glpicking;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

import com.example.glpicking.common.Point2D;
import com.example.glpicking.common.Point3D;

import static com.example.glpicking.common.Color.*;

public class ExampleGLRenderer implements Renderer {

    private List<ExampleGLObject> slides = new ArrayList<>();

    private float ratio;

    private int width;
    private int height;

    private Point2D press = null;

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        //add 4 test objects to the scene
        slides.add(new ExampleGLObject("red", RED, new Point3D(0.0f, 0.0f, 0.5f)));
        slides.add(new ExampleGLObject("yellow", YELLOW, new Point3D(0.5f, 0.5f, 1.0f)));
        slides.add(new ExampleGLObject("green", GREEN, new Point3D(-1.2f, 0.0f, 0.0f)));
        slides.add(new ExampleGLObject("blue", BLUE, new Point3D(0.5f, -1.0f, 0.0f)));
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        Ray ray = null;
        if (press != null) {
            ray = new Ray(gl, width, height, press.x, press.y);
        }

        for (ExampleGLObject slide : slides) {
            slide.draw(gl, ray);
        }
        press = null;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;

        ratio = (float) width / height;

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float left = -ratio;
        float right = ratio;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 3.0f;
        float far = 27.0f;

        gl.glFrustumf(left, right, bottom, top, near, far);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glLoadIdentity();
    }

    public void onPress(int x, int y) {
        press = new Point2D(x, y);
    }
}