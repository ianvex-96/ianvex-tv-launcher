package com.rampey.materialtv;

import android.content.Context;

import java.util.Locale;

/**
 * Runtime UI language. The launcher is Indonesian by default; English is picked in launcher settings.
 * A third option follows the TV's system language, falling back to Indonesian for anything non-English.
 */
final class L {
    /** Stored in LauncherPrefs. */
    static final int INDONESIAN = 0;
    static final int ENGLISH = 1;
    static final int SYSTEM = 2;

    private L() {
    }

    static boolean english(Context context) {
        int language = LauncherPrefs.language(context);
        if (language == ENGLISH) return true;
        if (language == INDONESIAN) return false;
        String tag = Locale.getDefault().getLanguage();
        return tag != null && tag.toLowerCase(Locale.ROOT).startsWith("en");
    }

    /** Indonesian text by default, English when the person chose it. */
    static String t(Context context, String indonesian, String english) {
        return english(context) ? english : indonesian;
    }
}
