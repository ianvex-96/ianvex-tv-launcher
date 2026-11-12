package com.rampey.materialtv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Full-screen preview of the launcher's screensaver, opened from quick settings. Any remote key returns to Home.
 * The real DreamService still runs on idle/sleep; this gives the Screensaver tile an immediate, visible action.
 */
public class ScreensaverActivity extends Activity {
    private ScreensaverContent content;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        content = new ScreensaverContent(this);
        setContentView(content.view());
    }

    @Override protected void onResume() {
        super.onResume();
        if (content != null) content.start();
    }

    @Override protected void onPause() {
        if (content != null) content.stop();
        super.onPause();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        finish();
        return true;
    }
}
