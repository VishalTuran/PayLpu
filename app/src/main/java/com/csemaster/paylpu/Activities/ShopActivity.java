package com.csemaster.paylpu.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.csemaster.paylpu.Adapters.GridAdapter;
import com.csemaster.paylpu.Adapters.MenuAdapter;
import com.csemaster.paylpu.Adapters.ShopAdapter;
import com.csemaster.paylpu.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    ArrayList<String> shopNames=new ArrayList<>();
    ArrayList<String> shopImages=new ArrayList<>();
    ArrayList<String> shopRatings=new ArrayList<>();
    ArrayList<String> shoptimes=new ArrayList<>();

    RecyclerView shopRecyclerView;
    String documentId;
    String shopName;
    String shopImage;
    String shopRating;
    String shopTime;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference hostelCollectionReference =firebaseFirestore.collection("Hostels");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
//        Toast.makeText(this, "Hostel Name:"+getIntent().getStringExtra("hostelName"), Toast.LENGTH_SHORT).show();
        shopRecyclerView=findViewById(R.id.shopRecyclerView);


        hostelCollectionReference
                .whereEqualTo("name",getIntent().getStringExtra("hostelName"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty())
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                            {
                                documentId=queryDocumentSnapshot.getId();
//                                Toast.makeText(ShopActivity.this, "DocumentId:"+documentId, Toast.LENGTH_SHORT).show();
                                CollectionReference shopCollectionReference=hostelCollectionReference.document(documentId).collection("Shops");
                                shopCollectionReference
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot shopsDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if(shopsDocumentSnapshots!=null && !shopsDocumentSnapshots.isEmpty())
                                                {
                                                    for (QueryDocumentSnapshot snapshot:shopsDocumentSnapshots)
                                                    {
                                                        shopName=snapshot.getString("name");
                                                        shopImage=snapshot.getString("shopImage");
                                                        shopRating=snapshot.getString("rating");
                                                        shopTime=snapshot.getString("time");
//                                                        Toast.makeText(ShopActivity.this, "ShopName:"+shopImage, Toast.LENGTH_SHORT).show();
                                                        shopNames.add(shopName);
                                                        shopImages.add(shopImage);
                                                        shopRatings.add(shopRating);
                                                        shoptimes.add(shopTime);
                                                    }


                                                    shopRecyclerView.setLayoutManager(new LinearLayoutManager(ShopActivity.this));
                                                    shopRecyclerView.setHasFixedSize(true);

                                                    ShopAdapter shopAdapter=new ShopAdapter(shopNames,shopImages,shoptimes,shopRatings,ShopActivity.this);
                                                    shopRecyclerView.setAdapter(shopAdapter);
                                                    shopAdapter.notifyDataSetChanged();


//                                                    Toast.makeText(ShopActivity.this, "ShopName:"+shopImages.get(1), Toast.LENGTH_SHORT).show();



                                                }
                                                else
                                                {
                                                    Toast.makeText(ShopActivity.this, "Shops Data Null", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }


                        }
                        else
                        {
                            Toast.makeText(ShopActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }
}
