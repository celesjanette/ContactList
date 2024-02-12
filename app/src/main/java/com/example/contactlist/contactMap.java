package com.example.contactlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.location.Geocoder;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import android.location.Location;
import org.w3c.dom.Text;
import com.google.android.material.snackbar.Snackbar;


import java.util.List;
import java.io.IOException;

public class contactMap extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener gpsListener;

    final int PERMISSION_REQUEST_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);

        // Call initialization methods
        initListButton();
        initSettingButton();
        initMapButton();
        initGetLocationButton();
    }

    private void initListButton() {
        ImageButton ibList = findViewById(R.id.ibContactList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contactMap.this, fullList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingButton() {
        ImageButton ibSetting = findViewById(R.id.ibSettings);
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contactMap.this, contactSetting.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibMap = findViewById(R.id.ibMap);
        ibMap.setEnabled(false);
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            gpsListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    TextView txtLatitude = findViewById(R.id.textLatitude);
                    TextView txtLongitude = findViewById(R.id.textLongitude);
                    TextView txtAccuracy = findViewById(R.id.textAccuracy);

                    txtLatitude.setText(String.valueOf(location.getLatitude()));
                    txtLongitude.setText(String.valueOf(location.getLongitude()));
                    txtAccuracy.setText(String.valueOf(location.getAccuracy()));
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

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error, Location not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call to super method

        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(contactMap.this, "MyContactList will not locate your contacts.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    public void initGetLocationButton() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(contactMap.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(contactMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate " + "your contacts", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(
                                                contactMap.this, new String[]{
                                                        android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(contactMap.this, new
                                        String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION);
                    }
                } else {
                    startLocationUpdates();
                }
            } else {
                startLocationUpdates();
            }

            try {
                startLocationUpdates();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            try {
                locationManager.removeUpdates(gpsListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}