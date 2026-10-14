package com.example.autoclicker;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingPanelService extends Service {
    private WindowManager wm; private View panel, picker; private WindowManager.LayoutParams params, pickerParams;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false; private int clicks = 0; private TextView status, targetText; private Button startButton;
    private int ink = Color.rgb(28,45,37), green = Color.rgb(42,105,76);
    @Override public void onCreate() { super.onCreate(); if (!Settings.canDrawOverlays(this)) { stopSelf(); return; } wm = (WindowManager)getSystemService(WINDOW_SERVICE); createPanel(); }
    private void createPanel() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(12),dp(10),dp(12),dp(10)); box.setBackgroundColor(Color.rgb(250,251,249));
        TextView header = new TextView(this); header.setText("✦ SELORRIA  ·  AutoClicker"); header.setTextColor(ink); header.setTextSize(14); header.setTypeface(null,Typeface.BOLD); header.setPadding(dp(4),dp(4),dp(4),dp(8)); box.addView(header);
        targetText = new TextView(this); targetText.setTextColor(green); targetText.setTextSize(12); box.addView(targetText); updateTargetText();
        status = new TextView(this); status.setText("Ready to tap"); status.setTextColor(ink); status.setPadding(dp(0),dp(6),dp(0),dp(4)); box.addView(status);
        LinearLayout row = new LinearLayout(this); Button pick = button("Pick target"), start = button("Start"), stop = button("Stop"), close = button("×"); row.addView(pick); row.addView(start); row.addView(stop); row.addView(close); box.addView(row);
        pick.setOnClickListener(v -> showPicker()); startButton = start; start.setOnClickListener(v -> startRun()); stop.setOnClickListener(v -> stopRun("Stopped")); close.setOnClickListener(v -> stopSelf());
        header.setOnTouchListener(new View.OnTouchListener(){float sx,sy;int ox,oy;public boolean onTouch(View v,MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){sx=e.getRawX();sy=e.getRawY();ox=params.x;oy=params.y;return true;}if(e.getAction()==MotionEvent.ACTION_MOVE){params.x=ox+(int)(e.getRawX()-sx);params.y=oy+(int)(e.getRawY()-sy);wm.updateViewLayout(panel,params);return true;}return e.getAction()==MotionEvent.ACTION_UP;}});
        panel = box; params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,windowType(),WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT); params.gravity=Gravity.TOP|Gravity.START;params.x=dp(12);params.y=dp(100); try{wm.addView(panel,params);}catch(Exception e){stopSelf();}
    }
    private void showPicker() {
        if (picker != null) return;
        stopRun("Paused for target selection");
        TextView overlay = new TextView(this); overlay.setText("TAP THE TARGET POSITION\n\nTouch the exact spot you want to automate.\nTap anywhere to select."); overlay.setTextColor(Color.WHITE); overlay.setTextSize(18); overlay.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL); overlay.setPadding(dp(24),dp(90),dp(24),dp(24)); overlay.setBackgroundColor(0x66000000);
        overlay.setOnTouchListener((v,e)->{ if(e.getAction()==MotionEvent.ACTION_UP){int x=(int)e.getRawX(),y=(int)e.getRawY();getSharedPreferences("selorria",MODE_PRIVATE).edit().putInt("targetX",x).putInt("targetY",y).apply(); removePicker(); updateTargetText(); if(status!=null)status.setText("Target selected · "+x+", "+y); return true;} return true; });
        picker=overlay; pickerParams=new WindowManager.LayoutParams(-1,-1,windowType(),WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);pickerParams.gravity=Gravity.TOP|Gravity.START;try{wm.addView(picker,pickerParams);}catch(Exception e){picker=null;toast("Could not open target picker.");}
    }
    private void removePicker(){if(wm!=null&&picker!=null){try{wm.removeView(picker);}catch(Exception ignored){}picker=null;}}
    private void updateTargetText(){if(targetText==null)return;int x=getSharedPreferences("selorria",MODE_PRIVATE).getInt("targetX",-1),y=getSharedPreferences("selorria",MODE_PRIVATE).getInt("targetY",-1);targetText.setText(x<0?"Target: not selected":"Target: X "+x+" · Y "+y);}
    private void startRun(){if(running)return;if(GestureAccessibilityService.instance==null){toast("Enable Selorria AutoClicker in Accessibility settings first.");return;}int x=getSharedPreferences("selorria",MODE_PRIVATE).getInt("targetX",-1),y=getSharedPreferences("selorria",MODE_PRIVATE).getInt("targetY",-1);if(x<0||y<0){toast("Pick a target first.");return;}running=true;clicks=0;status.setText("Running · 0 taps");runNext();}
    private void runNext(){if(!running)return;int repeat=getSharedPreferences("selorria",MODE_PRIVATE).getInt("repeat",0);
        if(repeat>0&&clicks>=repeat){stopRun("Complete · "+clicks+" taps");return;}
        int x=getSharedPreferences("selorria",MODE_PRIVATE).getInt("targetX",-1),y=getSharedPreferences("selorria",MODE_PRIVATE).getInt("targetY",-1);int interval=getSharedPreferences("selorria",MODE_PRIVATE).getInt("interval",500);
        GestureAccessibilityService svc=GestureAccessibilityService.instance;if(svc==null){stopRun("Accessibility service unavailable");return;}
        boolean accepted=svc.tap(x,y,()->handler.post(()->{if(!running)return;clicks++;if(status!=null)status.setText("Running · "+clicks+" taps");handler.postDelayed(this::runNext,interval);}));if(!accepted)stopRun("Tap failed; check accessibility permission.");
    }
    private void stopRun(String msg){running=false;handler.removeCallbacksAndMessages(null);if(status!=null)status.setText(msg);}
    private Button button(String s){Button b=new Button(this);b.setText(s);b.setTextSize(11);b.setAllCaps(false);b.setPadding(dp(3),0,dp(3),0);b.setTextColor(green);return b;}
    private int windowType(){return android.os.Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    @Override public int onStartCommand(Intent i,int flags,int id){return START_NOT_STICKY;}
    @Override public void onDestroy(){stopRun("Stopped");removePicker();if(wm!=null&&panel!=null)try{wm.removeView(panel);}catch(Exception ignored){}super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
