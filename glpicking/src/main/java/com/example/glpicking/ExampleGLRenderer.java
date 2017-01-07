package com.example.glpicking;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.example.glpicking.common.Point2D;
import com.example.glpicking.common.Point3D;
import com.example.glpicking.program.Program;

import static com.example.glpicking.common.Color.*;
import static android.opengl.GLES20.*;
import static com.example.glpicking.program.AttributeVariable.COLOR;
import static com.example.glpicking.program.AttributeVariable.POSITION;
import static com.example.glpicking.program.Program.createProgram;
import static java.util.Arrays.asList;

public class ExampleGLRenderer implements Renderer {

    private List<ExampleGLObject> slides = new ArrayList<>();

    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private Program program;

    private int width;
    private int height;

    private Point2D press = null;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        Point3D eye = new Point3D(0.0f, 0.0f, 5f);
        Point3D look = new Point3D(0.0f, 0.0f, 0.0f);
        Point3D up = new Point3D(0.0f, 1.0f, 0.0f);
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

        program = createProgram("color_vertex_shader", "color_fragment_shader", asList(POSITION, COLOR));

        //add 4 test objects to the scene
        slides.add(new ExampleGLObject(RED, new Point3D(0.0f, 0.0f, 0.5f)));
        slides.add(new ExampleGLObject(YELLOW, new Point3D(0.5f, 0.5f, 1.0f)));
        slides.add(new ExampleGLObject(GREEN, new Point3D(-1.2f, 0.0f, 0.0f)));
        slides.add(new ExampleGLObject(BLUE, new Point3D(0.5f, -1.0f, 0.0f)));
    }

    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        program.useForRendering();
        Ray ray = null;
        if (press != null) {
            ray = new Ray(modelMatrix, viewMatrix, projectionMatrix, mvpMatrix, width, height, press.x, press.y);
        }

        for (ExampleGLObject slide : slides) {
            slide.draw(ray, program, modelMatrix, viewMatrix, projectionMatrix, mvpMatrix);
        }
        press = null;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;

        float ratio = (float) width / height;

        float left = -ratio;
        float right = ratio;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 3.0f;
        float far = 27.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void onPress(int x, int y) {
        press = new Point2D(x, y);
    }
}