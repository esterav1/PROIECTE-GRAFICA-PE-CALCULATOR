package com.example.bouncysquaretextures;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SquareRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private final Square mSquare;
    private float y = 0.0f;
    private float speed = 0.018f;
    private int mode = 0;

    public SquareRenderer(Context context) {
        this.context = context;
        this.mSquare = new Square();
    }

    public void nextMode() {
        mode = (mode + 1) % 4;
        mSquare.setMode(mode);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.08f, 0.08f, 0.10f, 1.0f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);
        int resid = R.drawable.hedly;
        mSquare.createTexture(gl, this.context, resid);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / height, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        y += speed;
        if (y > 1.15f || y < -1.15f) speed = -speed;

        gl.glTranslatef(0.0f, y, -4.5f);
        mSquare.draw(gl);
    }
}
