package com.example.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button scan;
    Button details;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userid;
    TextView fullName, email, phone;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fullName = findViewById(R.id.fullname);
        email = findViewById(R.id.proemail);
        phone = findViewById(R.id.prophone);
        btnLogout = findViewById(R.id.logout);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference =fstore.collection("users").document(userid);
       //documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
         //  @Override
           // public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
           //     phone.setText(value.getString("phone"));
            //    fullName.setText(value.getString("fName"));
            //    email.setText(value.getString("email"));
          //  }
       // });

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
