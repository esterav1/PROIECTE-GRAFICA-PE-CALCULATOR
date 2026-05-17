package com.example.bouncysquareblending;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Square {
    private final FloatBuffer vertexBuffer;
    private final ByteBuffer indexBuffer;
    private final FloatBuffer texBuffer;
    private FloatBuffer colorBuffer;
    private int texture0;
    private int texture1;

    private final float[] vertices = {
            -1.0f, -0.70f,
             1.0f, -0.70f,
            -1.0f,  0.70f,
             1.0f,  0.70f
    };
    private final byte[] indices = {0, 1, 2, 1, 3, 2};
    private final float[] textureCoords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    public static final float[] COLORS_YMCA = {
            1.0f, 1.0f, 0.0f, 0.55f,
            0.0f, 1.0f, 1.0f, 0.55f,
            0.0f, 0.0f, 0.0f, 0.55f,
            1.0f, 0.0f, 1.0f, 0.55f
    };

    public static final float[] COLORS_RGBA = {
            1.0f, 0.0f, 0.0f, 0.55f,
            0.0f, 1.0f, 0.0f, 0.55f,
            0.0f, 0.0f, 1.0f, 0.55f,
            1.0f, 1.0f, 1.0f, 0.55f
    };

    public Square() {
        vertexBuffer = makeFloatBuffer(vertices);
        texBuffer = makeFloatBuffer(textureCoords);
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    private FloatBuffer makeFloatBuffer(float[] values) {
        ByteBuffer bb = ByteBuffer.allocateDirect(values.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(values);
        fb.position(0);
        return fb;
    }

    public void setColors(float[] colors) {
        colorBuffer = makeFloatBuffer(colors);
    }

    public void setTextures(GL10 gl, Context context, int resourceID0, int resourceID1) {
        texture0 = createTexture(gl, context, resourceID0);
        texture1 = createTexture(gl, context, resourceID1);
    }

    public int createTexture(GL10 gl, Context context, int resource) {
        int[] textures = new int[1];
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), resource);
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
        image.recycle();
        return textures[0];
    }

    private void prepareGeometry(GL10 gl) {
        gl.glFrontFace(GL10.GL_CW);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void finish(GL10 gl) {
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glFrontFace(GL10.GL_CCW);
    }

    public void drawSolid(GL10 gl) {
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        prepareGeometry(gl);
        finish(gl);
    }

    public void drawColored(GL10 gl) {
        gl.glDisable(GL10.GL_TEXTURE_2D);
        prepareGeometry(gl);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        finish(gl);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }

    public void drawTextured(GL10 gl, int whichTexture) {
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, whichTexture == 0 ? texture0 : texture1);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        prepareGeometry(gl);
        finish(gl);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void drawMultitextured(GL10 gl, int combineMode) {
        gl.glEnable(GL10.GL_TEXTURE_2D);
        prepareGeometry(gl);

        gl.glClientActiveTexture(GL10.GL_TEXTURE0);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture0);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

        gl.glActiveTexture(GL10.GL_TEXTURE1);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture1);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, combineMode);

        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, indexBuffer);

        gl.glActiveTexture(GL10.GL_TEXTURE1);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
