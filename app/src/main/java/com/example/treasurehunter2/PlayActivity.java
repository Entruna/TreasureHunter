package com.example.treasurehunter2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
public class PlayActivity extends AppCompatActivity {
    Button btnGo;
    TextView distanceTextView;
    EditText latitudeEditText, longitudeEditText;
    double currentLatitude;
    double currentLongitude;
    double treasureLatitude;
    double treasureLongitude;
    float distance, newDistance;
    protected LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        btnGo = findViewById(R.id.btn_go);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        distanceTextView = findViewById(R.id.distanceTextView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(PlayActivity.this.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                    String latS = latitudeEditText.getText().toString();
                    String lonS = longitudeEditText.getText().toString();
                    if (latS.isEmpty() || lonS.isEmpty()) {
                        Toast.makeText(PlayActivity.this, R.string.enter_coordinates, Toast.LENGTH_SHORT).show();
                    } else {
                        if (latS.matches("(\\+|-)?([0-9]+(\\.[0-9]+))") && lonS.matches("(\\+|-)?([0-9]+(\\.[0-9]+))")) {
                            treasureLatitude = Double.parseDouble(latS);
                            treasureLongitude = Double.parseDouble(lonS);
                            float[] results = new float[1];
                            Location.distanceBetween(currentLatitude, currentLongitude,
                                    treasureLatitude, treasureLongitude, results);
                            distance = results[0] / 1000;
                            if (currentLatitude != 0 && currentLongitude != 0) {
                                distanceTextView.setText(String.valueOf(distance));
                            }
                        } else {
                            Toast.makeText(PlayActivity.this, R.string.incorrect_format, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(PlayActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(PlayActivity.this, R.string.not_enough_permission, Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                1, locationListener);
    }
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double currChangedLatitude = location.getLatitude();
            double currChangedLongitude = location.getLongitude();
            if (distance != 0) {
                float[] results = new float[1];
                location.distanceBetween(currChangedLatitude, currChangedLongitude,
                        treasureLatitude, treasureLongitude, results);
                newDistance = results[0] / 1000;
                distanceTextView.setText(String.valueOf(newDistance));
                if (newDistance == distance) {
                    btnGo.setBackgroundColor(Color.GRAY);
                } else if (newDistance < distance) {
                    btnGo.setBackgroundColor(Color.RED);
                    distanceTextView.setTextColor(Color.RED);
                    distance = newDistance;
                } else {
                    btnGo.setBackgroundColor(Color.BLUE);
                    distanceTextView.setTextColor(Color.BLUE);
                }
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(PlayActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        currentLatitude = addresses.get(0).getLatitude();
                        currentLongitude = addresses.get(0).getLongitude();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
