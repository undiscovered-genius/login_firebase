package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {
    Button email, phone;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID;
    SignInButton signInButton;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("222983164938-h7243aqvsgec6gqgg03uua6h9ni5qkkn.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null || mFirebaseAuth.getCurrentUser() != null){
            Toast.makeText(this,"User is Logged in Already",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Comming Soon!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}