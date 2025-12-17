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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Activity lifecycle callbacks ile güvenlik özelliklerini yönetir.
 * - FLAG_SECURE ile screenshot/screen recording engelleme
 * - Background'a geçişte overlay ile içerik gizleme
 * 
 * Thread-safe ve null-safe implementasyon.
 */
public class SecurityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @NonNull
    private final Map<Integer, View> overlayViews = new ConcurrentHashMap<>();

    private volatile boolean screenshotProtectionEnabled = true;
    private volatile boolean backgroundProtectionEnabled = true;
    private volatile int overlayColor = Color.WHITE;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (screenshotProtectionEnabled) {
            applyFlagSecure(activity);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (backgroundProtectionEnabled && !overlayViews.containsKey(getActivityKey(activity))) {
            attachOverlay(activity);
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        setOverlayVisibility(activity, false);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        setOverlayVisibility(activity, true);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        int key = getActivityKey(activity);
        View overlay = overlayViews.remove(key);
        if (overlay != null && overlay.getParent() != null) {
            ((ViewGroup) overlay.getParent()).removeView(overlay);
        }
    }

    // region Public API

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

    // endregion

    // region Private Helpers

    private int getActivityKey(@NonNull Activity activity) {
        return System.identityHashCode(activity);
    }

    private void applyFlagSecure(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (window == null) return;

        window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );
    }

    private void attachOverlay(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (window == null) return;

        View decorView = window.getDecorView();
        if (decorView == null) return;

        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        if (rootView == null) return;

        View overlay = new View(activity);
        overlay.setBackgroundColor(overlayColor);
        overlay.setVisibility(View.GONE);

        rootView.addView(overlay, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        overlayViews.put(getActivityKey(activity), overlay);
    }

    private void setOverlayVisibility(@NonNull Activity activity, boolean visible) {
        if (!backgroundProtectionEnabled) return;

        View overlay = overlayViews.get(getActivityKey(activity));
        if (overlay == null) return;

        if (visible) {
            overlay.bringToFront();
            overlay.setVisibility(View.VISIBLE);
        } else {
            overlay.setVisibility(View.GONE);
        }
    }

    // endregion
}
