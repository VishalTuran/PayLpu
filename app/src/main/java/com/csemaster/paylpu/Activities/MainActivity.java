package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.csemaster.paylpu.Adapters.GridAdapter;
import com.csemaster.paylpu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GridView simpleGrid;
    ArrayList<String> name=new ArrayList<>();
    ArrayList<String> hostelImages=new ArrayList<>();

    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference hostelCollectionReference=firebaseFirestore.collection("Hostels");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleGrid=findViewById(R.id.simpleGridView);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        hostelCollectionReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty())
                        {
                            String hostelName,hostelImage = null;
                            for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                            {
                                hostelName=queryDocumentSnapshot.getString("name");
                                hostelImage=queryDocumentSnapshot.getString("image");
                                Log.d("DataFetched", "onSuccess: "+hostelName);
                                name.add(hostelName);
                                hostelImages.add(hostelImage);

                            }


                            GridAdapter gridAdapter=new GridAdapter(MainActivity.this,name,hostelImages);
                            simpleGrid.setAdapter(gridAdapter);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,ShopActivity.class);
                intent.putExtra("hostelName",name.get(i));
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}



//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                IntentIntegrator intentIntegrator=new IntentIntegrator(MainActivity.this);
//                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//                intentIntegrator.setCameraId(0);
//                intentIntegrator.setOrientationLocked(false);
//                intentIntegrator.setPrompt("scanning");
//                intentIntegrator.setBeepEnabled(true);
//                intentIntegrator.setBarcodeImageEnabled(true);
//                intentIntegrator.initiateScan();
//            }
//        });
