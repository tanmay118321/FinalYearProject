package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deepdefender.finalyearproject.Fragment.AdminMessageFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminDashboardActivity extends AppCompatActivity {

    CardView cardcomplaint,cardgeneratebills,cardupdatemenu,anncouncement,cardManageStudents,cardAttendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        cardcomplaint=findViewById(R.id.cardComplaint);
        cardgeneratebills=findViewById(R.id.cardGenerateBills);
        cardupdatemenu=findViewById(R.id.cardUpdateMenu);
        anncouncement=findViewById(R.id.anncouncement);
        cardManageStudents=findViewById(R.id.cardManageStudents);
        cardAttendance=findViewById(R.id.cardAttendance);
        cardAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this,AdminSideAttendancePage.class);
                startActivity(intent);
            }
        });
        cardManageStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this, AdminSideManageStudent.class);
                startActivity(intent);
            }
        });
        anncouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AdminMessageFragment())
                        .commit();
            }
        });

        cardupdatemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this,AdminMenuActivity.class);
                startActivity(intent);
            }
        });
        cardcomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this,AdminSideComplaint.class);
                startActivity(intent);
            }
        });
        cardgeneratebills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this,AdminSideGenerateBill.class);
                startActivity(intent);

            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    FirebaseDatabase.getInstance()
                            .getReference("adminTokens")
                            .child("admin1")
                            .setValue(token);
                });


    }

}