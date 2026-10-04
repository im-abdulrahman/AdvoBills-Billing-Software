package com.example.grownanded.Chat_Section;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.grownanded.BottomNavigationFragment;
import com.example.grownanded.HomeActivity;
import com.example.grownanded.R;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Chat_List_Activity extends AppCompatActivity {

    BottomNavigationFragment bottomNavigationFragment;
    String MyUid;

    RecyclerView recyclerView;
    DatabaseReference reference;
    FirebaseUser currentUser;
    LinearLayout empty_state_chatList_activity;
    Button btn_empty_state_chatList_activity;

    List<Chat_List_Model> chatList;
    Chat_List_Adapter adapterChat;

//*******************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // todo : set ids and inti

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        MyUid = currentUser.getUid();

        empty_state_chatList_activity = findViewById(R.id.empty_state_chatList_activity);
        btn_empty_state_chatList_activity = findViewById(R.id.btn_empty_state_chatList_activity);

        recyclerView = findViewById(R.id.RecyclerView_Chat_List);
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(MyUid);
        recyclerView.setHasFixedSize(true);
        chatList = new ArrayList<>();
        adapterChat = new Chat_List_Adapter(this,chatList);
        recyclerView.setAdapter(adapterChat);

//*******************************************************************************************************

        // todo: bottom Toolbar menu
        Fragment bottom_fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_id);
        if (bottom_fragment instanceof Fragment){
            bottomNavigationFragment = (BottomNavigationFragment)bottom_fragment;
            bottomNavigationFragment.initializecomponents();
            overridePendingTransition(0,0);
        }

        // todo : Chat list
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat_List_Model chat_list_model = ds.getValue(Chat_List_Model.class);
                    chatList.add(chat_list_model);
                }
                adapterChat.notifyDataSetChanged();
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyItemInserted(chatList.size());
                recyclerView.smoothScrollToPosition(1);
                if (chatList.size() == 0){
                    empty_state_chatList_activity.setVisibility(View.VISIBLE);
                } else {
                    empty_state_chatList_activity.setVisibility(View.GONE);
                }
                for ( int i=0 ; i <chatList.size(); i++){
                    lastmessage(chatList.get(i).getId());
                    newmessage(chatList.get(i).getId());
                    onlineStatus(chatList.get(i).getId());
                    typingStatus(chatList.get(i).getId());
                    lastTimeStamp(chatList.get(i).getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        // todo : Empty Chat list ( start chat btn )
        btn_empty_state_chatList_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat_List_Activity.this, Start_Chats_Activity.class);
                startActivity(intent);
            }
        });

    }

//*******************************************************************************************************

    // todo : custom Methods (user status)

    private void typingStatus(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String typingSts = null;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Users_Model users = ds.getValue(Users_Model.class);

                    if (users==null){
                        continue;
                    }

                    if (users.getUid().equals(userId) && users.getTypingTo().equals(currentUser.getUid())){
                        typingSts = "typing";
                    }
                }
                adapterChat.notifyDataSetChanged();
                Collections.sort(chatList, (obj1,obj2) -> obj1.timeStamp.compareTo(obj2.timeStamp));
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyItemInserted(chatList.size());
                adapterChat.setTypingMap(userId,typingSts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onlineStatus(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String onlineSts = null;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Users_Model users = ds.getValue(Users_Model.class);

                    if (users==null){
                        continue;
                    }

                    if (users.getUid().equals(userId) && users.getOnlineStatus().equals("online")){
                        onlineSts = "online";
                    }
                }
                adapterChat.notifyDataSetChanged();
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyItemInserted(chatList.size());
                adapterChat.setOnlineMap(userId,onlineSts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void newmessage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theNewMsg = null;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Chats_Model chat = ds.getValue(Chats_Model.class);

                    if (chat==null){
                        continue;
                    }

                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }

                    if (chat.getSender().equals(userId) && chat.getReceiver().equals(currentUser.getUid()) && chat.getIsSeen().equals("Delivered")){
                        theNewMsg = "new Message";
                    }
                }
                adapterChat.notifyDataSetChanged();
                Collections.sort(chatList, (obj1,obj2) -> obj1.timeStamp.compareTo(obj2.timeStamp));
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyItemInserted(chatList.size());
                adapterChat.setNewMsgMap(userId,theNewMsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastmessage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMsg = "start new chat";
                for (DataSnapshot ds : snapshot.getChildren()){
                    Chats_Model chat = ds.getValue(Chats_Model.class);

                    if (chat==null){
                        continue;
                    }

                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }

                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId) && chat.getMsgType().equals("default")){
                        theLastMsg = "Received: " + chat.getMessage();
                    }

                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId) && chat.getMsgType().equals("image")){
                        theLastMsg = "Received: JPEG Document";
                    }

                    if (chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid())){
                        theLastMsg = chat.getIsSeen();
                    }
                    if (chat.getSender().equals(userId) && chat.getReceiver().equals(currentUser.getUid()) && chat.getIsSeen().equals("Delivered")){
                        theLastMsg = "new Message";
                    }
                }
                Collections.sort(chatList, (obj1,obj2) -> obj1.timeStamp.compareTo(obj2.timeStamp));
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyDataSetChanged();
                adapterChat.setLastMsgMap(userId, theLastMsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastTimeStamp(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastTime = null;

                for (DataSnapshot ds : snapshot.getChildren()){
                    Chats_Model chat = ds.getValue(Chats_Model.class);

                    if (chat==null){
                        continue;
                    }

                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }

                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId)){
                        lastTime = chat.getTimestamp();
                    }
                    if (chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid())){
                        lastTime = chat.getTimestamp();
                    }


                }
                Collections.sort(chatList, (obj1,obj2) -> obj1.timeStamp.compareTo(obj2.timeStamp));
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyDataSetChanged();
                adapterChat.setLastTimeMap(userId, lastTime);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // todo : custom Methods (onBackPressed)

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Chat_List_Activity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }

    // todo : custom Methods (CheckOnlineStatus)

    private void CheckOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus" , status);
        dbRef.updateChildren(hashMap);

    }

    // todo : custom Methods (onStart)

    @Override
    protected void onStart() {
        CheckOnlineStatus("online");
        super.onStart();
    }

    // todo : custom Methods (onPause)

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(timeStamp);

    }

    // todo : custom Methods (onPause)

    @Override
    protected void onResume() {
        super.onResume();
    }
}