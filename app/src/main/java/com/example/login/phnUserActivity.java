package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class phnUserActivity extends AppCompatActivity {
    EditText name,mail;
    Button savebtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phn_user);
        name = findViewById(R.id.usrname);
        mail = findViewById(R.id.mail);
        savebtn = findViewById(R.id.savebtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!name.getText().toString().isEmpty() && !mail.getText().toString().isEmpty()){
                    String namee = name.getText().toString();
                    String maill = mail.getText().toString();

                    DocumentReference docReference = firestore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fName",name.getText().toString());
                        user.put("email",mail.getText().toString());
                        user.put("phone",firebaseAuth.getCurrentUser().getPhoneNumber());

                        docReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
                                    //Toast.makeText(phnUserActivity.this,""+namE +" "+maiL,Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                    finish();
//                                }else{
//                                    Toast.makeText(phnUserActivity.this,"Error! Please Try Again",Toast.LENGTH_SHORT).show();
//                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("LOG", "onFailure: Failed to Create User " + e.toString());
                            }
                        });

                }else{
                    Toast.makeText(phnUserActivity.this,"All Fields Required!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}