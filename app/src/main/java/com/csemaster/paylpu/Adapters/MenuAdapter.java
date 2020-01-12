package com.csemaster.paylpu.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.csemaster.paylpu.Activities.MainActivity;
import com.csemaster.paylpu.Activities.MenuActivity;
import com.csemaster.paylpu.Modals.CartModel;
import com.csemaster.paylpu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    ArrayList<String> foodItemNames = new ArrayList<>();
    ArrayList<Integer> foodItemPrices = new ArrayList<>();
    ArrayList<String> foodItemImages = new ArrayList<>();
    int quantity;
    String shopName;
    String previousShopName = "";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = firebaseFirestore.collection("Cart");
    String documnentId;

    Context context;

    public MenuAdapter(ArrayList<String> foodItemNames, ArrayList<Integer> foodItemPrices, ArrayList<String> foodItemImages, Context context, String shopName) {
        this.foodItemNames = foodItemNames;
        this.foodItemPrices = foodItemPrices;
        this.foodItemImages = foodItemImages;
        this.context = context;
        this.shopName = shopName;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menusinglelayout, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuAdapter.MenuViewHolder holder, final int position) {
        holder.foodName.setText(foodItemNames.get(position));
        holder.foodPrice.setText("Rs." + foodItemPrices.get(position));
        Glide.with(context).load(foodItemImages.get(position)).into(holder.foodImage);

        collectionReference
                .whereEqualTo("itemName", foodItemNames.get(position))
                .whereEqualTo("uId", firebaseAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots != null) {
                            holder.addToCartButton.setText("✔ Added to Cart");
                            holder.addToCartButton.setTextColor(Color.GREEN);
                            holder.addToCartButton.setClickable(false);
                        } else {
                            holder.addToCartButton.setText("Add to Cart");
                            holder.addToCartButton.setTextColor(Color.RED);
                            holder.addToCartButton.setClickable(true);
                        }
                    }
                });

        collectionReference
                .whereEqualTo("uId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                previousShopName=queryDocumentSnapshot.getString("shopName");
                                MenuActivity.shopName.setText(previousShopName);
                            }

                        }

                    }
                });

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference
                        .whereEqualTo("uId", firebaseAuth.getCurrentUser().getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots != null) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        previousShopName=queryDocumentSnapshot.getString("shopName");
                                        MenuActivity.shopName.setText(previousShopName);
                                    }

                                }

                            }
                        });

                if(!MenuActivity.shopName.getText().toString().equals(shopName) && !MenuActivity.totalItems.getText().equals("No Items Present In Cart"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Confirm").setMessage("Some food items are already present in your cart.Do you want to start fresh?")
                            .setPositiveButton("Yes,Start Fresh", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   collectionReference
                                           .whereEqualTo("shopName",previousShopName)
                                           .get()
                                           .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                               @Override
                                               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (!queryDocumentSnapshots.isEmpty())
                                                    {
                                                        for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                                                        {
                                                            documnentId=queryDocumentSnapshot.getId();
                                                            collectionReference.document(documnentId).delete();
                                                        }
                                                    }
                                               }
                                           });


                                    holder.addToCartButton.setText("✔ Added to Cart");
                                    holder.addToCartButton.setTextColor(Color.GREEN);
                                    holder.addToCartButton.setClickable(false);
                                    addToCart(shopName, foodItemNames.get(position), foodItemPrices.get(position), quantity, foodItemImages.get(position));
                                    MenuActivity.shopName.setText(shopName);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                }
                else
                {
                    holder.addToCartButton.setText("✔ Added to Cart");
                    holder.addToCartButton.setTextColor(Color.GREEN);
                    holder.addToCartButton.setClickable(false);
                    addToCart(shopName, foodItemNames.get(position), foodItemPrices.get(position), quantity, foodItemImages.get(position));
                    MenuActivity.shopName.setText(shopName);

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return foodItemNames.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, totalItem;
        ImageView foodImage;
        Button addToCartButton;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodItemName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            addToCartButton = itemView.findViewById(R.id.addtoCartButton);



        }
    }

    void addToCart(String shopName, String itemName, Integer itemPrice, int itemQuantity, String itemImage) {
        Map<String, Object> cart = new HashMap<>();
        cart.put("shopName", shopName);
        cart.put("itemName", itemName);
        cart.put("itemPrice", itemPrice);
        cart.put("itemQuantity", 1);
        cart.put("uId", firebaseAuth.getCurrentUser().getUid());
        cart.put("itemImage", itemImage);
        collectionReference.add(cart)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(context, "Data Successfully Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "Exception Occured:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
