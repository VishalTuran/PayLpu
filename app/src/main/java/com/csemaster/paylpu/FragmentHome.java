package com.csemaster.paylpu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csemaster.paylpu.Activities.MainActivity;
import com.csemaster.paylpu.Activities.ShopActivity;
import com.csemaster.paylpu.Adapters.GridAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    GridView simpleGrid;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> hostelImages = new ArrayList<>();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Context context;
    CollectionReference hostelCollectionReference = firebaseFirestore.collection("Hostels");

    CarouselView carouselView;
    int[] sampleImages = {R.drawable.veg_biryani, R.drawable.icecream, R.drawable.alooparatha, R.drawable.maggi, R.drawable.thali};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        simpleGrid = v.findViewById(R.id.simpleGridView);
        context = container.getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        carouselView = v.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);

        hostelCollectionReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            String hostelName, hostelImage = null;
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                hostelName = queryDocumentSnapshot.getString("name");
                                hostelImage = queryDocumentSnapshot.getString("image");
                                Log.d("DataFetched", "onSuccess: " + hostelName);
                                name.add(hostelName);
                                hostelImages.add(hostelImage);

                            }

                            GridAdapter gridAdapter = new GridAdapter(context, name, hostelImages);
                            simpleGrid.setAdapter(gridAdapter);
                        } else {
                            Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ShopActivity.class);
                intent.putExtra("hostelName", name.get(i));
                startActivity(intent);

            }
        });
        return v;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

}
