package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText phoneNumber, codeEnter;
    Button nxtBtn;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress = false;
    FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        phoneNumber = findViewById(R.id.phone);
        nxtBtn = findViewById(R.id.nextBtn);
        codeEnter = findViewById(R.id.codeEnter);
        progressBar = findViewById(R.id.progressBar);
        state = findViewById(R.id.state);
        codePicker = findViewById(R.id.ccp);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationInProgress){
                    if (!phoneNumber.getText().toString().isEmpty() && phoneNumber.getText().toString().length() == 10){
                        String phoneNum = "+"+codePicker.getSelectedCountryCode()+phoneNumber.getText().toString();
                        //Toast.makeText(PhoneActivity.this,""+phoneNum,Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP ...");
                        state.setVisibility(View.VISIBLE);
                        requestOTP(phoneNum);

                    }else{
                        phoneNumber.setError("Phone Number is Invalid");
                    }
                }else{
                    String userOTP = codeEnter.getText().toString();
                    if (!userOTP.isEmpty() && userOTP.length() == 6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userOTP);
                        verfyAuth(credential);
                    }else{
                        codeEnter.setError("Valid OTP is required.");
                    }
                }
            }
        });
    }


    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(PhoneActivity.this,"OTP Expired! Pls resend",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);
                verificationId = s;
                token = forceResendingToken;
                nxtBtn.setText("VERIFY");
                verificationInProgress = true;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verfyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneActivity.this,"Cannot Create Account"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verfyAuth(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PhoneActivity.this,"Verification Successfull!",Toast.LENGTH_SHORT).show();
                    checkUserProfile();
                }else {
                    Toast.makeText(PhoneActivity.this,"Verification Failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (firebaseAuth.getCurrentUser() != null){
//            progressBar.setVisibility(View.VISIBLE);
//            state.setText("Checking...");
//            state.setVisibility(View.VISIBLE);
//
//            checkUserProfile();
//        }
    }

    private void checkUserProfile() {
        DocumentReference documentRef = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(),phnUserActivity.class));
                    finish();
//                    Toast.makeText(PhoneActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PhoneActivity.this, "Profile Do Not Exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}