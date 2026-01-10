package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.Fragment.AdminMessageFragment;
import com.deepdefender.finalyearproject.Fragment.MessageAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminDashboardActivity extends AppCompatActivity {

    CardView cardcomplaint,cardgeneratebills,cardupdatemenu,cardUpdateMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        cardcomplaint=findViewById(R.id.cardComplaint);
        cardgeneratebills=findViewById(R.id.cardGenerateBills);
        cardupdatemenu=findViewById(R.id.cardUpdateMenu);
        cardUpdateMenu = findViewById(R.id.cardUpdateMenu);

        Button anncouncement = findViewById(R.id.anncouncement);

        cardupdatemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this,AdminSideUpdateMenu.class);
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
        cardUpdateMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboardActivity.this,AdminMenuActivity.class);
                startActivity(intent);
            }
        });

        anncouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AdminDashboardActivity.this,Message_Activity.class);
                startActivity(intent);
                //openFragment(new AdminMessageFragment());
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