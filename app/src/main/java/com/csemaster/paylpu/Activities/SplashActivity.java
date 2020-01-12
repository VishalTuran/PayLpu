package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.csemaster.paylpu.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    String[] permissionString = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!hasPermissions(this, permissionString)) {
            ActivityCompat.requestPermissions(this, permissionString, 131);
        } else {
            final boolean handler = new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                    {
                        Intent startAct = new Intent(SplashActivity.this, FirstActivity.class);
                        startActivity(startAct);
                        SplashActivity.this.finish();
                    }
                    else
                    {
                        Intent startAct = new Intent(SplashActivity.this, LoginScreen.class);
                        startActivity(startAct);
                        SplashActivity.this.finish();
                    }

                }
            }, 1000);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==131)
        {
            if(grantResults!=null && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {
                final boolean handler=new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                        {
                            Intent startAct = new Intent(SplashActivity.this, Dashboard.class);
                            startActivity(startAct);
                            SplashActivity.this.finish();
                        }
                        else
                        {
                            Intent startAct = new Intent(SplashActivity.this, LoginScreen.class);
                            startActivity(startAct);
                            SplashActivity.this.finish();
                        }
                    }
                },1000);
            }
            else {
                Toast.makeText(this,"Please grant all permissions", Toast.LENGTH_SHORT).show();
                this.finish();
            }
            return;
        }
        else
        {
            Toast.makeText(this,"Something went Wrong!",Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
    }

    boolean hasPermissions(Context context, String[] permissions)
    {
        boolean hasAllPermissions=true;
        for(int i=0;i<permissions.length;i++)
        {
            String permission=permissions[i];
            if( (context.checkCallingOrSelfPermission(permission)) !=PackageManager.PERMISSION_GRANTED)
            {
                hasAllPermissions=false;
            }
        }
        return hasAllPermissions;
    }
}
