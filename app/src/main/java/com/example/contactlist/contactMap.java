package com.example.contactlist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

import org.w3c.dom.Text;

import java.util.List;
import java.io.IOException;

public class contactMap extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener gpsListener;

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

    public void initGetLocationButton() {
        Button locationButton = findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fix null pointer exception by checking if EditTexts are null
                EditText editAddress = findViewById(R.id.editTextAddress);
                EditText editCity = findViewById(R.id.editTextCity);
                EditText editState = findViewById(R.id.editTextState); // Corrected from editTextText3
                EditText editZipCode = findViewById(R.id.editTextZipcode);

                // Check if any EditTexts are null
                if (editAddress == null || editCity == null || editState == null || editZipCode == null) {
                    // Handle the case where EditTexts are not found
                    return;
                }

                String address = editAddress.getText().toString() + ", " +
                        editCity.getText().toString() + ", " +
                        editState.getText().toString() + ", " +
                        editZipCode.getText().toString();
                List<Address> addresses = null;
                Geocoder geo = new Geocoder(contactMap.this);
                try {
                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextView txtLatitude = findViewById(R.id.textLatitude);
                TextView txtLongitude = findViewById(R.id.textLongitude);

                // Check if addresses is null or empty
                if (addresses != null && !addresses.isEmpty()) {
                    txtLatitude.setText(String.valueOf(addresses.get(0).getLatitude()));
                    txtLongitude.setText(String.valueOf(addresses.get(0).getLongitude()));
                } else {
                    // Handle the case where no addresses were found
                    txtLatitude.setText("No coordinates found");
                    txtLongitude.setText("No coordinates found");
                }
            }
        });
    }
}
