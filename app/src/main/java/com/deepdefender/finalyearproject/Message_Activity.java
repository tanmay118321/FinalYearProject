package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.Fragment.AdminMessageFragment;

public class Message_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        openFragment(new AdminMessageFragment());



    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Message_Activity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish(); // prevents returning to this activity
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null) // optional (for back button)
                .commit();
    }
}