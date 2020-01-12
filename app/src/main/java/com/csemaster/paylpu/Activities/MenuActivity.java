package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.csemaster.paylpu.Adapters.CartValueAdapter;
import com.csemaster.paylpu.Adapters.MenuAdapter;
import com.csemaster.paylpu.Modals.CartModel;
import com.csemaster.paylpu.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> foodItemNames = new ArrayList<>();
    ArrayList<String> foodItemImages = new ArrayList<>();
    ArrayList<Integer> foodItemPrices = new ArrayList<>();
    public static TextView totalItems, totalPrice, shopName;
    public static CardView cartmini;
    String name;
    int quantity;
    int price;
    String shop,imageUrl;
    String uId;
    String TAG = "Payment Error";


    String foodname, foodImage;
    int foodPrice;
    Button paymentButton;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = firebaseFirestore.collection("ShopMenus");
    CollectionReference cartCollectionReference = firebaseFirestore.collection("Cart");
    BottomSheetBehavior sheetBehavior;
    LinearLayout bottom_sheet;
    Button btn_bottom_sheet;
    ArrayList<CartModel> cartModelArrayList = new ArrayList<>();
    RecyclerView cartValueRecyclerView;
    public static CartValueAdapter cartValueAdapter;
    int finalQuantity, totalPriceInCart = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.requestLayout();
        recyclerView.getLayoutParams().height= Resources.getSystem().getDisplayMetrics().heightPixels-200;
        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        cartValueRecyclerView = findViewById(R.id.cartValueRecyclerView);
        cartmini = findViewById(R.id.cartMini);
        shopName = findViewById(R.id.shopNameInCart);
        shopName.setText(getIntent().getStringExtra("ShopName"));
        totalItems = findViewById(R.id.totalItemsInCart);
        totalPrice = findViewById(R.id.totalPriceInCart);
        paymentButton = findViewById(R.id.paymentButton);



        cartCollectionReference
                .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        finalQuantity = 0;
                        totalPriceInCart = 0;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                finalQuantity = finalQuantity + queryDocumentSnapshot.getLong("itemQuantity").intValue();
                                //int multipleFactor=queryDocumentSnapshot.getLong("itemPrice").intValue();
                                totalPriceInCart = totalPriceInCart + (queryDocumentSnapshot.getLong("itemQuantity").intValue() * queryDocumentSnapshot.getLong("itemPrice").intValue());
                                //Toast.makeText(MenuActivity.this, "FinalQuantity:" + finalQuantity, Toast.LENGTH_SHORT).show();
                            }
                            String finalQuantityString = String.valueOf(finalQuantity) + " Items |";
                            String finalPriceString = " Rs." + String.valueOf(totalPriceInCart);
                            MenuActivity.totalItems.setText(finalQuantityString);
                            MenuActivity.totalPrice.setText(finalPriceString);
                        } else {
                            totalItems.setText("No Items Present In Cart");
                            totalPrice.setText("");
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                });


        btn_bottom_sheet = findViewById(R.id.btn_bottom_sheet);
        cartmini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartCollectionReference
                        .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .whereEqualTo("shopName", getIntent().getStringExtra("ShopName"))
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    cartModelArrayList.clear();
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        name = queryDocumentSnapshot.getString("itemName");
                                        quantity = queryDocumentSnapshot.getLong("itemQuantity").intValue();
                                        price = queryDocumentSnapshot.getLong("itemPrice").intValue();
                                        shop = queryDocumentSnapshot.getString("shopName");
                                        uId = queryDocumentSnapshot.getString("uId");
                                        imageUrl=queryDocumentSnapshot.getString("itemImage");

                                        cartModelArrayList.add(new CartModel(name, quantity, price, shop, uId,imageUrl));

                                    }

                                    //totalItems.setText(String.valueOf(totalItemInCart));

                                    cartValueRecyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
                                    cartValueRecyclerView.setHasFixedSize(true);

                                    cartValueAdapter = new CartValueAdapter(MenuActivity.this, cartModelArrayList);
                                    cartValueRecyclerView.setAdapter(cartValueAdapter);
                                    cartValueAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btn_bottom_sheet.setText("Close sheet");

                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btn_bottom_sheet.setText("Expand sheet");
                }
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        btn_bottom_sheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        btn_bottom_sheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                showCart();
            }
        });

        collectionReference
                .whereEqualTo("vendorName", getIntent().getStringExtra("ShopName"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                foodname = snapshot.getString("itemName");
                                foodImage = snapshot.getString("itemUrl");
                                foodPrice = snapshot.getLong("itemPrice").intValue();

                                foodItemNames.add(foodname);
                                foodItemImages.add(foodImage);
                                foodItemPrices.add(foodPrice);
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
                            recyclerView.setHasFixedSize(true);

                            MenuAdapter menuAdapter = new MenuAdapter(foodItemNames, foodItemPrices, foodItemImages, MenuActivity.this, getIntent().getStringExtra("ShopName"));
                            recyclerView.setAdapter(menuAdapter);
                            menuAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MenuActivity.this, "No Menu Found" + queryDocumentSnapshots.isEmpty(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalPriceInCart!=0)
                {
                    Intent intent = new Intent(MenuActivity.this, PaymentActivity.class);
                    intent.putExtra("totalPrice", totalPriceInCart);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MenuActivity.this, "Your Cart is Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void showCart()
    {
        cartCollectionReference
                .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            cartModelArrayList.clear();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                name = queryDocumentSnapshot.getString("itemName");
                                quantity = queryDocumentSnapshot.getLong("itemQuantity").intValue();
                                price = queryDocumentSnapshot.getLong("itemPrice").intValue();
                                shop = queryDocumentSnapshot.getString("shopName");
                                uId = queryDocumentSnapshot.getString("uId");
                                imageUrl=queryDocumentSnapshot.getString("itemImage");

                                cartModelArrayList.add(new CartModel(name, quantity, price, shop, uId,imageUrl));

                            }

                            //totalItems.setText(String.valueOf(totalItemInCart));

                            cartValueRecyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
                            cartValueRecyclerView.setHasFixedSize(true);

                            cartValueAdapter = new CartValueAdapter(MenuActivity.this, cartModelArrayList);
                            cartValueRecyclerView.setAdapter(cartValueAdapter);
                            cartValueAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


}
