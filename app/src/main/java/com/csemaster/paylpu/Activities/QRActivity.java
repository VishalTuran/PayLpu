package com.csemaster.paylpu.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.csemaster.paylpu.R;

public class QRActivity extends AppCompatActivity {
    ImageView qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        qrCode=findViewById(R.id.qrCodeFullScreen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=getIntent();
        Glide.with(this).load(intent.getStringExtra("qrcode")).placeholder(R.drawable.qrcode).into(qrCode);
    }
}
