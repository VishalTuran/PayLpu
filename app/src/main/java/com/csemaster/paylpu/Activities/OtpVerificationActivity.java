package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csemaster.paylpu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {
    EditText otpNumber;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    CardView verifyButton;
    TextView resendCode;
    String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otpNumber = findViewById(R.id.otpNumber);
        verifyButton = findViewById(R.id.verifyButton);
        resendCode=findViewById(R.id.resendCode);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        mobile = intent.getStringExtra("PhoneNo");
        sendVerificationCode(mobile);


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=otpNumber.getText().toString().trim();
                if(code.isEmpty()|| code.length()<6)
                {
                    otpNumber.setError("Enter Valid Code");
                    otpNumber.requestFocus();
                    return;
                }
                verifyVerificationCode(code);
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(mobile);
            }
        });
    }

    //This method is used to create an instance of the PhoneAuthProvider and send otp to the given number.
    //The country code could also be taken as an input from user.
    private void sendVerificationCode(String mobile)
    {
        //CountDownTimer constructor takes two parameter.First is the total time for which counter will work
        //Second is the time by which the main time will decrease.
        new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long l) {
                resendCode.setText(""+l/1000);
                resendCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendCode.setText("Resend Code");
                resendCode.setEnabled(true);
            }
        }.start();
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+mobile,30, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallbacks);
    }


    //the callback to detect the verification status

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code=phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automtically
            //in this case the code will be null
            //so user has to manually entere the code

            if(code!=null)
            {
                otpNumber.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpVerificationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("onVerificationFailed","Error:"+e.getMessage());

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            //it will automatically be created when the code is sent.
            mVerificationId=s;
            Toast.makeText(OtpVerificationActivity.this,"Otp Send",Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyVerificationCode(String code)
    {
        //creating the credentials
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);

        //signin the user
        signInWithPhoneAuthCrediental(credential);
    }

    private void signInWithPhoneAuthCrediental(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isNewUser=task.getResult().getAdditionalUserInfo().isNewUser();
                        if(task.isSuccessful()){
                            //verification is Successful
                            if(!isNewUser)
                            {
                                Intent intent=new Intent(OtpVerificationActivity.this,FirstActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent=new Intent(OtpVerificationActivity.this,UserInfoActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("Mobile",mobile);
                                startActivity(intent);
                                finish();
                            }

                        }
                        else
                        {
                            //verification Unsuccessful

                            String message="Something is wrong,we will fix it soon....";
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                message="Invalid code entered";
                            }

                            Toast.makeText(OtpVerificationActivity.this,message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
