package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.csemaster.paylpu.Modals.UserModel;
import com.csemaster.paylpu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserInfoActivity extends AppCompatActivity {
    EditText userName;
    ImageView profileImage;
    Button saveButton;
    ProgressBar userInfoProcess;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("UserInfo");
    String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        userName=findViewById(R.id.userName);
        profileImage=findViewById(R.id.imageViewProfile);
        saveButton=findViewById(R.id.buttonSave);
        userInfoProcess=findViewById(R.id.userInfoProgress);

        Intent intent=getIntent();
        mobileNo=intent.getStringExtra("Mobile");
        firebaseAuth=FirebaseAuth.getInstance();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    private void saveUserInformation() {
        userInfoProcess.setVisibility(View.VISIBLE);
        final String displayName=userName.getText().toString().trim();


        if(displayName.isEmpty())
        {
            userName.setError("Name Required");
            userName.requestFocus();
            return;
        }

        final FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user!=null)
        {
            UserModel userModel=new UserModel();
            userModel.setName(displayName);
            userModel.setuId(firebaseAuth.getCurrentUser().getUid());
            collectionReference
                    .add(userModel);

            userInfoProcess.setVisibility(View.GONE);
            startActivity(new Intent(UserInfoActivity.this,MapActivity.class));
        }
    }



}
