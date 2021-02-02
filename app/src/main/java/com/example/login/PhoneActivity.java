package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);
                verificationId = s;
                token = forceResendingToken;

            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneActivity.this,"Cannot Create Account"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}