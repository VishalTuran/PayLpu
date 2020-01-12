package com.csemaster.paylpu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CartValueAdapter  extends RecyclerView.Adapter<CartValueAdapter.CartValueViewHolder> {
    Context context;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("Cart");
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    Set<CartModel> cartModelSet;
    ArrayList<CartModel> cartModelArrayList;
    DocumentReference documentReference;
    String documnentId;
    int finalQuantity = 0;

    public CartValueAdapter(Context context, ArrayList<CartModel> cartModelArrayList) {
        this.context = context;
        //this.cartModelSet = cartModelSet;
        this.cartModelArrayList=cartModelArrayList;
        cartModelSet=new LinkedHashSet<>(cartModelArrayList);
        cartModelArrayList.clear();
        cartModelArrayList.addAll(cartModelSet);
    }

    @NonNull
    @Override
    public CartValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vh= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlecartvaluelayout,parent,false);
        return new CartValueViewHolder(vh);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartValueViewHolder holder, final int position) {
        holder.itemName.setText(cartModelArrayList.get(position).getName());
        holder.quantity.setText(String.valueOf(cartModelArrayList.get(position).getQuantity()));

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity(holder.itemName.getText().toString());
            }
        });

        Glide.with(context).load(cartModelArrayList.get(position).getImageUrl()).into(holder.itemImage);

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.valueOf(holder.quantity.getText().toString())>1)
                {
                    decreaseQuantity(holder.itemName.getText().toString());
                }
                else if(Integer.valueOf(holder.quantity.getText().toString())==1)
                {
                    deleteFromCart(holder.itemName.getText().toString());
                    cartModelArrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,cartModelArrayList.size());
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return cartModelArrayList.size();
    }

    public class CartValueViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,quantity;
        ImageView plus,minus,itemImage;


        public CartValueViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.itemNameCartValue);
            quantity=itemView.findViewById(R.id.totalItem);
            plus=itemView.findViewById(R.id.plusFab);
            minus=itemView.findViewById(R.id.minusFab);
            itemImage=itemView.findViewById(R.id.cartItemImage);
        }
    }

    private void increaseQuantity(final String itemName)
    {
        collectionReference
                .whereEqualTo("itemName",itemName)
                .whereEqualTo("uId",firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                        {
                             documnentId = queryDocumentSnapshot.getId();
                             documentReference=collectionReference.document(documnentId);
                            documentReference
                                    .update("itemQuantity", FieldValue.increment(1))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                //Toast.makeText(context, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(context, "Data Updation Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void decreaseQuantity(final String itemName)
    {
        collectionReference
                .whereEqualTo("itemName",itemName)
                .whereEqualTo("uId",firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                        {
                            documnentId = queryDocumentSnapshot.getId();
                            documentReference=collectionReference.document(documnentId);
                            documentReference
                                    .update("itemQuantity", FieldValue.increment(-1))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                //Toast.makeText(context, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(context, "Data Updation Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void deleteFromCart(final String itemName)
    {
        collectionReference
                .whereEqualTo("itemName",itemName)
                .whereEqualTo("uId",firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                        {
                            documnentId = queryDocumentSnapshot.getId();
                            documentReference=collectionReference.document(documnentId);
                            documentReference
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                //Toast.makeText(context, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(context, "Data Updation Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }



}
