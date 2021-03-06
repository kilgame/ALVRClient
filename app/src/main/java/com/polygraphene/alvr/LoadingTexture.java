package com.polygraphene.alvr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

public class LoadingTexture {

    private int mTexture = 0;
    private Canvas mCanvas = null;
    private Bitmap mBitmap = null;
    private Paint mPaint = null;
    private String mCurrentText = "";

    void initializeMessageCanvas(int texture){
        mBitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_4444);

        mCanvas = new Canvas(mBitmap);

        mPaint = new Paint();
        mPaint.setTextSize(80);
        mPaint.setAntiAlias(true);
        mPaint.setARGB(0xff, 0x10, 0x10, 0x10);

        // Create texture for draw error/information messages.
        mTexture = texture;
    }

    void drawMessage(String text) {
        if (text.equals(mCurrentText)) {
            return;
        }
        mCurrentText = text;

        // Draw text on center.
        Rect r = new Rect();

        mBitmap.eraseColor(0x00e0f0f0);

        mCanvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();

        mPaint.setTextAlign(Paint.Align.LEFT);

        float y = cHeight / 2f;
        for(String line : text.split("\n")) {
            mPaint.getTextBounds(text, 0, line.length(), r);

            float x = cWidth / 2f - r.width() / 2f - r.left;
            mCanvas.drawText(line, x, y + r.height() / 2f - r.bottom, mPaint);

            y += r.height() * 1.5;
        }

        // Note that gl context has created on vrAPI.initialize.
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);

        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
    }

    void destroyTexture() {
        int[] textures = new int [1];
        textures[0] = mTexture;
        GLES20.glDeleteTextures(1, textures, 0);
    }

}
