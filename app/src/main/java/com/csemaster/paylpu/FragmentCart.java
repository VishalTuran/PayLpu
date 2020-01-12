package com.csemaster.paylpu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csemaster.paylpu.Activities.OrderModel;
import com.csemaster.paylpu.Activities.QRScanner;
import com.csemaster.paylpu.Adapters.ProductDetailsAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class FragmentCart extends Fragment  implements PaymentStatusListener {
    EditText editText;
    Button button;
    String TAG = "main";
    final int UPI_PAYMENT = 0;
    int GOOGLE_PAY_REQUEST_CODE = 123;
    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
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
    Context context;
    int finalQuantity,totalPriceInCart;
    int finalPrice;
    LinearLayout emptyCartLayout;
    Context fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_payment,container,false);
        button = v.findViewById(R.id.googlePayButton);
        shopNameTextView=v.findViewById(R.id.paymentShopName);
        context=(Activity)container.getContext();

        productRecyclerView=v.findViewById(R.id.productDetailsRV);
        paymentLayout=v.findViewById(R.id.paymentLayout);
        progressBar=v.findViewById(R.id.productDetailsProgressBar);
        totalPricepayment=v.findViewById(R.id.totalPricePayment);
        totalPricepayment.setText("Rs. "+getPrice());
        fragment=context;

        emptyCartLayout=v.findViewById(R.id.cartEmptyLayout);

        paymentLayout.setVisibility(View.INVISIBLE);
        emptyCartLayout.setVisibility(View.VISIBLE);

        qrScan = IntentIntegrator.forSupportFragment(FragmentCart.this);


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
                                emptyCartLayout.setVisibility(View.GONE);
                                paymentLayout.setVisibility(View.VISIBLE);
                            }

                            productDetailsAdapter=new ProductDetailsAdapter(context,itemsName,itemsQuantity);
                            productDetailsAdapter.notifyDataSetChanged();
                            productRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            productRecyclerView.setHasFixedSize(true);

                            productRecyclerView.setAdapter(productDetailsAdapter);
                        }
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();
            }
        });

        return v;
    }


    public int getPrice()
    {
        cartCollection
                .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        finalQuantity = 0;
                        totalPriceInCart=0;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                finalQuantity = finalQuantity + queryDocumentSnapshot.getLong("itemQuantity").intValue();
                                totalPriceInCart = totalPriceInCart + (queryDocumentSnapshot.getLong("itemQuantity").intValue() * queryDocumentSnapshot.getLong("itemPrice").intValue());
                            }
                            totalPricepayment.setText("Rs. "+totalPriceInCart);
                        } else {

                            paymentLayout.setVisibility(View.GONE);
                            emptyCartLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
        return totalPriceInCart;
    }

    private void payUsingAllUpi(String upi,String name,int cost) {
        final EasyUpiPayment easyUpiPayment=new EasyUpiPayment.Builder()
                .with((Activity) context)
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
            final OrderModel orderModel = new OrderModel();
            orderModel.setOrderId(orderId);

            orderModel.setItems(items);
            orderModel.setShopName(shopName);
            orderModel.setTotalPrice(getPrice());
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
        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(context, QRScanner.class));
    }

    @Override
    public void onTransactionSubmitted() {
        Toast.makeText(context, "Pending | Submitted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionFailed() {
        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
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
                                shopName = snapshot.getString("shopName");
                            }
                        }
                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(context, "Result Not Found", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    payUsingAllUpi(jsonObject.getString("upi"),jsonObject.getString("name"),totalPriceInCart);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Toast.makeText(context, "Not Working!", Toast.LENGTH_SHORT).show();
            //super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
