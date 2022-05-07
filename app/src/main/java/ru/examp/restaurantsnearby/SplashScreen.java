package ru.examp.restaurantsnearby;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // в отдельном потоке проверяем, разрешён ли доступ к геолокации пользователя
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    // библиотека проверки разрешения на геолокацию
                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            // если разрешено - запускаем MainActivity и находим местоположение устройства
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            Toast.makeText(SplashScreen.this, "Доступ к геолокации запрещён\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                        }
                    };

                    TedPermission.create()
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("Для использования приложения необходимо предоставить доступ к геолокации: [Настройки] -> [Приложения] -> [Права]")
                            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                            .check();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}