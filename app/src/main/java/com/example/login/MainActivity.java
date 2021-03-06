package com.example.login;

//package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
  public static final String TAG = "TAG";
  public static final int GOOGLE_SIGN_IN_CODE = 1005;
  EditText emailId, password, mphone, mfullname;
  Button btnSignUp;
  TextView tvSignIn;
  FirebaseAuth mFirebaseAuth;
  FirebaseFirestore fStore = FirebaseFirestore.getInstance();
  String userID,userid;
  SignInButton signInButton;
  GoogleSignInOptions gso;
  GoogleSignInClient signInClient;
  ImageButton phoneBtn;
  FirebaseFirestore fstore;

  private FirebaseFunctions mFunctions;
// ...


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mFirebaseAuth = FirebaseAuth.getInstance();
    emailId = findViewById(R.id.mail);
    password = findViewById(R.id.editText2);
    btnSignUp = findViewById(R.id.savebtn);
    tvSignIn = findViewById(R.id.textView);
    mphone = findViewById(R.id.prophone);
    mfullname = findViewById(R.id.usrname);
    mFunctions = FirebaseFunctions.getInstance();
    signInButton = findViewById(R.id.signGoogle);
    phoneBtn = findViewById(R.id.phoneButton);
    fstore = FirebaseFirestore.getInstance();

//Added in StartActivity

//    if (mFirebaseAuth.getCurrentUser() != null){
//      startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//      finish();
//    }
//
    gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("222983164938-h7243aqvsgec6gqgg03uua6h9ni5qkkn.apps.googleusercontent.com")
            .requestEmail()
            .build();
    signInClient = GoogleSignIn.getClient(this,gso);
    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//    if (signInAccount != null || mFirebaseAuth.getCurrentUser() != null){
//      Toast.makeText(this,"User is Logged in Already",Toast.LENGTH_SHORT).show();
//      startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//      finish();
//    }
    signInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent sign = signInClient.getSignInIntent();
        startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
      }
    });

    phoneBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, PhoneActivity.class));
      }
    });

    btnSignUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        final String email = emailId.getText().toString();
        final String pwd = password.getText().toString();
        final String fullname = mfullname.getText().toString();
        final String phone = mphone.getText().toString();

        if(email.isEmpty()){
          emailId.setError("Please enter email id");
          emailId.requestFocus();
        }
        else if(pwd.isEmpty()){
          password.setError("Please enter password");
          password.requestFocus();
        }
        else if(email.isEmpty() && pwd.isEmpty()){
          Toast.makeText(MainActivity.this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
        }
        else if(!(email.isEmpty() && pwd.isEmpty())){
          mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
              if(!task.isSuccessful()){
                Toast.makeText(MainActivity.this, "SignUp Unsuccessful, Please Try Again!", Toast.LENGTH_SHORT).show();
              }
              else {
                userID = mFirebaseAuth.getCurrentUser().getUid();
                final DocumentReference documentReference= fStore.collection("users").document(userID);
                Map<String,Object> user = new HashMap<>();
                user.put("fName",fullname);
                user.put("email",email);
                user.put("phone",phone);
                user.put("password",pwd);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                    Log.d(TAG,"Onsuccess: user Profile is createdfor "+userID);
                  }
                }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"OnFailure : "+e.toString());
                  }
                });
                // Add a new document with a generated ID

                startActivity(new Intent(MainActivity.this,HomeActivity.class));
              }
            }
          });
        }
        else {
          Toast.makeText(MainActivity.this, "Error Ocurred!", Toast.LENGTH_SHORT).show();
        }
      }
    }) ;

    tvSignIn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(MainActivity.this, loginActivity.class);
        startActivity(i);
      }
    }) ;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GOOGLE_SIGN_IN_CODE){
      Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        GoogleSignInAccount signInAccount = signInTask.getResult(ApiException.class);
        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
              Toast.makeText(getApplicationContext(),"Your Google Account is Connected",Toast.LENGTH_SHORT).show();
              //userid = mFirebaseAuth.getCurrentUser().getUid();
              //Toast.makeText(HomeActivity.this, " "+userProvider, Toast.LENGTH_SHORT).show();
              Log.d("tag", "onCreate: " + mFirebaseAuth.getCurrentUser().getEmail() +"  "+ mFirebaseAuth.getCurrentUser().getDisplayName()+"  "+ mFirebaseAuth.getCurrentUser().getPhoneNumber());
              userid = mFirebaseAuth.getCurrentUser().getUid();
              String number = mFirebaseAuth.getCurrentUser().getPhoneNumber();
              final String num;
              if (number == null){
                num = "-";
              }else{
                num = mFirebaseAuth.getCurrentUser().getPhoneNumber();
              }
              final DocumentReference documentreference= fstore.collection("users").document(userid);
              Map<String,Object> user = new HashMap<>();
              user.put("fName",mFirebaseAuth.getCurrentUser().getDisplayName());
              user.put("email",mFirebaseAuth.getCurrentUser().getEmail());
              user.put("phone",num);
              documentreference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  Log.d("tag","Onsuccess: user Profile is createdfor "+userid);
                }
              }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.d("tag","OnFailure : "+e.toString());
                }
              });
              startActivity(new Intent(MainActivity.this,HomeActivity.class));
              finish();
            }else{
              Toast.makeText(getApplicationContext(), "ERROR ", Toast.LENGTH_SHORT).show();
            }
          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(),"Error! Try Again",Toast.LENGTH_SHORT).show();
          }
        });

      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
  }

}