package ru.examp.restaurantsnearby;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {

    private MapView mapview;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 1000;
    private static final double MINIMAL_DISTANCE = 1;
    private static final boolean USE_IN_BACKGROUND = false;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private Point myLocation;
    private boolean isLocationFound = false;

    // убираем привязку камеры к местоположению устройства, лишь единажды наведя камеру
    public void showUserPositionOnceOnMap() {
        if (!isLocationFound) {
            isLocationFound = true;
            mapview.getMap().move(
                    new CameraPosition(new Point(myLocation.getLatitude(), myLocation.getLongitude()), 17.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 0),
                    null);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String MAPKIT_API_KEY = "6a673eca-16bb-4c2a-98b9-acc88b012a1f";
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        mapview = findViewById(R.id.mapview);

        // show user's location
        MapKit mapKit = MapKitFactory.getInstance();
        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(mapview.getMapWindow());
        userLocationLayer.setVisible(true); // Sets user location visibility
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

        // get user's latitude and longitude
        mapView = (MapView) mapview;
        locationManager = MapKitFactory.getInstance().createLocationManager();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull @NotNull Location location) {
                myLocation = location.getPosition(); //this user point
                Log.w(TAG, "my location - " + myLocation.getLatitude() + "," + myLocation.getLongitude());
                showUserPositionOnceOnMap();
            }

            @Override
            public void onLocationStatusUpdated(@NonNull @NotNull LocationStatus locationStatus) {
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
        MapKitFactory.getInstance().onStop();

        locationManager.unsubscribe(myLocationListener);
        mapView.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();

        mapView.onStart();
        subscribeToLocationUpdate();
    }

    @Override
    public void onObjectAdded(@NonNull @org.jetbrains.annotations.NotNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectRemoved(@NonNull @org.jetbrains.annotations.NotNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectUpdated(@NonNull @org.jetbrains.annotations.NotNull UserLocationView userLocationView, @NonNull @org.jetbrains.annotations.NotNull ObjectEvent objectEvent) {
    }

    // https://yandex.ru/dev/maps/mapkit/doc/android-ref/lite/com/yandex/mapkit/location/LocationManager.html#_4
    private void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }
}
