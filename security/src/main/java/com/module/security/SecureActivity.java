package com.module.security;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Güvenli Activity base class.
 * Bu sınıftan extend eden Activity'ler otomatik olarak:
 * - Ekran görüntüsü koruması
 * - Arka plan koruması
 * özelliklerine sahip olur.
 * 
 * KULLANIM:
 * public class MyActivity extends SecureActivity {
 *     // Otomatik güvenlik aktif
 * }
 */
public abstract class SecureActivity extends AppCompatActivity {

    private FlagSecureHelper flagSecureHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        flagSecureHelper = new FlagSecureHelper.Builder(this)
                .setScreenshotProtection(isScreenshotProtectionEnabled())
                .setBackgroundProtection(isBackgroundProtectionEnabled())
                .setOverlayColor(getOverlayColor())
                .build();
        
        flagSecureHelper.apply();
        getLifecycle().addObserver(flagSecureHelper);
    }

    protected boolean isScreenshotProtectionEnabled() {
        return true;
    }

    protected boolean isBackgroundProtectionEnabled() {
        return true;
    }

    protected int getOverlayColor() {
        return Color.WHITE;
    }
}
