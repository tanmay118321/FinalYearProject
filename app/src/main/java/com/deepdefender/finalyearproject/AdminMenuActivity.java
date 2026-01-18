package com.deepdefender.finalyearproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class AdminMenuActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etBreakfast, etLunch, etDinner;
    Button btnUpdate;

    DatabaseReference dbRef;
    String selectedDay = "Monday";

    int[] dayIds = {
            R.id.edtMon, R.id.edtTue, R.id.edtWed,
            R.id.edtThu, R.id.edtFri, R.id.edtSat, R.id.edtSun
    };

    String[] dayNames = {
            "Monday","Tuesday","Wednesday",
            "Thursday","Friday","Saturday","Sunday"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminmenu);

        etBreakfast = findViewById(R.id.edtBreakfastItems);
        etLunch = findViewById(R.id.edtLunchItems);
        etDinner = findViewById(R.id.edtDinnerItems);
        btnUpdate = findViewById(R.id.updatebutton);

        dbRef = FirebaseDatabase.getInstance().getReference("WeeklyMenu");

        for (int id : dayIds) {
            findViewById(id).setOnClickListener(this);
        }

        seedDefaultMenu();
        selectDay(0);

        btnUpdate.setOnClickListener(v -> updateMenu());
    }

    private void updateMenu() {
        MenuModel menu = new MenuModel(
                etBreakfast.getText().toString(),
                etLunch.getText().toString(),
                etDinner.getText().toString()
        );

        dbRef.child(selectedDay).setValue(menu)
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "Menu Updated", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < dayIds.length; i++) {
            if (v.getId() == dayIds[i]) {
                selectDay(i);
                break;
            }
        }
    }

    private void selectDay(int index) {
        selectedDay = dayNames[index];

        for (int i = 0; i < dayIds.length; i++) {
            TextView tv = findViewById(dayIds[i]);

            if (i == index) {
                tv.setBackgroundResource(R.drawable.days_bg);
            } else {
                tv.setBackgroundResource(android.R.color.transparent);
                // or default background drawable
                // tv.setBackgroundResource(R.drawable.days_bg_default);
            }
        }

        loadMenu(selectedDay);
    }


    private void loadMenu(String day) {
        dbRef.child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                MenuModel m = s.getValue(MenuModel.class);
                if (m != null) {
                    etBreakfast.setText(m.breakfast);
                    etLunch.setText(m.lunch);
                    etDinner.setText(m.dinner);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { }
        });
    }

    private void seedDefaultMenu() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                if (!s.exists()) {
                    dbRef.child("Monday").setValue(new MenuModel("Poha","Dum Aloo/Dal Wadi","Soyabean/Baigan Bharta"));
                    dbRef.child("Tuesday").setValue(new MenuModel("Pasta","Patagobi/Shimla Mirch","Bhendi Aloo"));
                    dbRef.child("Wednesday").setValue(new MenuModel("Matki","Chole Aloo","Kadi Bhat"));
                    dbRef.child("Thursday").setValue(new MenuModel("Poha","Dal Loki","Chavli/Gavar"));
                    dbRef.child("Friday").setValue(new MenuModel("Chane","Fulgobi","Baigan Aloo"));
                    dbRef.child("Saturday").setValue(new MenuModel("Matki","Aloo Mattar","Kadi Bhat"));
                    dbRef.child("Sunday").setValue(new MenuModel("Sunday Special","Dal Tadka","Chicken/Paneer"));
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { }
        });
    }
}
