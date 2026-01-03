package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {


        private EditText etEmail, etPassword;
        private Button btnLogin;

        private FirebaseAuth mAuth;
        TextView registertxt;

        private FirebaseFirestore db;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            registertxt=findViewById(R.id.tvRegister);


            btnLogin.setOnClickListener(v -> loginUser());
            registertxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Login.this,Register.class);
                    startActivity(intent);
                }
            });
        }

        private void loginUser() {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        String uid = mAuth.getCurrentUser().getUid();

                        db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(document -> {

                                    String role = document.getString("role");

                                    if ("admin".equals(role)) {
                                      //  startActivity(new Intent(this, AdminDashboardActivity.class));
                                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

                                    } else {
                                       startActivity(new Intent(this, HomeActivity.class));
                                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                });
                    })
                    .addOnFailureListener(e ->

                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show());
                            startActivity(new Intent(this, HomeActivity.class));
                            finish();
        }
    }

