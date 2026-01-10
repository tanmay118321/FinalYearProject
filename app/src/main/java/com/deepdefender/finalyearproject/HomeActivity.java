package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.Fragment.Profile;
import com.deepdefender.finalyearproject.Fragment.ProfileFragment;
import com.deepdefender.finalyearproject.Fragment.StudentDashboardFragment;
import com.deepdefender.finalyearproject.Fragment.UserMessageFragment;
import com.deepdefender.finalyearproject.Fragment.UserMenuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.homenav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new StudentDashboardFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    //rofileActivity profileActivity = new ProfileActivity();
    //Profile profile = new Profile();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        if (item.getItemId() == R.id.nav_home) {
            fragment = new StudentDashboardFragment();
        }
        else if(item.getItemId() == R.id.nav_profile)
        {
            fragment = new ProfileFragment();
        }
        else if (item.getItemId() == R.id.nav_menu)
        {
            fragment = new UserMessageFragment();
        }
        else if(item.getItemId() == R.id.nav_food_menu)
        {
            fragment = new UserMenuFragment();
        }
        // Future fragments can be added here
        // else if (item.getItemId() == R.id.nav_menu) { ... }nav_food_menu

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }

        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homescreen, fragment)
                .commit();
    }
}
