package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button scan;
    Button details;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userid;
    String userProvider;
    TextView fullName, email, phone;
    ImageView profileImage;
    ImageView changeProfileImage;
    StorageReference storageReference;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fullName = findViewById(R.id.usrname);
        email = findViewById(R.id.proemail);
        phone = findViewById(R.id.prophone);
        btnLogout = findViewById(R.id.logout);
        profileImage = findViewById(R.id.profileImg);
        changeProfileImage = findViewById(R.id.uploadimg);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

//        try {
//            userProvider = String.valueOf(mAuth.getCurrentUser().getProviderId());
            userid = mAuth.getCurrentUser().getUid();
//            Toast.makeText(HomeActivity.this, " "+userProvider, Toast.LENGTH_SHORT).show();
//            Log.d("tag", "onCreate: " +userProvider + mAuth.getCurrentUser().getEmail() +"  "+ mAuth.getCurrentUser().getDisplayName()+"  "+ mAuth.getCurrentUser().getPhoneNumber());
//            userid = mAuth.getCurrentUser().getUid();
//            String number = mAuth.getCurrentUser().getPhoneNumber();
//            final String num;
//            if (number == null){
//                num = "-";
//            }else{
//                num = mAuth.getCurrentUser().getPhoneNumber();
//            }
//            final DocumentReference documentreference= fstore.collection("users").document(userid);
//            Map<String,Object> user = new HashMap<>();
//            user.put("fName",mAuth.getCurrentUser().getDisplayName());
//            user.put("email",mAuth.getCurrentUser().getEmail());
//            user.put("phone",num);
//            documentreference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d("tag","Onsuccess: user Profile is createdfor "+userid);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d("tag","OnFailure : "+e.toString());
//                }
//            });

            StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);
                }
            });
            DocumentReference documentReference =fstore.collection("users").document(userid);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    phone.setText(value.getString("phone"));
                    fullName.setText(value.getString("fName"));
                    email.setText(value.getString("email"));
                }
            });
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // FirebaseAuth.getInstance().signOut();
                try {
                    GoogleSignIn.getClient(HomeActivity.this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                            .signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intToMain = new Intent(HomeActivity.this, MainActivity.class);
                            startActivity(intToMain);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this,"Error! Try Again",Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intToMain = new Intent(HomeActivity.this, loginActivity.class);
                startActivity(intToMain);
                finish();
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

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //image to firebase
        final StorageReference fileRef =storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(HomeActivity.this, "Uploading!!! Will take few seconds!", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
                Toast.makeText(HomeActivity.this, "Profile Uploaded!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

