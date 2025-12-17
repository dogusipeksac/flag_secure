package com.module.security;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Modüler FLAG_SECURE yardımcı sınıfı.
 * - Ekran görüntüsü almayı engeller
 * - Uygulama arka plana alındığında içeriği gizler
 */
public class FlagSecureHelper implements DefaultLifecycleObserver {

    private final Activity activity;
    private View overlayView;
    private final boolean enableScreenshotProtection;
    private final boolean enableBackgroundProtection;
    private final int overlayColor;

    private FlagSecureHelper(Builder builder) {
        this.activity = builder.activity;
        this.enableScreenshotProtection = builder.enableScreenshotProtection;
        this.enableBackgroundProtection = builder.enableBackgroundProtection;
        this.overlayColor = builder.overlayColor;
    }

    public void apply() {
        if (enableScreenshotProtection) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }
        if (enableBackgroundProtection) {
            overlayView = new View(activity);
            overlayView.setBackgroundColor(overlayColor);
            overlayView.setVisibility(View.GONE);
        }
    }

    public void showOverlay() {
        if (overlayView != null && enableBackgroundProtection) {
            ViewGroup rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            if (overlayView.getParent() == null) {
                rootView.addView(overlayView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
            }
            overlayView.setVisibility(View.VISIBLE);
            overlayView.bringToFront();
        }
    }

    public void hideOverlay() {
        if (overlayView != null) {
            overlayView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        hideOverlay();
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        showOverlay();
    }

    public static class Builder {
        private final Activity activity;
        private boolean enableScreenshotProtection = true;
        private boolean enableBackgroundProtection = true;
        private int overlayColor = Color.WHITE;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setScreenshotProtection(boolean enable) {
            this.enableScreenshotProtection = enable;
            return this;
        }

        public Builder setBackgroundProtection(boolean enable) {
            this.enableBackgroundProtection = enable;
            return this;
        }

        public Builder setOverlayColor(int color) {
            this.overlayColor = color;
            return this;
        }

        public FlagSecureHelper build() {
            return new FlagSecureHelper(this);
        }
    }
}
