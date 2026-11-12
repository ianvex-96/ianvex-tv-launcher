package com.rampey.materialtv;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Small shared preference store for launcher settings that survive restarts. */
final class LauncherPrefs {
    private static final String NAME = "launcher";
    private static final String DOCK_APPS = "dock_apps";
    private static final String GREETING_NAME = "greeting_name";
    private static final String GREETING_ON = "greeting_on";
    private static final String ACCENT_SEED = "accent_seed";
    private static final String DYNAMIC_COLOR = "dynamic_color";
    private static final String SCREENSAVER_STYLE = "screensaver_style";
    private static final String HIDDEN_APPS = "hidden_apps";
    private static final String SCREEN_OFF_RESTORE = "screen_off_restore";

    static final String DEFAULT_GREETING_NAME = "Nafil Julio Firmansyah";

    private LauncherPrefs() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /** Null means automatic (most-used apps); an empty string means the user picked nothing. */
    static String getDockApps(Context context) {
        return prefs(context).getString(DOCK_APPS, null);
    }

    static void setDockApps(Context context, List<String> packages) {
        prefs(context).edit().putString(DOCK_APPS, TextUtils.join(",", packages)).apply();
    }

    static void clearDockApps(Context context) {
        prefs(context).edit().remove(DOCK_APPS).apply();
    }

    static String greetingName(Context context) {
        return prefs(context).getString(GREETING_NAME, DEFAULT_GREETING_NAME);
    }

    static void setGreetingName(Context context, String name) {
        prefs(context).edit().putString(GREETING_NAME, name == null ? "" : name.trim()).apply();
    }

    static boolean greetingOn(Context context) {
        return prefs(context).getBoolean(GREETING_ON, true);
    }

    static void setGreetingOn(Context context, boolean on) {
        prefs(context).edit().putBoolean(GREETING_ON, on).apply();
    }

    static int accentSeed(Context context) {
        return prefs(context).getInt(ACCENT_SEED, Palette.DEFAULT_SEED);
    }

    static void setAccentSeed(Context context, int seed) {
        prefs(context).edit().putInt(ACCENT_SEED, seed).apply();
    }

    static boolean dynamicColor(Context context) {
        return prefs(context).getBoolean(DYNAMIC_COLOR, true);
    }

    static void setDynamicColor(Context context, boolean on) {
        prefs(context).edit().putBoolean(DYNAMIC_COLOR, on).apply();
    }

    /** 0 = shapes + clock, 1 = clock only, 2 = dark clock. */
    static int screensaverStyle(Context context) {
        return prefs(context).getInt(SCREENSAVER_STYLE, 0);
    }

    static void setScreensaverStyle(Context context, int style) {
        prefs(context).edit().putInt(SCREENSAVER_STYLE, Math.max(0, Math.min(2, style))).apply();
    }

    static boolean isHidden(Context context, String pkg) {
        return hiddenSet(context).contains(pkg);
    }

    static List<String> hiddenApps(Context context) {
        return new ArrayList<>(hiddenSet(context));
    }

    static void hideApp(Context context, String pkg) {
        Set<String> hidden = hiddenSet(context);
        if (hidden.add(pkg)) {
            prefs(context).edit().putString(HIDDEN_APPS, TextUtils.join(",", hidden)).apply();
        }
    }

    static void showApp(Context context, String pkg) {
        Set<String> hidden = hiddenSet(context);
        if (hidden.remove(pkg)) {
            prefs(context).edit().putString(HIDDEN_APPS, TextUtils.join(",", hidden)).apply();
        }
    }

    static void clearHiddenApps(Context context) {
        prefs(context).edit().remove(HIDDEN_APPS).apply();
    }

    /** -1 when no screen-off timeout is waiting to be restored. */
    static int screenOffRestore(Context context) {
        return prefs(context).getInt(SCREEN_OFF_RESTORE, -1);
    }

    static void setScreenOffRestore(Context context, int timeout) {
        prefs(context).edit().putInt(SCREEN_OFF_RESTORE, timeout).apply();
    }

    static void clearScreenOffRestore(Context context) {
        prefs(context).edit().remove(SCREEN_OFF_RESTORE).apply();
    }

    private static Set<String> hiddenSet(Context context) {
        Set<String> out = new LinkedHashSet<>();
        String saved = prefs(context).getString(HIDDEN_APPS, "");
        if (saved == null || saved.isEmpty()) return out;
        for (String pkg : saved.split(",")) {
            if (!pkg.isEmpty()) out.add(pkg);
        }
        return out;
    }
}
