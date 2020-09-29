package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.zxing.Result;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static android.text.TextUtils.concat;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView zXingScannerView ;
    private static final int REQUEST_CAMERA = 1;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userid;
    Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        scan = findViewById(R.id.scan);
//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


        zXingScannerView = new ZXingScannerView(this);
        if(checkPermission())
        {
            Toast.makeText(Scanner.this, "PERMISSION is granted!", Toast.LENGTH_LONG).show();
        }
        else
        {
            requestPermission();
        }
    }
    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(Scanner.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }
    public void scan(View view){
        zXingScannerView  =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        //mAuth = FirebaseAuth.getInstance();
        //fstore = FirebaseFirestore.getInstance();
        //userid = mAuth.getCurrentUser().getUid();
        String qrcopy = scanResult;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zXingScannerView.resumeCameraPreview(Scanner.this);
            }
        });
        builder.setNeutralButton("UPLOAD", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userid = mAuth.getCurrentUser().getUid();
                final DocumentReference documentReference= fstore.collection("brcode").document(userid);
                Map<String,Object> user = new HashMap<>();
                user.putIfAbsent("Br_code",scanResult);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Scanner.this, "Upload successful, Thank You!", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG,"Onsuccess: user Profile is createdfor "+userid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Scanner.this," Unsuccessful, Please Try Again!" , Toast.LENGTH_SHORT).show();
                       // Log.d(TAG,"OnFailure : "+e.toString());
                    }
                });
                //Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(scanResult));
                //startActivity(intent);
            }
        });
        //database = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference qrnode = database.child("QRcode");
        //String qrnode = scanResult;
        //database.child("users").child(qrnode).setValue(scanResult);
        //qrnode.setValue(scanResult);
        Log.w("ggggg","something");

       //rootnode = FirebaseDatabase.getInstance();
        //ref = rootnode.getReference("Code");
        //ref.setValue("ayush");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");

        //myRef.setValue("Hello, World!");


        builder.setMessage(scanResult);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }




}
