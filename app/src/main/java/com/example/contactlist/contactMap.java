package com.example.contactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.view.WindowManager;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.graphics.Point;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import java.io.IOException;
import android.hardware.Sensor;

public class contactMap extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_LOCATION = 101;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    ArrayList<Contact> contacts = new ArrayList<>();

    Contact currentContact = null;
    //chapter 8 sensor code
    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    TextView textDirection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);
        Bundle extras = getIntent().getExtras();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(mySensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "Sensors not found", Toast.LENGTH_LONG).show();
        }
        textDirection = (TextView) findViewById(R.id.textHeading);

        try {
            ContactDataSource ds = new ContactDataSource(contactMap.this);
            ds.open();
            if (extras != null) {
                currentContact = ds.getSpecificContact(extras.getInt("contactid"));
            } else {
                contacts = ds.getContacts("contactname", "ASC");
            }
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Contact(s) could not be retrieved.", Toast.LENGTH_LONG).show();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        createLocationRequest();
        createLocationCallback();

        initListButton();
        initSettingsButton();
        initMapButton();
        initMapTypeButtons();
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            float[] accelerometerValues;
            float[] magneticValues;
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticValues = event.values;
            if (accelerometerValues != null && magneticValues != null) {
                float R[] = new float[9];
                float I[] = new float[9];

                boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticValues);
                if (success) {
                    float orientation[] = new float [3];
                   SensorManager.getOrientation(R, orientation);

                   float azimut = (float) Math.toDegrees(orientation[0]);
                   if (azimut < 0.0f) { azimut+= 360.0f;}
                   String direction;
                   if (azimut >= 315 || azimut < 45) { direction = "N"; }
                   else if (azimut >= 225 && azimut < 315) { direction = "W";}
                   else if (azimut >= 135 && azimut < 225) { direction = "S";}
                   else { direction = "E";}
                   textDirection.setText(direction);
                }
        }
    }


    };

    private void initMapTypeButtons() {
        RadioGroup rgMapType = findViewById(R.id.radioGroupMapType);
        rgMapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbNormal = findViewById(R.id.radioButtonNormal);
                if (rbNormal.isChecked()) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
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

    private void initSettingsButton() {
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

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Set priority on locationRequest object
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                            " Long: " + location.getLongitude() +
                            " Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null); // Remove null from third argument
        gMap.setMyLocationEnabled(true);
    }

    private void stopLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        RadioButton rbNormal = findViewById(R.id.radioButtonNormal);
        rbNormal.setChecked(true);

        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < contacts.size(); i++) {
            currentContact = contacts.get(i);

            Geocoder geo = new Geocoder(this);
            List<Address> addresses = null;

            String address = currentContact.getStreetAddress() + ", " +
                    currentContact.getCity() + ", " +
                    currentContact.getState() + " " +
                    currentContact.getZipCode();

            try {
                addresses = geo.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    builder.include(point);
                    gMap.addMarker(new MarkerOptions().position(point).title(currentContact.getContactName()).snippet(address));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (builder.build().getCenter() != null) {
            LatLngBounds bounds = builder.build();
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, measuredWidth, measuredHeight, 450));
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(contactMap.this).create();
            alertDialog.setTitle("No Data");
            alertDialog.setMessage("No data is available for the mapping function.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
        }

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(contactMap.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(contactMap.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate " + "your contacts", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(
                                                contactMap.this, new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(contactMap.this, new
                                        String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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



        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(contactMap.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(contactMap.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate " + "your contacts", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(
                                                contactMap.this, new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(contactMap.this, new
                                        String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            try {
                stopLocationUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

}