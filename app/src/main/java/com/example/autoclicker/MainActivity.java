package com.example.autoclicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ScrollView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText intervalInput, repeatInput;
    private TextView targetLabel;
    private int ink = Color.rgb(25, 39, 32);
    private int green = Color.rgb(37, 91, 64);
    private int pale = Color.rgb(235, 241, 232);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(24), dp(22), dp(28));
        root.setBackgroundColor(Color.rgb(247, 247, 241));

        TextView brand = new TextView(this); brand.setText("✦  SELORRIA  /  DIGITAL PRODUCTS"); brand.setTextColor(green); brand.setTextSize(11); brand.setLetterSpacing(.10f); brand.setTypeface(null, 1); root.addView(brand);
        TextView title = new TextView(this); title.setText("Selorria AutoClicker"); title.setTextColor(ink); title.setTextSize(28); title.setTypeface(null, 1); title.setPadding(0, dp(8), 0, 0); root.addView(title);
        TextView tagline = new TextView(this); tagline.setText("Your Wish. Our Command."); tagline.setTextColor(green); tagline.setTextSize(16); tagline.setPadding(0, dp(3), 0, dp(8)); root.addView(tagline);
        TextView intro = new TextView(this); intro.setText("Click less. Get more done.\n\nA lightweight tool for simple repeated tapping. Choose your target, set an interval, and stay in control."); intro.setTextColor(Color.rgb(90, 101, 94)); intro.setTextSize(15); intro.setLineSpacing(dp(3), 1f); intro.setPadding(0, 0, 0, dp(22)); root.addView(intro);

        TextView targetHeading = heading("TARGET POSITION"); root.addView(targetHeading);
        targetLabel = new TextView(this); targetLabel.setTextColor(ink); targetLabel.setTextSize(15); targetLabel.setPadding(dp(14), dp(14), dp(14), dp(14)); targetLabel.setBackground(round(pale, 16)); targetLabel.setElevation(dp(1)); root.addView(targetLabel, fullWidth()); refreshTarget();
        Button overlay = button("Allow floating controls"); root.addView(overlay, spaced()); overlay.setOnClickListener(v -> { if (!Settings.canDrawOverlays(this)) startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))); else toast("Floating controls are already allowed."); });
        Button accessibility = button("Enable accessibility service"); root.addView(accessibility, spaced()); accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        TextView intervalHeading = heading("CLICK INTERVAL (MILLISECONDS)"); intervalHeading.setPadding(0, dp(22), 0, dp(8)); root.addView(intervalHeading);
        intervalInput = new EditText(this); intervalInput.setSingleLine(true); intervalInput.setInputType(2); intervalInput.setText(String.valueOf(getSharedPreferences("selorria", MODE_PRIVATE).getInt("interval", 500))); intervalInput.setHint("500"); root.addView(intervalInput, fullWidth());
        TextView repeatHeading = heading("REPEAT COUNT"); repeatHeading.setPadding(0, dp(18), 0, dp(8)); root.addView(repeatHeading);
        repeatInput = new EditText(this); repeatInput.setSingleLine(true); repeatInput.setInputType(2); repeatInput.setText(String.valueOf(getSharedPreferences("selorria", MODE_PRIVATE).getInt("repeat", 0))); repeatInput.setHint("0 = continuous"); root.addView(repeatInput, fullWidth());
        TextView hint = new TextView(this); hint.setText("Enter 0 for continuous tapping, or a positive number for a fixed number of taps. Minimum interval: 50 ms."); hint.setTextColor(Color.rgb(100, 110, 103)); hint.setTextSize(13); hint.setPadding(0, dp(8), 0, dp(8)); root.addView(hint);
        Button launch = button("Launch floating controls"); launch.setTextColor(Color.WHITE); launch.setBackground(round(green, 16)); LinearLayout.LayoutParams lp = spaced(); lp.topMargin = dp(20); root.addView(launch, lp); launch.setOnClickListener(v -> launchPanel());
        TextView footer = new TextView(this); footer.setText("✦  DIGITAL-FIRST  ·  NO ROOT REQUIRED  ·  ANDROID 7.0+"); footer.setGravity(Gravity.CENTER); footer.setTextColor(Color.GRAY); footer.setTextSize(12); footer.setPadding(0, dp(20), 0, 0); root.addView(footer);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.addView(root); setContentView(scroll);
    }

    private void launchPanel() {
        try {
            int interval = Integer.parseInt(intervalInput.getText().toString().trim());
            int repeat = Integer.parseInt(repeatInput.getText().toString().trim());
            if (interval < 50 || repeat < 0) throw new NumberFormatException();
            getSharedPreferences("selorria", MODE_PRIVATE).edit().putInt("interval", interval).putInt("repeat", repeat).apply();
            if (!Settings.canDrawOverlays(this)) { toast("Allow floating controls first."); startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))); return; }
            startService(new Intent(this, FloatingPanelService.class));
            toast("Floating controls launched. Open the app you want to tap, then choose Pick target.");
        } catch (NumberFormatException e) { toast("Enter an interval of at least 50 ms and a repeat count of 0 or more."); }
    }
    @Override protected void onResume() { super.onResume(); if (targetLabel != null) refreshTarget(); }
    private void refreshTarget() { int x = getSharedPreferences("selorria", MODE_PRIVATE).getInt("targetX", -1), y = getSharedPreferences("selorria", MODE_PRIVATE).getInt("targetY", -1); targetLabel.setText(x < 0 ? "No target selected yet" : "Selected target  ·  X " + x + "  /  Y " + y); }
    private TextView heading(String s) { TextView t = new TextView(this); t.setText(s); t.setTextColor(ink); t.setTextSize(12); t.setTypeface(null, 1); t.setPadding(0, 0, 0, dp(8)); return t; }
    private Button button(String s) { Button b = new Button(this); b.setText(s); b.setAllCaps(false); b.setTextColor(green); b.setBackground(round(pale, 14)); b.setPadding(dp(12), dp(8), dp(12), dp(8)); return b; }
    private android.graphics.drawable.GradientDrawable round(int color, int radius) { android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); d.setStroke(dp(1), Color.rgb(221, 229, 217)); return d; }
    private LinearLayout.LayoutParams fullWidth() { return new LinearLayout.LayoutParams(-1, -2); }
    private LinearLayout.LayoutParams spaced() { LinearLayout.LayoutParams p = fullWidth(); p.topMargin = dp(8); return p; }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
