package com.example.grownanded.Chat_Section;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.grownanded.ProfileActivity;
import com.example.grownanded.R;
import com.example.grownanded.Settings_Activity;
import com.example.grownanded.Users_Adapter;
import com.example.grownanded.Users_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Start_Chats_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    StartChat_Adapter adapterUsers;
    List<Users_Model> userList;
    SearchView searchView;
    ImageView toolbar_btn_back_start_chat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chats);

        recyclerView = findViewById(R.id.RecyclerView_users_show_SC);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        userList = new ArrayList<>();

        // todo: searchView init
        searchView = findViewById(R.id.searchView_userslist_SC);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())){
                    SearchUsers(s);

                }
                else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())){
                    SearchUsers(s);

                }
                else {
                    getAllUsers();
                }
                return false;
            }
        });

        // todo: back btn
        toolbar_btn_back_start_chat = findViewById(R.id.toolbar_btn_back_start_chat);
        toolbar_btn_back_start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start_Chats_Activity.this, Chat_List_Activity.class);
                startActivity(intent);
                overridePendingTransition(0,0);            }
        });

        // todo: user list method
        getAllUsers();

    }

/******************************************************************************************************************/

    // todo : custom methods

    private void getAllUsers() {

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Users_Model modelUser = ds.getValue(Users_Model.class);

                    if (!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }

                    adapterUsers = new StartChat_Adapter(getApplicationContext(),userList);
                    recyclerView.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // todo : search query

    private void SearchUsers(String query) {

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Users_Model modelUser = ds.getValue(Users_Model.class);

                    if (!modelUser.getUid().equals(fUser.getUid())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }

                    adapterUsers = new StartChat_Adapter(getApplicationContext(),userList);
                    adapterUsers.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}