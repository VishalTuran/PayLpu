package com.csemaster.paylpu.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.csemaster.paylpu.Adapters.ProductDetailsAdapter;
import com.csemaster.paylpu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity implements PaymentStatusListener {
    EditText editText;
    Button button;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    HashMap<String, Integer> items = new HashMap<>();
    String shopName;
    CollectionReference cartCollection = firebaseFirestore.collection("Cart");
    final String orderId = UUID.randomUUID().toString().substring(0, 14);
    ArrayList<String> itemsName=new ArrayList<>();
    ArrayList<Integer> itemsQuantity=new ArrayList<>();
    TextView shopNameTextView;
    RecyclerView productRecyclerView;
    ProductDetailsAdapter productDetailsAdapter;
    LinearLayout paymentLayout;
    ProgressBar progressBar;
    TextView totalPricepayment;
    IntentIntegrator qrScan;
    String name,upi;
    LinearLayout cartEmptyLayout;
    OrderModel orderModel = new OrderModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        button = findViewById(R.id.googlePayButton);
        shopNameTextView=findViewById(R.id.paymentShopName);
        cartEmptyLayout=findViewById(R.id.cartEmptyLayout);
        productRecyclerView=findViewById(R.id.productDetailsRV);
        paymentLayout=findViewById(R.id.paymentLayout);
        progressBar=findViewById(R.id.productDetailsProgressBar);
        totalPricepayment=findViewById(R.id.totalPricePayment);
        totalPricepayment.setText("Rs. "+String.valueOf(getIntent().getIntExtra("totalPrice",0)));

        paymentLayout.setVisibility(View.INVISIBLE);
        cartEmptyLayout.setVisibility(View.VISIBLE);

        qrScan = new IntentIntegrator(this);


        cartCollection.whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String itemName = snapshot.getString("itemName");
                                int itemQuantity = snapshot.getLong("itemQuantity").intValue();
                                shopName = snapshot.getString("shopName");
                                shopNameTextView.setText("From: "+shopName);
                                itemsName.add(itemName);
                                itemsQuantity.add(itemQuantity);
                                cartEmptyLayout.setVisibility(View.GONE);
                                paymentLayout.setVisibility(View.VISIBLE);
                            }

                            productDetailsAdapter=new ProductDetailsAdapter(PaymentActivity.this,itemsName,itemsQuantity);
                            productDetailsAdapter.notifyDataSetChanged();
                            productRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                            productRecyclerView.setHasFixedSize(true);

                            productRecyclerView.setAdapter(productDetailsAdapter);
                        }
                        else
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            paymentLayout.setVisibility(View.GONE);
                        }
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();
            }
        });
    }

    private void payUsingAllUpi(String upi,String name,int cost) {
        final EasyUpiPayment easyUpiPayment=new EasyUpiPayment.Builder()
                .with(PaymentActivity.this)
                .setPayeeVpa(upi)
                .setPayeeName(name)
                .setTransactionId(""+ Timestamp.now().getSeconds())
                .setTransactionRefId(orderId)
                .setDescription("For Food")
                .setAmount(cost+".00")
                .build();

        easyUpiPayment.startPayment();
        easyUpiPayment.setPaymentStatusListener(this);

    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        Log.d("TransactionsDetails",transactionDetails.toString());
        String status=transactionDetails.getStatus();
        if(status.equals("Success"))
        {
            orderModel.setOrderId(orderId);

            orderModel.setItems(items);
            orderModel.setShopName(shopName);
            orderModel.setTotalPrice(getIntent().getIntExtra("totalPrice", 0));
            orderModel.setTxnId(transactionDetails.getTransactionId());
            orderModel.setuId(FirebaseAuth.getInstance().getCurrentUser().getUid());

            SimpleDateFormat formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            }
            Date date = new Date();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                orderModel.setDateTime(formatter.format(date));
            }

            firebaseFirestore.collection("Orders")
                    .document(orderId)
                    .set(orderModel);

            firebaseFirestore.collection("Cart")
                    .whereEqualTo("uId",FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                                {
                                    String documnetId=queryDocumentSnapshot.getId();

                                    firebaseFirestore.collection("Cart").document(documnetId)
                                            .delete();
                                }
                            }
                        }
                    });

        }


    }

    @Override
    public void onTransactionSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(PaymentActivity.this,QRScanner.class));
    }

    @Override
    public void onTransactionSubmitted() {
        Toast.makeText(this, "Pending | Submitted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionFailed() {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartCollection.whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String itemName = snapshot.getString("itemName");
                                int itemQuantity = snapshot.getLong("itemQuantity").intValue();
                                items.put(itemName, itemQuantity);
//                                itemsName.add(itemName);
//                                itemsQuantity.add(itemQuantity);


                                shopName = snapshot.getString("shopName");
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    payUsingAllUpi(jsonObject.getString("upi"),jsonObject.getString("name"),getIntent().getIntExtra("totalPrice",0));

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}




