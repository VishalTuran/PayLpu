package com.csemaster.paylpu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csemaster.paylpu.Activities.MyOrderActivity;
import com.csemaster.paylpu.Activities.OrderModel;
import com.csemaster.paylpu.R;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderHolder> {
    Context context;
    ArrayList<OrderModel> orderModelArrayList;
    ArrayList<String> itemNames;
    ArrayList<Integer> itemPrices;
    OrderItemAdapter orderItemAdapter;
    public MyOrderAdapter(Context context, ArrayList<OrderModel> orderModelArrayList) {
        this.context = context;
        this.orderModelArrayList = orderModelArrayList;
    }

    @NonNull
    @Override
    public MyOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlemyorder,parent,false);
        return new MyOrderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderHolder holder, int position) {
        holder.totalPrice.setText("Rs."+orderModelArrayList.get(position).getTotalPrice());
        holder.date.setText("On:"+orderModelArrayList.get(position).getDateTime());
        holder.orderId.setText("OrderId:"+orderModelArrayList.get(position).getOrderId());
        holder.shopName.setText("Items from "+orderModelArrayList.get(position).getShopName());

        itemNames=new ArrayList<>(orderModelArrayList.get(position).getItems().keySet());
        itemPrices=new ArrayList<>(orderModelArrayList.get(position).getItems().values());

        orderItemAdapter=new OrderItemAdapter(context,itemNames,itemPrices);
        orderItemAdapter.notifyDataSetChanged();
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setHasFixedSize(true);

        holder.recyclerView.setAdapter(orderItemAdapter);

    }

    @Override
    public int getItemCount() {
        return orderModelArrayList.size();
    }

    public class MyOrderHolder extends RecyclerView.ViewHolder {
        TextView shopName,orderId,date,totalPrice;
        RecyclerView recyclerView;
        public MyOrderHolder(@NonNull View itemView) {
            super(itemView);
            shopName=itemView.findViewById(R.id.orderShopName);
            orderId=itemView.findViewById(R.id.orderId);
            date=itemView.findViewById(R.id.orderDate);
            totalPrice=itemView.findViewById(R.id.orderTotalPrice);
            recyclerView=itemView.findViewById(R.id.singleOrderItemRV);
        }
    }
}
