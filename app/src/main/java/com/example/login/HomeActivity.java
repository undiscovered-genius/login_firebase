package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button scan;
    Button details;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, loginActivity.class);
                startActivity(intToMain);
            }
        });
        details = findViewById(R.id.button4);
        try {

            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intToDetail = new Intent(HomeActivity.this, Details.class);
                    startActivity(intToDetail);


                }
            });

        } catch (Exception e) {
           details.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i = new Intent(HomeActivity.this, Details.class);
                   startActivity(i);
               }
           });
        }

        scan = findViewById(R.id.button3);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToScan = new Intent(HomeActivity.this, Scanner.class);
                startActivity(intToScan);
            }
        });

    }
}
