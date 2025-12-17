package com.module.flagsecure;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * Application class ile tüm Activity'lere otomatik güvenlik uygular.
 * Extend almaya gerek yok - sadece manifest'te tanımla.
 * 
 * KULLANIM:
 * 1. AndroidManifest.xml'de: android:name=".FlagSecureApplication"
 * 2. Bitti! Tüm Activity'ler otomatik korunur.
 */
public class FlagSecureApplication extends Application {

    @NonNull
    private final SecurityLifecycleCallbacks securityCallbacks = new SecurityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(securityCallbacks);
    }

    /**
     * Güvenlik ayarlarına erişim sağlar.
     */
    @NonNull
    public SecurityLifecycleCallbacks getSecurityCallbacks() {
        return securityCallbacks;
    }

    // region Convenience Methods (delegate to SecurityLifecycleCallbacks)

    public void setScreenshotProtectionEnabled(boolean enabled) {
        securityCallbacks.setScreenshotProtectionEnabled(enabled);
    }

    public void setBackgroundProtectionEnabled(boolean enabled) {
        securityCallbacks.setBackgroundProtectionEnabled(enabled);
    }

    public void setOverlayColor(int color) {
        securityCallbacks.setOverlayColor(color);
    }

    public boolean isScreenshotProtectionEnabled() {
        return securityCallbacks.isScreenshotProtectionEnabled();
    }

    public boolean isBackgroundProtectionEnabled() {
        return securityCallbacks.isBackgroundProtectionEnabled();
    }

    // endregion
}
