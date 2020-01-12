package com.csemaster.paylpu.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.csemaster.paylpu.Adapters.MyOrderAdapter;
import com.csemaster.paylpu.Adapters.ProductDetailsAdapter;
import com.csemaster.paylpu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrderActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    MyOrderAdapter myOrderAdapter;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference cartCollectionReference;
    ArrayList<OrderModel> orderModelArrayList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        toolbar=findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.orderRecyclerView);
        cartCollectionReference=firebaseFirestore.collection("Orders");

        cartCollectionReference
                .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty())
                        {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                            {
                                OrderModel orderModel=queryDocumentSnapshot.toObject(OrderModel.class);
                                orderModelArrayList.add(orderModel);
                            }

                            myOrderAdapter=new MyOrderAdapter(MyOrderActivity.this,orderModelArrayList);
                            myOrderAdapter.notifyDataSetChanged();
                            recyclerView.setLayoutManager(new LinearLayoutManager(MyOrderActivity.this));
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setAdapter(myOrderAdapter);
                        }
                    }
                });
    }
}
