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

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    Context context;
    ArrayList<String> itemNames;
    ArrayList<Integer> itemQuantity;

    public OrderItemAdapter(Context context, ArrayList<String> itemNames, ArrayList<Integer> itemQuantity) {
        this.context = context;
        this.itemNames = itemNames;
        this.itemQuantity = itemQuantity;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.singleorderitem,parent,false);
        return new OrderItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        holder.itemPrice.setText("x"+itemQuantity.get(position));
        holder.itemName.setText(itemNames.get(position));
    }

    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,itemPrice;
        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.orderItemName);
            itemPrice=itemView.findViewById(R.id.orderItemQuantity);
        }
    }
}
