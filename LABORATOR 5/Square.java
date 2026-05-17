package com.example.bouncysquaretextures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
    private final int[] textures = new int[1];
    private FloatBuffer mFVertexBuffer;
    private final ByteBuffer mColorBuffer;
    private final ShortBuffer mIndexBuffer;
    public FloatBuffer mTextureBuffer;

    private final float[] normalVertices = {
            -1.0f, -0.70f,
             1.0f, -0.70f,
            -1.0f,  0.70f,
             1.0f,  0.70f
    };

    private final float[] distortedVertices = {
            -1.0f, -0.70f,
             1.0f, -0.30f,
            -1.0f,  0.70f,
             1.0f,  0.30f
    };

    private final float[] fullCoords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    private final float[] croppedCoords = {
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f
    };

    private final float[] repeatedCoords = {
            0.0f, 2.0f,
            2.0f, 2.0f,
            0.0f, 0.0f,
            2.0f, 0.0f
    };

    private final float[] textureCoords = fullCoords.clone();
    private float texIncrease = 0.006f;
    private int mode = 0;

    public Square() {
        setVertexBuffer(normalVertices);

        byte[] colors = {
                (byte)255, (byte)255, (byte)255, (byte)255,
                (byte)255, (byte)255, (byte)255, (byte)255,
                (byte)255, (byte)255, (byte)255, (byte)255,
                (byte)255, (byte)255, (byte)255, (byte)255
        };
        mColorBuffer = ByteBuffer.allocateDirect(colors.length);
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        short[] indices = {0, 1, 2, 1, 3, 2};
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

        setTextureCoords(fullCoords);
    }

    private void setVertexBuffer(float[] vertices) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);
    }

    private void setTextureCoords(float[] coords) {
        System.arraycopy(coords, 0, textureCoords, 0, coords.length);
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }

    private void refreshTextureBuffer() {
        mTextureBuffer.clear();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (mode == 0) {
            setVertexBuffer(normalVertices);
            setTextureCoords(fullCoords);
        } else if (mode == 1) {
            setVertexBuffer(normalVertices);
            setTextureCoords(croppedCoords);
        } else if (mode == 2) {
            setVertexBuffer(normalVertices);
            setTextureCoords(repeatedCoords);
        } else {
            setVertexBuffer(distortedVertices);
            setTextureCoords(fullCoords);
        }
    }

    public void createTexture(GL10 gl, Context contextRegf, int resource) {
        Bitmap image = BitmapFactory.decodeResource(contextRegf.getResources(), resource);
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
        image.recycle();
    }

    public void draw(GL10 gl) {
        if (mode == 3) {
            textureCoords[0] += texIncrease;
            textureCoords[1] += texIncrease;
            textureCoords[2] += texIncrease;
            textureCoords[3] += texIncrease;
            textureCoords[4] += texIncrease;
            textureCoords[5] += texIncrease;
            textureCoords[6] += texIncrease;
            textureCoords[7] += texIncrease;
            refreshTextureBuffer();
        }

        gl.glFrontFace(GL10.GL_CW);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Kept white so the texture appears in original form, not tinted by vertex colors.
        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
