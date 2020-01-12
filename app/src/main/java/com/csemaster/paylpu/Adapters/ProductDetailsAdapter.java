package com.csemaster.paylpu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csemaster.paylpu.R;

import java.util.ArrayList;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> itemNames;
    ArrayList<Integer> itemPrices;

    public ProductDetailsAdapter(Context context, ArrayList<String> itemNames, ArrayList<Integer> itemPrices) {
        this.context = context;
        this.itemNames = itemNames;
        this.itemPrices = itemPrices;
    }

    @NonNull
    @Override
    public ProductDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vh= LayoutInflater.from(parent.getContext()).inflate(R.layout.productdetaillayout,parent,false);
        return new MyViewHolder(vh);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailsAdapter.MyViewHolder holder, int position) {
        holder.name.setText(itemNames.get(position));
        holder.price.setText("X "+String.valueOf(itemPrices.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,price;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.productDetailsName);
            price=itemView.findViewById(R.id.productDetailsPrice);
        }
    }
}
