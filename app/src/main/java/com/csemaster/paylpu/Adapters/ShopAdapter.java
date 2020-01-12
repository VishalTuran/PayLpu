package com.csemaster.paylpu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.csemaster.paylpu.Activities.MenuActivity;
import com.csemaster.paylpu.R;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    ArrayList<String> shopNames=new ArrayList<>();
    ArrayList<String> shopImages=new ArrayList<>();
    ArrayList<String> shopTimeTaken=new ArrayList<>();
    ArrayList<String> shopRating=new ArrayList<>();
    Context context;

    public ShopAdapter(ArrayList<String> shopNames, ArrayList<String> shopImages, ArrayList<String> shopTimeTaken, ArrayList<String> shopRating, Context context) {
        this.shopNames = shopNames;
        this.shopImages = shopImages;
        this.shopTimeTaken = shopTimeTaken;
        this.shopRating = shopRating;
        this.context = context;
    }

    @NonNull
    @Override
    public ShopAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vh= LayoutInflater.from(parent.getContext()).inflate(R.layout.shopsinglelayout,parent,false);
        return new ShopViewHolder(vh);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ShopViewHolder holder, final int position) {
        holder.name.setText(shopNames.get(position));
        holder.rating.setText(shopRating.get(position));
        holder.time.setText(shopTimeTaken.get(position));
        Glide.with(context).load(shopImages.get(position)).into(holder.image);
        holder.shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MenuActivity.class);
                intent.putExtra("ShopName",shopNames.get(position));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return shopImages.size();
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView name,rating,time;
        ImageView image;
        CardView shop;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.shopName);
            rating= itemView.findViewById(R.id.shopRating);
            time= itemView.findViewById(R.id.timetoDeliver);
            image= itemView.findViewById(R.id.shopImage);
            shop=itemView.findViewById(R.id.shopSingleItem);
        }
    }
}
