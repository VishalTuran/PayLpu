package com.csemaster.paylpu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.csemaster.paylpu.Activities.Dashboard;
import com.csemaster.paylpu.Activities.FirstActivity;
import com.csemaster.paylpu.Activities.LoginScreen;
import com.csemaster.paylpu.Activities.MapActivity;
import com.csemaster.paylpu.Activities.MyOrderActivity;
import com.csemaster.paylpu.Activities.OrderModel;
import com.csemaster.paylpu.Activities.QRActivity;
import com.csemaster.paylpu.Adapters.MyOrderAdapter;
import com.csemaster.paylpu.Modals.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentAccount extends Fragment {
    FirebaseAuth firebaseAuth;
    ImageView profilePic;
    String DebugTag="TAG";
    TextView profileName;
    Button signOut;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("UserInfo");
    LinearLayout qrDashboard,orderNow;
    CollectionReference cartCollectionReference=firebaseFirestore.collection("Orders");
    String qrUrl;
    Context context;
    TextView totalOrder;
    int totalOrderInteger;
    LinearLayout faqButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_dashboard,container,false);
        context=container.getContext();
        firebaseAuth=FirebaseAuth.getInstance();
        profileName=v.findViewById(R.id.profileNameDashboard);
        profilePic=v.findViewById(R.id.profileImageDashboard);
        signOut=v.findViewById(R.id.dashboardSignOut);
        orderNow=v.findViewById(R.id.orderNowCardButton);
        totalOrder=v.findViewById(R.id.totalOrder);
        faqButton=v.findViewById(R.id.faqButton);

        cartCollectionReference
                .whereEqualTo("uId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty())
                        {
                           totalOrderInteger=queryDocumentSnapshots.size();
                        }
                        totalOrder.setText(String.valueOf(totalOrderInteger));
                    }
                });


        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(container.getContext(), MyOrderActivity.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(context, LoginScreen.class));
                getActivity().finish();
            }
        });


        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MapActivity.class));
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        String currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        collectionReference.whereEqualTo("uId",currentUserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots!=null)
                        {
                            UserModel userModel=new UserModel();

                            for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots)
                            {
                                userModel.setName(snapshot.getString("name"));
                                qrUrl=snapshot.getString("qrCodeImageUrl");
                                profileName.setText(userModel.getName());
                            }
                        }
                    }
                });
    }
}
