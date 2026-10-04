package com.example.grownanded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Followers_list_Activity extends AppCompatActivity {

    String id;

    RecyclerView recyclerView;
    Follower_list_adapter follower_list_adapter;
    List<Users_Model> userList ;
    List<String> idList;

    ImageView imageViewProfile;
    TextView nameTv, titleTv;

    TextView empty_Screen_alert ;
    LinearLayout empty_screen , usersList_screen;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);

        titleTv = findViewById(R.id.Tv_count_FollowList);
        nameTv = findViewById(R.id.FollowList_Toolbar_name);
        imageViewProfile = findViewById(R.id.FollowList_Toolbar_Image);

        empty_screen = findViewById(R.id.empty_screen_followers_activity);
        usersList_screen = findViewById(R.id.followers_recycler_layout);
        empty_Screen_alert = findViewById(R.id.empty_screen_TV_followers_activity);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // todo

        Intent intent = getIntent();
        id = intent.getStringExtra("hisId");

        recyclerView = findViewById(R.id.RecyclerView_Follow_List);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        idList = new ArrayList<>();

        follower_list_adapter = new Follower_list_adapter(this,userList);
        recyclerView.setAdapter(follower_list_adapter);

//*******************************************************************************************************

        // todo : show user data into toolbar

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(id);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();

                    nameTv.setText(name);
                    try {
                        Picasso.get().load(image).placeholder(R.mipmap.profile_icon).into(imageViewProfile);
                    } catch (Exception e) {
                        Picasso.get().load(R.mipmap.profile_icon).into(imageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//*******************************************************************************************************

        getFollowers();

    }

//*************************************************************************************************************

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(id).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    idList.add(ds.getKey());
                        titleTv.setText(snapshot.getChildrenCount() + " Followers");
                }

                if (snapshot.getChildrenCount()==0){
                    empty_screen.setVisibility(View.VISIBLE);
                    usersList_screen.setVisibility(View.GONE);
                    empty_Screen_alert.setText("Followers list is empty !");
                }

                showUser();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void  showUser(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Users_Model user = ds.getValue(Users_Model.class);
                    for (String id : idList){
                        if (user.getUid().equals(id)){
                            userList.add(user);
                        }
                    }
                }
                follower_list_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}