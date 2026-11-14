package com.rampey.materialtv;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Launcher settings: greeting, theme, screensaver style and hidden apps. Opened from the quick-settings
 * "Launcher" tile. Every change is saved immediately; Home re-reads the prefs when it comes back.
 */
public class LauncherSettingsActivity extends Activity {
    private static final int[] SEEDS = {
            0xFF6750A4, 0xFFB3261E, 0xFF386A20, 0xFF0061A4,
            0xFF8B5000, 0xFF8E4585, 0xFF006A6A, 0xFF6D5E00};
    private static final String[] SEED_NAMES_ID = {
            "Ungu", "Merah", "Hijau", "Biru", "Oranye", "Merah muda", "Teal", "Zaitun"};
    private static final String[] SEED_NAMES_EN = {
            "Purple", "Red", "Green", "Blue", "Orange", "Pink", "Teal", "Olive"};
    private static final String[] SAVER_NAMES_ID = {"Bentuk + jam", "Jam saja", "Gelap"};
    private static final String[] SAVER_NAMES_EN = {"Shapes + clock", "Clock only", "Dark"};

    private Palette palette;
    private PackageManager pm;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        palette = Palette.of(LauncherPrefs.accentSeed(this));
        pm = getPackageManager();
        setContentView(build());
    }

    private View build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(48), dp(24), dp(48), dp(18));
        root.setBackgroundColor(palette.surface);

        TextView title = text(L.t(this, "Pengaturan launcher", "Launcher settings"),
                30, palette.onSurface, 760);
        title.setIncludeFontPadding(false);
        root.addView(title, match());
        TextView subtitle = text(L.t(this,
                        "Sapaan, warna, screensaver dan aplikasi tersembunyi.",
                        "Greeting, color, screensaver and hidden apps."), 13,
                palette.onSurfaceVariant, 500);
        subtitle.setPadding(0, dp(3), 0, dp(10));
        root.addView(subtitle, match());

        ScrollView scroll = new ScrollView(this);
        scroll.setFocusable(false);
        scroll.setClipToPadding(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 0, 0, dp(16));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        buildLanguage(content);
        buildGreeting(content);
        buildTheme(content);
        buildQuickStyle(content);
        buildScreensaver(content);
        buildHidden(content);
        buildAbout(content);

        TextView back = footerButton(L.t(this, "Kembali", "Back"), true);
        back.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams backLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        backLp.topMargin = dp(10);
        root.addView(back, backLp);
        return root;
    }

    // ---- language ----

    private void buildLanguage(LinearLayout parent) {
        section(parent, L.t(this, "Bahasa", "Language"));
        TextView hint = text(L.t(this,
                        "Pilih bahasa tampilan launcher. Bahasa Indonesia tetap menjadi bawaan.",
                        "Choose the launcher display language. Indonesian stays the default."),
                12, palette.onSurfaceVariant, 500);
        hint.setPadding(dp(2), 0, 0, dp(8));
        parent.addView(hint, match());

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int current = LauncherPrefs.language(this);
        String[] labels = {
                L.t(this, "Ikuti sistem", "Follow system"),
                "Indonesia",
                "English"};
        int[] values = {L.SYSTEM, L.INDONESIAN, L.ENGLISH};
        for (int i = 0; i < labels.length; i++) {
            final int value = values[i];
            TextView chip = chip(labels[i], value == current, () -> {
                LauncherPrefs.setLanguage(this, value);
                recreate();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.setMarginStart(dp(8));
            row.addView(chip, lp);
        }
        parent.addView(row, match());
    }

    // ---- greeting ----

    private void buildGreeting(LinearLayout parent) {
        section(parent, L.t(this, "Sapaan", "Greeting"));

        EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setText(LauncherPrefs.greetingName(this));
        name.setHint(L.t(this, "Nama sapaan", "Greeting name"));
        name.setHintTextColor(withAlpha(palette.onSurfaceVariant, 0xAA));
        name.setTextColor(palette.onSurface);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        name.setPadding(dp(14), dp(8), dp(14), dp(8));
        name.setBackground(fieldBackground(false));
        name.setFocusable(true);
        name.setFocusableInTouchMode(true);
        name.setOnFocusChangeListener((v, focused) -> name.setBackground(fieldBackground(focused)));
        name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override public void afterTextChanged(Editable s) {
                LauncherPrefs.setGreetingName(LauncherSettingsActivity.this, s.toString());
            }
        });
        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameLp.bottomMargin = dp(7);
        parent.addView(name, nameLp);

        boolean on = LauncherPrefs.greetingOn(this);
        View toggle = toggleRow(L.t(this, "Tampilkan sapaan", "Show greeting"),
                on ? L.t(this, "Aktif", "On") : L.t(this, "Nonaktif", "Off"), on, () -> {
            LauncherPrefs.setGreetingOn(this, !LauncherPrefs.greetingOn(this));
            recreate();
        });
        parent.addView(toggle, match());

        TextView reset = chip(L.t(this, "Pakai nama bawaan", "Use default name"), false, () -> {
            LauncherPrefs.setGreetingName(this, LauncherPrefs.DEFAULT_GREETING_NAME);
            recreate();
        });
        LinearLayout.LayoutParams resetLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        resetLp.topMargin = dp(7);
        parent.addView(reset, resetLp);
    }

    // ---- theme ----

    private void buildTheme(LinearLayout parent) {
        section(parent, L.t(this, "Warna", "Color"));
        TextView hint = text(L.t(this,
                        "Pilih warna aksen, atau ikuti warna aplikasi yang sedang disorot.",
                        "Pick an accent color, or follow the color of the focused app."), 12,
                palette.onSurfaceVariant, 500);
        hint.setPadding(dp(2), 0, 0, dp(8));
        parent.addView(hint, match());

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int current = LauncherPrefs.accentSeed(this);
        String[] seedNames = L.english(this) ? SEED_NAMES_EN : SEED_NAMES_ID;
        for (int i = 0; i < SEEDS.length; i++) {
            final int seed = SEEDS[i];
            boolean selected = seed == current;
            TextView dot = new TextView(this);
            dot.setText(selected ? "✓" : "");
            dot.setTextColor(0xFF000000);
            dot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            dot.setGravity(Gravity.CENTER);
            dot.setContentDescription(seedNames[i]);
            dot.setFocusable(true);
            dot.setFocusableInTouchMode(false);
            dot.setClickable(true);
            dot.setDefaultFocusHighlightEnabled(false);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(seed);
            bg.setStroke(selected ? dp(3) : dp(1), selected ? palette.onSurface : withAlpha(palette.onSurface, 0x44));
            dot.setBackground(bg);
            dot.setOnFocusChangeListener((v, focused) -> {
                bg.setStroke(focused ? dp(3) : selected ? dp(3) : dp(1),
                        focused ? palette.primary : selected ? palette.onSurface : withAlpha(palette.onSurface, 0x44));
                dot.animate().scaleX(focused ? 1.12f : 1f).scaleY(focused ? 1.12f : 1f)
                        .setDuration(Motion.FAST_SPATIAL_MS).start();
            });
            dot.setOnClickListener(v -> {
                LauncherPrefs.setAccentSeed(this, seed);
                LauncherPrefs.setDynamicColor(this, false);
                Toast.makeText(this, L.t(this, "Warna ", "Color ")
                        + seedNames[indexOf(seed)]
                        + L.t(this, " diterapkan", " applied"), Toast.LENGTH_SHORT).show();
                recreate();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(46), dp(46));
            if (i > 0) lp.setMarginStart(dp(9));
            row.addView(dot, lp);
        }
        parent.addView(row, match());

        boolean dynamic = LauncherPrefs.dynamicColor(this);
        View toggle = toggleRow(L.t(this, "Ikuti warna aplikasi", "Follow app color"),
                dynamic ? L.t(this, "Aktif", "On") : L.t(this, "Nonaktif", "Off"), dynamic, () -> {
            LauncherPrefs.setDynamicColor(this, !LauncherPrefs.dynamicColor(this));
            recreate();
        });
        LinearLayout.LayoutParams toggleLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toggleLp.topMargin = dp(9);
        parent.addView(toggle, toggleLp);
    }

    private static int indexOf(int seed) {
        for (int i = 0; i < SEEDS.length; i++) if (SEEDS[i] == seed) return i;
        return 0;
    }

    // ---- quick settings style ----

    private void buildQuickStyle(LinearLayout parent) {
        section(parent, L.t(this, "Quick settings", "Quick settings"));
        TextView hint = text(L.t(this,
                        "Pilih gaya panel quick settings. Android 17 tampil di tengah; One UI tampil ringkas di pojok kanan atas.",
                        "Choose the quick settings panel style. Android 17 sits in the center; One UI is a compact card in the top-right corner."),
                12, palette.onSurfaceVariant, 500);
        hint.setPadding(dp(2), 0, 0, dp(8));
        parent.addView(hint, match());

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int current = LauncherPrefs.quickStyle(this);
        String[] labels = {
                L.t(this, "Android 17", "Android 17"),
                L.t(this, "One UI", "One UI")};
        int[] values = {LauncherPrefs.QUICK_ANDROID17, LauncherPrefs.QUICK_ONE_UI};
        for (int i = 0; i < labels.length; i++) {
            final int value = values[i];
            TextView chip = chip(labels[i], value == current, () -> {
                LauncherPrefs.setQuickStyle(this, value);
                recreate();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.setMarginStart(dp(8));
            row.addView(chip, lp);
        }
        parent.addView(row, match());
    }

    // ---- screensaver ----

    private void buildScreensaver(LinearLayout parent) {
        section(parent, "Screensaver");
        int style = LauncherPrefs.screensaverStyle(this);
        String[] saverNames = L.english(this) ? SAVER_NAMES_EN : SAVER_NAMES_ID;
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        for (int i = 0; i < saverNames.length; i++) {
            final int value = i;
            TextView chip = chip(saverNames[i], i == style, () -> {
                LauncherPrefs.setScreensaverStyle(this, value);
                recreate();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.setMarginStart(dp(8));
            row.addView(chip, lp);
        }
        parent.addView(row, match());

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER_VERTICAL);
        TextView preview = chip(L.t(this, "Pratinjau", "Preview"), false, () -> {
            try {
                startActivity(new Intent(this, ScreensaverActivity.class));
            } catch (RuntimeException ignored) {
            }
        });
        TextView idle = chip(L.t(this, "Waktu tidur layar", "Screen timeout"), false, () -> {
            try {
                startActivity(new Intent(Settings.ACTION_DREAM_SETTINGS));
            } catch (ActivityNotFoundException unavailable) {
                try {
                    startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
                } catch (ActivityNotFoundException ignored) {
                }
            }
        });
        buttons.addView(preview, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        LinearLayout.LayoutParams idleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        idleLp.setMarginStart(dp(8));
        buttons.addView(idle, idleLp);
        LinearLayout.LayoutParams buttonsLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonsLp.topMargin = dp(8);
        parent.addView(buttons, buttonsLp);
    }

    // ---- hidden apps ----

    private void buildHidden(LinearLayout parent) {
        section(parent, L.t(this, "Aplikasi disembunyikan", "Hidden apps"));
        List<String> hidden = LauncherPrefs.hiddenApps(this);
        if (hidden.isEmpty()) {
            TextView empty = text(L.t(this,
                            "Tidak ada aplikasi disembunyikan. Tahan OK pada ubin untuk menyembunyikan.",
                            "No hidden apps. Hold OK on a tile to hide it."),
                    12, palette.onSurfaceVariant, 500);
            empty.setPadding(dp(2), 0, 0, dp(2));
            parent.addView(empty, match());
            return;
        }
        for (String pkg : hidden) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(7), dp(10), dp(7));
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(14));
            bg.setColor(palette.container);
            bg.setStroke(dp(1), withAlpha(palette.onSurfaceVariant, 0x22));
            row.setBackground(bg);

            TextView label = text(appLabel(pkg), 15, palette.onSurface, 550);
            label.setSingleLine(true);
            label.setEllipsize(TextUtils.TruncateAt.END);
            row.addView(label, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView show = chip(L.t(this, "Tampilkan", "Show"), false, () -> {
                LauncherPrefs.showApp(this, pkg);
                recreate();
            });
            row.addView(show, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(6);
            parent.addView(row, lp);
        }
        TextView all = chip(L.t(this, "Tampilkan semua", "Show all"), false, () -> {
            LauncherPrefs.clearHiddenApps(this);
            recreate();
        });
        LinearLayout.LayoutParams allLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        allLp.topMargin = dp(3);
        parent.addView(all, allLp);
    }

    private CharSequence appLabel(String pkg) {
        try {
            return pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0));
        } catch (PackageManager.NameNotFoundException e) {
            return pkg;
        }
    }

    private void buildAbout(LinearLayout parent) {
        String version = "";
        try {
            version = pm.getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        TextView about = text("ianvex tv launcher " + version, 11, palette.onSurfaceVariant, 500);
        about.setPadding(dp(2), dp(14), 0, 0);
        parent.addView(about, match());
    }

    // ---- pieces ----

    private void section(LinearLayout parent, String title) {
        TextView label = text(title, 15, palette.primary, 700);
        label.setPadding(dp(2), dp(14), 0, dp(7));
        parent.addView(label, match());
    }

    private View toggleRow(String title, String status, boolean on, Runnable action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(10), dp(14), dp(10));
        row.setFocusable(true);
        row.setFocusableInTouchMode(false);
        row.setClickable(true);
        row.setDefaultFocusHighlightEnabled(false);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(14));
        row.setBackground(bg);

        TextView label = text(title, 15, palette.onSurface, 600);
        row.addView(label, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView value = text(status, 13, on ? palette.primary : palette.onSurfaceVariant, 700);
        row.addView(value, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Runnable paint = () -> {
            boolean focused = row.hasFocus();
            bg.setColor(focused ? blend(palette.containerHigh, palette.primary, 0.18f)
                    : on ? palette.containerHigh : palette.container);
            bg.setStroke(focused ? dp(2) : dp(1),
                    focused ? palette.primary : on ? withAlpha(palette.primary, 0x66)
                            : withAlpha(palette.onSurfaceVariant, 0x22));
            value.setTextColor(on ? palette.primary : palette.onSurfaceVariant);
        };
        row.setOnFocusChangeListener((v, focused) -> paint.run());
        row.setOnClickListener(v -> action.run());
        paint.run();
        return row;
    }

    private TextView chip(String label, boolean selected, Runnable action) {
        TextView b = text(label, 13, selected ? palette.onPrimary : palette.onSurface, 650);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(14), dp(10), dp(14), dp(10));
        b.setSingleLine(true);
        b.setEllipsize(TextUtils.TruncateAt.END);
        b.setFocusable(true);
        b.setFocusableInTouchMode(false);
        b.setClickable(true);
        b.setDefaultFocusHighlightEnabled(false);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(18));
        bg.setColor(selected ? palette.primary : palette.container);
        bg.setStroke(selected ? dp(2) : dp(1),
                selected ? palette.onPrimary : withAlpha(palette.onSurfaceVariant, 0x22));
        b.setBackground(bg);
        b.setOnFocusChangeListener((v, focused) -> {
            bg.setColor(focused ? blend(palette.containerHigh, palette.primary, 0.20f)
                    : selected ? palette.primary : palette.container);
            bg.setStroke(focused ? dp(2) : selected ? dp(2) : dp(1),
                    focused ? palette.primary : selected ? palette.onPrimary
                            : withAlpha(palette.onSurfaceVariant, 0x22));
            b.setTextColor(focused ? palette.onSurface : selected ? palette.onPrimary : palette.onSurface);
        });
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private TextView footerButton(String label, boolean filled) {
        TextView b = text(label, 15, filled ? palette.onPrimary : palette.onSurface, 650);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(18), dp(11), dp(18), dp(11));
        b.setFocusable(true);
        b.setFocusableInTouchMode(false);
        b.setClickable(true);
        b.setDefaultFocusHighlightEnabled(false);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(20));
        bg.setColor(filled ? palette.primary : palette.container);
        if (!filled) bg.setStroke(dp(1), withAlpha(palette.onSurfaceVariant, 0x22));
        b.setBackground(bg);
        b.setOnFocusChangeListener((v, focused) -> {
            if (filled) {
                bg.setColor(focused ? blend(palette.primary, palette.onPrimary, 0.14f) : palette.primary);
                bg.setStroke(focused ? dp(2) : 0, palette.onPrimary);
            } else {
                bg.setColor(focused ? blend(palette.containerHigh, palette.primary, 0.18f) : palette.container);
                bg.setStroke(focused ? dp(2) : dp(1),
                        focused ? palette.primary : withAlpha(palette.onSurfaceVariant, 0x22));
            }
        });
        return b;
    }

    private GradientDrawable fieldBackground(boolean focused) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(14));
        bg.setColor(palette.container);
        bg.setStroke(focused ? dp(2) : dp(1),
                focused ? palette.primary : withAlpha(palette.onSurfaceVariant, 0x33));
        return bg;
    }

    private TextView text(CharSequence value, float sp, int color, int weight) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        t.setTextColor(color);
        try {
            t.setTypeface(new Typeface.Builder(getAssets(), "fonts/GoogleSansFlex.ttf")
                    .setFontVariationSettings("'wght' " + weight + ", 'opsz' 24, 'ROND' 0").build());
        } catch (RuntimeException ignored) {
        }
        return t;
    }

    private static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private static int blend(int base, int overlay, float amount) {
        float keep = 1f - amount;
        int r = Math.round(Color.red(base) * keep + Color.red(overlay) * amount);
        int g = Math.round(Color.green(base) * keep + Color.green(overlay) * amount);
        int b = Math.round(Color.blue(base) * keep + Color.blue(overlay) * amount);
        return Color.argb(255, r, g, b);
    }

    private LinearLayout.LayoutParams match() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(float value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics()));
    }
}
