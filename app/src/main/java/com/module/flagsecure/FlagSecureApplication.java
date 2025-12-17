package com.module.flagsecure;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application class ile tüm Activity'lere otomatik güvenlik uygular.
 * Extend almaya gerek yok - sadece manifest'te tanımla.
 * 
 * Null-safe ve thread-safe implementasyon.
 * 
 * KULLANIM:
 * 1. AndroidManifest.xml'de: android:name=".FlagSecureApplication"
 * 2. Bitti! Tüm Activity'ler otomatik korunur.
 */
public class FlagSecureApplication extends Application {

    @NonNull
    private final Map<Integer, WeakReference<View>> overlayViews = new ConcurrentHashMap<>();
    
    private volatile boolean screenshotProtectionEnabled = true;
    private volatile boolean backgroundProtectionEnabled = true;
    private volatile int overlayColor = Color.WHITE;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new SecurityLifecycleCallbacks());
    }

    public void setScreenshotProtectionEnabled(boolean enabled) {
        this.screenshotProtectionEnabled = enabled;
    }

    public void setBackgroundProtectionEnabled(boolean enabled) {
        this.backgroundProtectionEnabled = enabled;
    }

    public void setOverlayColor(int color) {
        this.overlayColor = color;
    }

    public boolean isScreenshotProtectionEnabled() {
        return screenshotProtectionEnabled;
    }

    public boolean isBackgroundProtectionEnabled() {
        return backgroundProtectionEnabled;
    }

    private class SecurityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            if (screenshotProtectionEnabled) {
                applyFlagSecure(activity);
            }
            
            if (backgroundProtectionEnabled) {
                createOverlay(activity);
            }
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            hideOverlay(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            showOverlay(activity);
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            overlayViews.remove(activity.hashCode());
        }
    }

    private void applyFlagSecure(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (window != null) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }
    }

    private void createOverlay(@NonNull Activity activity) {
        View overlay = new View(activity);
        overlay.setBackgroundColor(overlayColor);
        overlay.setVisibility(View.GONE);
        overlayViews.put(activity.hashCode(), new WeakReference<>(overlay));
    }

    private void showOverlay(@NonNull Activity activity) {
        if (!backgroundProtectionEnabled) return;
        
        WeakReference<View> overlayRef = overlayViews.get(activity.hashCode());
        if (overlayRef == null) return;
        
        View overlay = overlayRef.get();
        if (overlay == null) {
            overlayViews.remove(activity.hashCode());
            return;
        }

        Window window = activity.getWindow();
        if (window == null) return;
        
        View decorView = window.getDecorView();
        if (decorView == null) return;
        
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        if (rootView == null) return;

        if (overlay.getParent() == null) {
            rootView.addView(overlay, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
        overlay.setVisibility(View.VISIBLE);
        overlay.bringToFront();
    }

    private void hideOverlay(@NonNull Activity activity) {
        WeakReference<View> overlayRef = overlayViews.get(activity.hashCode());
        if (overlayRef == null) return;
        
        View overlay = overlayRef.get();
        if (overlay == null) {
            overlayViews.remove(activity.hashCode());
            return;
        }
        
        overlay.setVisibility(View.GONE);
    }
}
