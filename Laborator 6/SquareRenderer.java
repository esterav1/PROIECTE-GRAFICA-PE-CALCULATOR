package com.example.bouncysquareblending;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class SquareRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private final Square squareBlueStyle = new Square();
    private final Square squareRedStyle = new Square();
    private float mTransY = 0.0f;
    private int mode = 0;
    private final int[] combiners = {GL10.GL_MODULATE, GL10.GL_ADD, GL10.GL_BLEND, GL10.GL_DECAL};
    private int combinerIndex = 0;

    public SquareRenderer(Context context) {
        this.context = context;
        squareBlueStyle.setColors(Square.COLORS_YMCA);
        squareRedStyle.setColors(Square.COLORS_RGBA);
    }

    public int nextMode() {
        mode = (mode + 1) % 5;
        return mode;
    }

    public int nextCombiner() {
        combinerIndex = (combinerIndex + 1) % combiners.length;
        mode = 4;
        return combiners[combinerIndex];
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        squareBlueStyle.setTextures(gl, context, R.drawable.hedly, R.drawable.splash);
        squareRedStyle.setTextures(gl, context, R.drawable.goldengate, R.drawable.splash);
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
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL11.GL_MODELVIEW);
        mTransY += 0.075f;

        switch (mode) {
            case 0: drawAlphaBlending(gl); break;
            case 1: drawAdditiveAndColorMask(gl); break;
            case 2: drawMulticolorBlending(gl); break;
            case 3: drawTextureBlending(gl); break;
            default: drawMultitexturing(gl); break;
        }
    }

    private void drawAlphaBlending(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColorMask(true, true, true, true);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float)Math.sin(mTransY), -3.0f);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
        squareBlueStyle.drawSolid(gl);
        gl.glLoadIdentity();
        gl.glTranslatef((float)(Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
        squareRedStyle.drawSolid(gl);
    }

    private void drawAdditiveAndColorMask(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float)Math.sin(mTransY), -3.0f);
        gl.glColorMask(true, true, true, true);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        squareBlueStyle.drawSolid(gl);
        gl.glLoadIdentity();
        gl.glTranslatef((float)(Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        gl.glColorMask(true, false, true, true);
        gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
        squareRedStyle.drawSolid(gl);
        gl.glColorMask(true, true, true, true);
    }

    private void drawMulticolorBlending(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float)Math.sin(mTransY), -3.0f);
        squareBlueStyle.drawColored(gl);
        gl.glLoadIdentity();
        gl.glTranslatef((float)(Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        squareRedStyle.drawColored(gl);
    }

    private void drawTextureBlending(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float)Math.sin(mTransY), -3.0f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        squareBlueStyle.drawTextured(gl, 0);
        gl.glLoadIdentity();
        gl.glTranslatef((float)(Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        squareRedStyle.drawTextured(gl, 0);
    }

    private void drawMultitexturing(GL10 gl) {
        gl.glDisable(GL10.GL_BLEND);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float)Math.sin(mTransY), -3.0f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        squareBlueStyle.drawMultitextured(gl, combiners[combinerIndex]);
    }
}
