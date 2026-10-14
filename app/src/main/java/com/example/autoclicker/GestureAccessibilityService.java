package com.example.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

public class GestureAccessibilityService extends AccessibilityService {
    static volatile GestureAccessibilityService instance;
    @Override public void onServiceConnected(){super.onServiceConnected();instance=this;}
    @Override public void onAccessibilityEvent(AccessibilityEvent event){}
    @Override public void onInterrupt(){}
    @Override public void onDestroy(){if(instance==this)instance=null;super.onDestroy();}
    boolean tap(int x,int y,Runnable finished){
        if(Build.VERSION.SDK_INT<24)return false;
        Path path=new Path();path.moveTo(x,y);
        GestureDescription gesture=new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path,0,50)).build();
        return dispatchGesture(gesture,new GestureResultCallback(){@Override public void onCompleted(GestureDescription g){if(finished!=null)finished.run();}@Override public void onCancelled(GestureDescription g){if(finished!=null)finished.run();}},null);
    }
}
