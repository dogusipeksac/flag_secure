package com.module.flagsecure;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Switch switchScreenshot = findViewById(R.id.switchScreenshot);
        Switch switchBackground = findViewById(R.id.switchBackground);

        switchScreenshot.setChecked(true);
        switchBackground.setChecked(true);

        switchScreenshot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, 
                "Ekran görüntüsü koruması: " + (isChecked ? "Açık" : "Kapalı"), 
                Toast.LENGTH_SHORT).show();
        });

        switchBackground.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, 
                "Arka plan koruması: " + (isChecked ? "Açık" : "Kapalı"), 
                Toast.LENGTH_SHORT).show();
        });
    }
}
