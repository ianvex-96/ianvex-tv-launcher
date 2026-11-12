package com.rampey.materialtv;

import android.service.dreams.DreamService;

/**
 * The launcher's screensaver: clock, date, the drifting shape, colors that glide between Material palettes.
 * The drawing and timing live in ScreensaverContent so the quick-settings preview can use the same view.
 */
public class Screensaver extends DreamService {
    private ScreensaverContent content;

    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        content = new ScreensaverContent(this);
        setContentView(content.view());
    }

    @Override public void onDreamingStarted() {
        super.onDreamingStarted();
        if (content != null) content.start();
    }

    @Override public void onDreamingStopped() {
        if (content != null) content.stop();
        super.onDreamingStopped();
    }
}
