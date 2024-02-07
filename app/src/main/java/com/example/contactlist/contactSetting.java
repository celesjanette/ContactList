package com.example.contactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.contactlist.R;
import android.annotation.SuppressLint;
public class contactSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_setting);
        initListButton();
        initSettingButton();
        initMapButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();
    }

    private void initListButton() {
        ImageButton ibList = findViewById(R.id.ibContactList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contactSetting.this, fullList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingButton() {
        ImageButton ibSetting = findViewById(R.id.ibSettings);
        ibSetting.setEnabled(false);


    }

    private void initMapButton() {
        ImageButton ibMap = findViewById(R.id.ibMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contactSetting.this, contactMap.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "ASC");
        RadioButton rbName = findViewById(R.id.radioName);
        RadioButton rbCity = findViewById(R.id.radioCity);
        RadioButton rbBirthday = findViewById(R.id.radioBirthday);
        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        } else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        } else {
            rbBirthday.setChecked(true);
        }
        RadioButton rbAscending = findViewById(R.id.radioAscending);
        RadioButton rbDescending = findViewById(R.id.radioDescending);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        } else {
            rbDescending.setChecked(true);
        }
    }

    private void initSortByClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener((arg0, arg1) -> { // arrow is a lamba expression and basically means go to the next
            RadioButton rbName = findViewById(R.id.radioName);
            RadioButton rbCity = findViewById(R.id.radioCity);

            // updating SharedPreferences based on the selected sorting criteria
            if (rbName.isChecked()) {
                getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortfield", "contactname").apply();
            } else if (rbCity.isChecked()) {
                getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortfield", "city").apply();
            } else {
                getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortfield", "birthday").apply();
            }
        });
    }

    // method to handle sorting order selection
    private void initSortOrderClick() {
        @SuppressLint("WrongViewCast") RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener((arg0, arg1) -> {
            RadioButton rbAscending = findViewById(R.id.radioAscending);

            // updating SharedPreferences based on the selected sorting order
            if (rbAscending.isChecked()) {
                getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortorder", "ASC").apply();
            } else {
                getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortorder", "DESC").apply();
            }
        });
    }
}