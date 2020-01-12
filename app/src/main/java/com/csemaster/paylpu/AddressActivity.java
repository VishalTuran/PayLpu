package com.csemaster.paylpu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csemaster.paylpu.Activities.FirstActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class AddressActivity extends AppCompatActivity {
    TextInputEditText houseNo,cityET,stateET,pincodeET;
    Button orderNow;
    CollectionReference collectionReference;
    LinearLayout home,office,other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        String city=getIntent().getStringExtra("city");
        String state=getIntent().getStringExtra("state");
        String pincode=getIntent().getStringExtra("pincode");
        String address=getIntent().getStringExtra("address");

        Toast.makeText(this, "City:"+address, Toast.LENGTH_SHORT).show();
        houseNo=findViewById(R.id.houseNo);
        cityET=findViewById(R.id.cityName);
        stateET=findViewById(R.id.state);
        pincodeET=findViewById(R.id.pincode);
        home=findViewById(R.id.homeLayout);
        office=findViewById(R.id.office);
        other=findViewById(R.id.other);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setBackgroundColor(Color.parseColor("#5DA399"));
                office.setBackgroundColor(Color.parseColor("#ffffff"));
                other.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });

        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setBackgroundColor(Color.parseColor("#ffffff"));
                office.setBackgroundColor(Color.parseColor("#5DA399"));
                other.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setBackgroundColor(Color.parseColor("#ffffff"));
                office.setBackgroundColor(Color.parseColor("#ffffff"));
                other.setBackgroundColor(Color.parseColor("#5DA399"));
            }
        });

        orderNow=findViewById(R.id.orderNowButton);

        collectionReference= FirebaseFirestore.getInstance().collection("UserInfo");
        if(!TextUtils.isEmpty(city))
        {
            cityET.setText(city);
        }
        if(!TextUtils.isEmpty(state))
        {
            stateET.setText(state);
        }
        if(!TextUtils.isEmpty(pincode))
        {
            pincodeET.setText(pincode);
        }
        if(!TextUtils.isEmpty(address))
        {
            houseNo.setText(address);
        }

        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(houseNo.getText().toString()) && !TextUtils.isEmpty(cityET.getText().toString())
                        && !TextUtils.isEmpty(stateET.getText().toString()) && !TextUtils.isEmpty(pincodeET.getText().toString()))
                {
                    collectionReference
                            .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots!=null)
                                    {
                                        for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                                        {
                                            String documentId=queryDocumentSnapshot.getId();

                                            HashMap<String,Object> hashMap=new HashMap<>();

                                            hashMap.put("city",cityET.getText().toString());
                                            hashMap.put("state",stateET.getText().toString());
                                            hashMap.put("pincode",pincodeET.getText().toString());
                                            hashMap.put("houseNo.",houseNo.getText().toString());

                                            collectionReference.document(documentId)
                                                    .update("address",hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            startActivity(new Intent(AddressActivity.this, FirstActivity.class));
                                                        }
                                                    });

                                        }
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(AddressActivity.this, "Fill All the details", Toast.LENGTH_SHORT).show();

                }
            }
        });





    }
}
