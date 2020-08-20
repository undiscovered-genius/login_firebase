package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthUserCollisionException;
//import com.google.firebase.auth.FirebaseUser;


public class loginActivity extends AppCompatActivity {
    EditText emailId, password ;
    Button btnSignIn;
    TextView tvSignUp,mforgotpasswordlink, susu;
    FirebaseAuth mFirebaseAuth;
    //private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnSignIn = findViewById(R.id.button);
        tvSignUp = findViewById(R.id.textView);
        mforgotpasswordlink = findViewById(R.id.forgotpassword);
        susu = findViewById(R.id.textView7);
        susu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent zz= new Intent(loginActivity.this,MainActivity.class);
                startActivity(zz);
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view ){


                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(loginActivity.this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(loginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Intent intToHome = new Intent(loginActivity.this, HomeActivity.class);
                                startActivity(intToHome);
                                finish();
                            }
                        }
                    }) ;
                }
                else {
                    Toast.makeText(loginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mforgotpasswordlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetMail;
                resetMail = new EditText(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Reset Password?");
                builder.setMessage("Enter Your Email to Recieve Reset Link");
                builder.setView(resetMail);
                builder.setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail;
                        mail = resetMail.getText().toString();
                        mFirebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(loginActivity.this, "Reset Link Send To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception i) {
                                Toast.makeText(loginActivity.this, "Error! Reset Link is not Send.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                builder.setNegativeButton("No", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Intent intSignUp = new Intent(loginActivity.this, MainActivity.class);
                        //startActivity(intSignUp);

                    }
                });
                builder.create().show();
            }

        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(loginActivity.this, MainActivity.class);
                startActivity(intSignUp);
            }
        });

    }

}
