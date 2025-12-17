# Security Module

Ekran görüntüsü engelleme ve arka plan koruması için modüler Android kütüphanesi.

## Kurulum

### 1. Modülü projenize kopyalayın
`security` klasörünü projenizin root dizinine kopyalayın.

### 2. settings.gradle.kts'e ekleyin
```kotlin
include(":security")
```

### 3. app/build.gradle.kts'e dependency ekleyin
```kotlin
dependencies {
    implementation(project(":security"))
}
```

## Kullanım

### En Basit Yol: SecureActivity'den extend edin

```java
import com.module.security.SecureActivity;

public class MyActivity extends SecureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);
        // Otomatik olarak:
        // - Ekran görüntüsü engellenir
        // - Arka plana alındığında beyaz overlay gösterilir
    }
}
```

### Özelleştirme

```java
public class MyActivity extends SecureActivity {
    
    // Ekran görüntüsü korumasını kapat
    @Override
    protected boolean isScreenshotProtectionEnabled() {
        return false;
    }
    
    // Arka plan korumasını kapat
    @Override
    protected boolean isBackgroundProtectionEnabled() {
        return false;
    }
    
    // Overlay rengini değiştir
    @Override
    protected int getOverlayColor() {
        return Color.BLUE;
    }
}
```

### Manuel Kullanım (AppCompatActivity ile)

```java
public class MyActivity extends AppCompatActivity {
    private FlagSecureHelper helper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);
        
        helper = new FlagSecureHelper.Builder(this)
            .setScreenshotProtection(true)
            .setBackgroundProtection(true)
            .setOverlayColor(Color.WHITE)
            .build();
        
        helper.apply();
        getLifecycle().addObserver(helper);
    }
}
```

## Özellikler

| Özellik | Açıklama |
|---------|----------|
| Screenshot Engelleme | FLAG_SECURE ile ekran görüntüsü/kayıt engellenir |
| Arka Plan Koruması | Uygulama arka plandayken overlay gösterilir |
| Özelleştirilebilir | Renk ve özellikler override edilebilir |
