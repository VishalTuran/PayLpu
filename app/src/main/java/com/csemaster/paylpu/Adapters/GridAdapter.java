package com.csemaster.paylpu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.csemaster.paylpu.R;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> title;
    ArrayList<String> image;

    public GridAdapter(Context context, ArrayList<String> title, ArrayList<String> hostelImages) {
        this.context = context;
        this.title=title;
        this.layoutInflater = (LayoutInflater.from(context));
        this.image=hostelImages;
    }


    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.customgridlayout,null);
        ImageView logo=(ImageView)view.findViewById(R.id.pic);
        TextView textView=view.findViewById(R.id.title);
        //logo.setImageResource(logos[i]);
        Glide.with(context).load(R.drawable.b_storefront).into(logo);
        textView.setText(title.get(i));
        return view;
    }
}

