package com.example.bouncysquareblending;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends Activity {
    private GLSurfaceView view;
    private SquareRenderer renderer;
    private final String[] modeNames = {
            "Alpha blending: SRC_ALPHA / ONE_MINUS_SRC_ALPHA",
            "Additive blending + glColorMask",
            "Multicolor vertex blending",
            "Texture blending with translucency",
            "Multitexturing: tap with two fingers for combiner"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderer = new SquareRenderer(this.getApplicationContext());
        view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(1);
        view.setRenderer(renderer);
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getPointerCount() > 1) {
                    renderer.nextCombiner();
                    Toast.makeText(this, "Multitexture combiner changed", Toast.LENGTH_SHORT).show();
                } else {
                    int mode = renderer.nextMode();
                    Toast.makeText(this, modeNames[mode], Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        setContentView(view);
        Toast.makeText(this, modeNames[0], Toast.LENGTH_LONG).show();
    }

    @Override protected void onPause() { super.onPause(); view.onPause(); }
    @Override protected void onResume() { super.onResume(); view.onResume(); }
}
