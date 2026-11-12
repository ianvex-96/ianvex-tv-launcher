package com.rampey.materialtv;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Pick the apps that appear in the home dock: search, choose, and put them in order. Opened from quick settings. */
public class DockAppsActivity extends Activity {
    private static final int MAX_APPS = 6;

    private final List<String> selected = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();
    private final List<TextView> chipViews = new ArrayList<>();
    private PackageManager pm;
    private Palette palette;
    private TextView count;
    private TextView saveButton;
    private EditText search;
    private LinearLayout list;
    private LinearLayout chips;
    private HorizontalScrollView chipScroller;
    private TextView chipHint;

    private static final class Row {
        LinearLayout view;
        TextView name;
        TextView check;
        String pkg;
        String label;
        GradientDrawable bg;
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        pm = getPackageManager();
        palette = Palette.of(getIntent().getIntExtra("seed", Palette.DEFAULT_SEED));
        readInitialSelection();
        setContentView(build());
        filter(search == null ? "" : search.getText().toString());
        if (search != null) search.requestFocus();
    }

    private void readInitialSelection() {
        String saved = LauncherPrefs.getDockApps(this);
        if (saved != null) {
            addSaved(saved.split(","));
            return;
        }
        String[] current = getIntent().getStringArrayExtra("current");
        if (current != null) addSaved(current);
    }

    private void addSaved(String[] packages) {
        for (String pkg : packages) {
            if (selected.size() >= MAX_APPS) break;
            if (pkg == null || pkg.isEmpty() || LauncherPrefs.isHidden(this, pkg)) continue;
            if (pm.getLaunchIntentForPackage(pkg) != null && !selected.contains(pkg)) selected.add(pkg);
        }
    }

    private View build() {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(48), dp(24), dp(48), dp(18));
        column.setBackgroundColor(palette.surface);

        TextView title = text("Dock apps", 30, palette.onSurface, 760);
        title.setIncludeFontPadding(false);
        column.addView(title, match());

        TextView hint = text("Pilih maksimal 6 aplikasi. Urutkan dengan tombol kiri/kanan.", 13, palette.onSurfaceVariant, 500);
        hint.setPadding(0, dp(3), 0, dp(8));
        column.addView(hint, match());

        search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Cari aplikasi");
        search.setHintTextColor(withAlpha(palette.onSurfaceVariant, 0xAA));
        search.setTextColor(palette.onSurface);
        search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        search.setInputType(InputType.TYPE_CLASS_TEXT);
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        search.setPadding(dp(14), dp(8), dp(14), dp(8));
        search.setBackground(fieldBackground(false));
        search.setFocusable(true);
        search.setFocusableInTouchMode(true);
        search.setOnFocusChangeListener((v, focused) -> search.setBackground(fieldBackground(focused)));
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        searchLp.bottomMargin = dp(8);
        column.addView(search, searchLp);

        chipHint = text("", 12, palette.primary, 650);
        chipHint.setPadding(dp(2), 0, 0, dp(4));
        column.addView(chipHint, match());

        chipScroller = new HorizontalScrollView(this);
        chipScroller.setHorizontalScrollBarEnabled(false);
        chipScroller.setFocusable(false);
        chipScroller.setClipToPadding(false);
        chipScroller.setClipChildren(false);
        chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        chips.setPadding(0, dp(2), 0, dp(4));
        chipScroller.addView(chips);
        LinearLayout.LayoutParams chipLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chipLp.bottomMargin = dp(4);
        column.addView(chipScroller, chipLp);

        count = text("", 12, palette.primary, 650);
        count.setPadding(dp(2), 0, 0, dp(6));
        column.addView(count, match());
        updateCount();

        ScrollView scroll = new ScrollView(this);
        scroll.setFocusable(false);
        scroll.setClipToPadding(false);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, dp(2), 0, dp(8));
        scroll.addView(list, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        column.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        List<ResolveInfo> apps = launchableApps();
        for (ResolveInfo info : apps) addRow(info);

        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.CENTER_VERTICAL);
        footer.setPadding(0, dp(10), 0, 0);
        TextView automatic = footerButton("Otomatis", false);
        automatic.setOnClickListener(v -> {
            LauncherPrefs.clearDockApps(this);
            Toast.makeText(this, "Dock memakai urutan otomatis", Toast.LENGTH_SHORT).show();
            finish();
        });
        saveButton = footerButton("Simpan", true);
        saveButton.setOnClickListener(v -> {
            LauncherPrefs.setDockApps(this, selected);
            Toast.makeText(this, "Dock disimpan", Toast.LENGTH_SHORT).show();
            finish();
        });
        footer.addView(automatic, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        View gap = new View(this);
        footer.addView(gap, new LinearLayout.LayoutParams(dp(10), dp(1)));
        footer.addView(saveButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        column.addView(footer, match());

        rebuildChips();
        return column;
    }

    private GradientDrawable fieldBackground(boolean focused) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(14));
        bg.setColor(palette.container);
        bg.setStroke(focused ? dp(2) : dp(1),
                focused ? palette.primary : withAlpha(palette.onSurfaceVariant, 0x33));
        return bg;
    }

    private void addRow(ResolveInfo info) {
        String pkg = info.activityInfo.packageName;
        String label = String.valueOf(info.loadLabel(pm));

        Row row = new Row();
        row.pkg = pkg;
        row.label = label;
        row.view = new LinearLayout(this);
        row.view.setOrientation(LinearLayout.HORIZONTAL);
        row.view.setGravity(Gravity.CENTER_VERTICAL);
        row.view.setPadding(dp(14), dp(9), dp(14), dp(9));
        row.view.setFocusable(true);
        // A tap should click, not only take focus: one tap selects the app.
        row.view.setFocusableInTouchMode(false);
        row.view.setClickable(true);
        row.view.setDefaultFocusHighlightEnabled(false);
        row.bg = new GradientDrawable();
        row.bg.setCornerRadius(dp(14));
        row.view.setBackground(row.bg);

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(info.loadIcon(pm));
        row.view.addView(icon, new LinearLayout.LayoutParams(dp(28), dp(28)));

        row.name = text(label, 16, palette.onSurface, 550);
        row.name.setSingleLine(true);
        row.name.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        nameLp.setMarginStart(dp(14));
        row.view.addView(row.name, nameLp);

        row.check = text("✓", 18, palette.primary, 800);
        row.check.setPadding(dp(12), 0, 0, 0);
        row.view.addView(row.check, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        row.view.setOnClickListener(v -> toggle(row));
        row.view.setOnFocusChangeListener((v, focused) -> paint(row));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(6);
        list.addView(row.view, lp);
        rows.add(row);
        paint(row);
    }

    private void toggle(Row row) {
        if (selected.contains(row.pkg)) {
            selected.remove(row.pkg);
        } else {
            if (selected.size() >= MAX_APPS) {
                Toast.makeText(this, "Maksimal 6 aplikasi", Toast.LENGTH_SHORT).show();
                return;
            }
            selected.add(row.pkg);
        }
        updateCount();
        paint(row);
        rebuildChips();
    }

    private void rebuildChips() {
        chips.removeAllViews();
        chipViews.clear();
        if (selected.isEmpty()) {
            chipHint.setText("Belum ada aplikasi dipilih.");
            chipScroller.setVisibility(View.GONE);
            return;
        }
        chipScroller.setVisibility(View.VISIBLE);
        chipHint.setText("Urutan dock (◀ ▶ untuk menggeser)");
        for (int i = 0; i < selected.size(); i++) {
            final int index = i;
            String pkg = selected.get(i);
            String label = labelFor(pkg);
            TextView chip = text((i + 1) + "  " + label, 13, palette.onSurface, 650);
            chip.setSingleLine(true);
            chip.setEllipsize(TextUtils.TruncateAt.END);
            chip.setMaxWidth(dp(190));
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(14), dp(8), dp(14), dp(8));
            chip.setFocusable(true);
            chip.setFocusableInTouchMode(false);
            chip.setClickable(true);
            chip.setDefaultFocusHighlightEnabled(false);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(18));
            bg.setColor(palette.containerHigh);
            bg.setStroke(dp(1), withAlpha(palette.primary, 0x66));
            chip.setBackground(bg);
            chip.setOnFocusChangeListener((v, focused) -> {
                bg.setColor(focused ? blend(palette.containerHigh, palette.primary, 0.22f) : palette.containerHigh);
                bg.setStroke(focused ? dp(2) : dp(1),
                        focused ? palette.primary : withAlpha(palette.primary, 0x66));
                chip.setTextColor(focused ? palette.onSurface : palette.onSurfaceVariant);
                if (focused) scrollChipIntoView(chip);
            });
            chip.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    moveChip(index, -1);
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    moveChip(index, 1);
                    return true;
                }
                return false;
            });
            // A tap on a chip removes it from the dock (the list row stays ticked until it is tapped too).
            chip.setOnClickListener(v -> {
                selected.remove(pkg);
                updateCount();
                rebuildChips();
                for (Row row : rows) if (row.pkg.equals(pkg)) paint(row);
                if (!chipViews.isEmpty()) chipViews.get(Math.min(index, chipViews.size() - 1)).requestFocus();
                else if (search != null) search.requestFocus();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) lp.setMarginStart(dp(7));
            chips.addView(chip, lp);
            chipViews.add(chip);
        }
    }

    private void moveChip(int index, int delta) {
        int to = index + delta;
        if (to < 0 || to >= selected.size()) return;
        Collections.swap(selected, index, to);
        rebuildChips();
        if (to < chipViews.size()) chipViews.get(to).requestFocus();
    }

    private void scrollChipIntoView(View chip) {
        if (chipScroller == null) return;
        chipScroller.post(() -> {
            int left = chip.getLeft() - dp(8);
            int right = chip.getRight() + dp(8) - chipScroller.getWidth();
            if (left < chipScroller.getScrollX()) chipScroller.smoothScrollTo(Math.max(0, left), 0);
            else if (right > chipScroller.getScrollX()) chipScroller.smoothScrollTo(Math.max(0, right), 0);
        });
    }

    private String labelFor(String pkg) {
        for (Row row : rows) if (row.pkg.equals(pkg)) return row.label;
        try {
            return String.valueOf(pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)));
        } catch (PackageManager.NameNotFoundException e) {
            return pkg;
        }
    }

    private void filter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        View firstVisible = null;
        for (Row row : rows) {
            boolean visible = q.isEmpty() || row.label.toLowerCase(Locale.ROOT).contains(q)
                    || row.pkg.toLowerCase(Locale.ROOT).contains(q);
            row.view.setVisibility(visible ? View.VISIBLE : View.GONE);
            if (visible && firstVisible == null) firstVisible = row.view;
        }
        if (q.isEmpty()) return;
        View focus = getCurrentFocus();
        if (focus != null && focus.getVisibility() != View.VISIBLE && firstVisible != null) {
            firstVisible.requestFocus();
        }
    }

    private void updateCount() {
        if (count != null) count.setText(selected.size() + " dari " + MAX_APPS + " dipilih");
    }

    private void paint(Row row) {
        boolean on = selected.contains(row.pkg);
        boolean focused = row.view.hasFocus();
        row.bg.setColor(focused ? blend(palette.containerHigh, palette.primary, 0.18f)
                : on ? palette.containerHigh : palette.container);
        if (focused) {
            row.bg.setStroke(dp(2), palette.primary);
        } else if (on) {
            row.bg.setStroke(dp(1), withAlpha(palette.primary, 0x99));
        } else {
            row.bg.setStroke(dp(1), withAlpha(palette.onSurfaceVariant, 0x22));
        }
        row.name.setTextColor(palette.onSurface);
        row.check.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
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

    private List<ResolveInfo> launchableApps() {
        Map<String, ResolveInfo> byPackage = new LinkedHashMap<>();
        for (String category : new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
            for (ResolveInfo info : pm.queryIntentActivities(
                    new Intent(Intent.ACTION_MAIN).addCategory(category), 0)) {
                String pkg = info.activityInfo.packageName;
                if (pkg.equals(getPackageName()) || HomeActivity.NOT_IN_DOCK.contains(pkg)) continue;
                if (LauncherPrefs.isHidden(this, pkg)) continue;
                if (pm.getLaunchIntentForPackage(pkg) == null) continue;
                byPackage.putIfAbsent(pkg, info);
            }
        }
        List<ResolveInfo> apps = new ArrayList<>(byPackage.values());
        Map<ResolveInfo, String> labels = new LinkedHashMap<>();
        for (ResolveInfo info : apps) labels.put(info, String.valueOf(info.loadLabel(pm)));
        Collections.sort(apps, (a, b) -> labels.get(a).compareToIgnoreCase(labels.get(b)));
        return apps;
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
