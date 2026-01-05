package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminSideUpdateMenu extends AppCompatActivity {

    // UI
    private TabLayout tabMealTypes;
    private EditText etDishName, etDescription;
    private SwitchMaterial switchSpecial;
    private ImageButton btnBack;

    // Firebase
    private DatabaseReference menuRef;

    // State
    private String selectedMeal = "breakfast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_side_update_menu);

        bindViews();
        initFirebase();
        setupTabs();
        setupBack();
        setupSaveButton();
    }

    // ================= VIEW BINDING =================

    private void bindViews() {

        tabMealTypes = findViewById(R.id.tab_meal_types);

        etDishName = findViewById(R.id.et_dish_name_1);
        etDescription = findViewById(R.id.et_description_1);
        switchSpecial = findViewById(R.id.switch_special_1);

        btnBack = findViewById(R.id.btn_back);
    }

    private void initFirebase() {
        menuRef = FirebaseDatabase.getInstance().getReference("today_menu");
    }

    // ================= TAB HANDLING =================

    private void setupTabs() {

        tabMealTypes.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        selectedMeal = tab.getText()
                                .toString()
                                .toLowerCase();

                        clearInputs();
                        loadExistingMenu();
                    }

                    @Override public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override public void onTabReselected(TabLayout.Tab tab) {}
                });
    }

    // ================= SAVE MENU =================

    private void setupSaveButton() {

        findViewById(R.id.btn_save_changes)
                .setOnClickListener(v -> saveMenu());
    }

    private void saveMenu() {

        String dishName = etDishName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean isSpecial = switchSpecial.isChecked();

        if (dishName.isEmpty()) {
            Toast.makeText(this,
                    "Dish name is required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> menuData = new HashMap<>();
        menuData.put("name", dishName);
        menuData.put("description", description);
        menuData.put("special", isSpecial);
        menuData.put("time", getMealTime(selectedMeal));
        menuData.put("timestamp", System.currentTimeMillis());

        menuRef.child(selectedMeal)
                .setValue(menuData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this,
                                "Menu saved successfully",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to save menu",
                                Toast.LENGTH_SHORT).show());
    }

    // ================= LOAD EXISTING MENU =================

    private void loadExistingMenu() {

        menuRef.child(selectedMeal)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) return;

                    etDishName.setText(
                            snapshot.child("name")
                                    .getValue(String.class));

                    etDescription.setText(
                            snapshot.child("description")
                                    .getValue(String.class));

                    Boolean special =
                            snapshot.child("special")
                                    .getValue(Boolean.class);

                    switchSpecial.setChecked(
                            special != null && special);
                });
    }

    // ================= HELPERS =================

    private void clearInputs() {
        etDishName.setText("");
        etDescription.setText("");
        switchSpecial.setChecked(false);
    }

    private void setupBack() {
        btnBack.setOnClickListener(v -> finish());
    }

    private String getMealTime(String meal) {

        switch (meal) {
            case "breakfast":
                return "8:00 AM - 10:00 AM";
            case "lunch":
                return "12:30 PM - 2:30 PM";
            case "dinner":
                return "7:30 PM - 9:30 PM";
            case "snacks":
                return "4:30 PM - 6:00 PM";
            default:
                return "";
        }
    }
}
