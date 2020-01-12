package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.csemaster.paylpu.Modals.UserModel;
import com.csemaster.paylpu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Dashboard extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ImageView profilePic;
    String DebugTag="TAG";
    TextView profileName;
    Button signOut;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("UserInfo");
    LinearLayout qrDashboard,orderNow;
    String qrUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth=FirebaseAuth.getInstance();
        profileName=findViewById(R.id.profileNameDashboard);
        profilePic=findViewById(R.id.profileImageDashboard);
        signOut=findViewById(R.id.dashboardSignOut);
        orderNow=findViewById(R.id.orderNowCardButton);

        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this,FirstActivity.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Dashboard.this,LoginScreen.class));
                finish();
            }
        });

//        qrDashboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Dashboard.this,QRActivity.class);
//                intent.putExtra("qrcode",qrUrl);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected void onStart() {
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

                            }
                            profileName.setText(userModel.getName());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedId=item.getItemId();

        if(selectedId==R.id.signout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(Dashboard.this,LoginScreen.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
