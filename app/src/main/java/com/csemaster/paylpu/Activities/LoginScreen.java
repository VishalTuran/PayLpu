package com.csemaster.paylpu.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.csemaster.paylpu.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    EditText phoneSignIn;
    CardView signInButtonCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        phoneSignIn=findViewById(R.id.phoneSignIn);
        signInButtonCard=findViewById(R.id.signInButtonCard);


        signInButtonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo=phoneSignIn.getText().toString().trim();
                if (phoneNo.isEmpty() || phoneNo.length()<10)
                {
                    Toast.makeText(LoginScreen.this,"Enter valid Mobile Number",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent=new Intent(LoginScreen.this,OtpVerificationActivity.class);
                    intent.putExtra("PhoneNo",phoneNo);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseAuth.getInstance().signOut();
        super.onStart();

    }
}
