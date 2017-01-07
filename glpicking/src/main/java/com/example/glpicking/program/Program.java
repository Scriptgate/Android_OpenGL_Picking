package com.example.glpicking.program;

import java.util.List;

import static android.opengl.GLES20.*;
import static com.example.glpicking.program.AttributeVariable.toStringArray;
import static com.example.glpicking.program.RawResourceReader.readShaderFileFromResource;
import static com.example.glpicking.program.ShaderHelper.compileShader;
import static com.example.glpicking.program.ShaderHelper.createAndLinkProgram;

public class Program {

    private int handle;

    private Program(int programHandle) {
        this.handle = programHandle;
    }

    public static Program createProgram(String vertexShaderResource, String fragmentShaderResource, List<AttributeVariable> attributes) {

        int vertexShaderHandle = loadShader(GL_VERTEX_SHADER, vertexShaderResource);
        int fragmentShaderHandle = loadShader(GL_FRAGMENT_SHADER, fragmentShaderResource);

        // Create a program object and store the handle to it.
        int programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, toStringArray(attributes));
        return new Program(programHandle);
    }

    public int getHandle(AttributeVariable attribute) {
        return glGetAttribLocation(handle, attribute.getName());
    }

    public int getHandle(UniformVariable uniform) {
        return glGetUniformLocation(handle, uniform.getName());
    }

    public void useForRendering() {
        glUseProgram(handle);
    }

    public static int loadShader(int shaderType, String resource) {
        final String shaderSource = readShaderFileFromResource(resource);
        return compileShader(shaderType, shaderSource);
    }
}
