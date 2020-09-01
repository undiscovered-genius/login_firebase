package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {

    EditText namedt;
    Button send;
    FirebaseAuth mAuth;
    TextView messagedt;
    FirebaseFirestore fstore;
    String userid;

    //FirebaseDatabase firebaseDatabase;
    //DatabaseReference databaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
            namedt = findViewById(R.id.namedata);
            messagedt = findViewById(R.id.messagedata);
            send = findViewById(R.id.btn_send);



        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Mname = namedt.getText().toString();
                final String Mmessage = messagedt.getText().toString();
                userid = mAuth.getCurrentUser().getUid();
                //Map<String, Object> feed = new HashMap<>();
                //feed.put("Name",Mname);
                //feed.put("message",Mmessage);
                //namestring = namedt;
                //mssagestring = messagedt;
              //  firebaseDatabase = FirebaseDatabase.getInstance();
               // databaseReference2 = firebaseDatabase.getReference("feedback");
               // UserDataClass userDataClass = new UserDataClass(Mname, Mmessage);

                //fstore.collection("feedback").document(userid).set(userDataClass).addOnCompleteListener(Details.this, new OnCompleteListener<Void>() {
                  //  @Override
                    //public void onComplete(@NonNull Task<Void> task) {
                      //  Toast.makeText(Details.this, "Upload successful, Thank You!", Toast.LENGTH_SHORT).show();
                    //}
               // });
               DocumentReference documentReference1 = fstore.collection("feedback").document(userid);
                Map<String, Object> user = new HashMap<>();
                user.put("Name",Mname);
                user.put("message",Mmessage);

                documentReference1.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                   public void onSuccess(Void aVoid) {
                        Toast.makeText(Details.this, "Upload successful, Thank You!", Toast.LENGTH_SHORT).show();
                        namedt.setText("");
                        messagedt.setText("");
                        //Log.d(TAG,"Onsuccess: user Profile is createdfor "+userID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Details.this, "Unsuccessful, Please Try Again!", Toast.LENGTH_SHORT).show();
                       // Log.d(TAG,"OnFailure : "+e.toString());
                    }
                });
            }
        });

    }
}
