package com.example.grownanded.Chat_Section;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grownanded.EditProfile_Activity;
import com.example.grownanded.HomeActivity;
import com.example.grownanded.R;
import com.example.grownanded.User_Profile_Activity;
import com.example.grownanded.Users_Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class Chats_Activity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv, btn_options;
    Dialog loading_dialog , confirmDialog;

    TextView nameTv , userStatus;
    EditText messageEt;
    ImageView sentChatBtn , btn_sentVoice_on_chat_page;
    ImageView btn_show_share_options;
    LinearLayout emptyState, options_layout, closeOptionsLayout;
    LinearLayout chatList_shareOp_camera, chatList_shareOp_gallery;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid, hisName, hisImage;
    String MyUid;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<Chats_Model> chatList;
    Chats_Adapter adapterChat;

    //permissions
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    // array of permission to be request
    String cameraPermission[];
    String storagePermission[];

    private ArrayList<Uri> imagesUri;
    private int count = 0;
    Uri image_uri = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
//*******************************************************************************************************

        // todo : set ids for chat activity
        toolbar = findViewById(R.id.chat_page_toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.RecyclerView_users_chat_msg);
        profileIv = findViewById(R.id.chat_toolbar_image);
        nameTv = findViewById(R.id.chat_toolbar_name);
        userStatus = findViewById(R.id.chat_toolbar_status);
        messageEt = findViewById(R.id.sendMsg_Edit_text);
        sentChatBtn = findViewById(R.id.btn_sentChat_on_chat_page);
        btn_options = findViewById(R.id.btn_options_on_chat_toolbar);
        emptyState = findViewById(R.id.empty_state_chats_activity);
        btn_sentVoice_on_chat_page = findViewById(R.id.btn_sentVoice_on_chat_page);
        btn_show_share_options = findViewById(R.id.btn_linearLayout_chatList_share_options);
        options_layout = findViewById(R.id.linearLayout_chatList_share_options);
        closeOptionsLayout = findViewById(R.id.btn_close_linearLayout_chatList_share_options);
        chatList_shareOp_camera = findViewById(R.id.chatList_shareOp_camera);
        chatList_shareOp_gallery = findViewById(R.id.chatList_shareOp_gallery);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisId");
        hisName = intent.getStringExtra("hisName");
        hisImage = intent.getStringExtra("hisImage");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");

        // init array of permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imagesUri = new ArrayList<>();

        // loading dialog
        loading_dialog = new Dialog(this);
        loading_dialog.setContentView(R.layout.layout_loading_dialog);
        loading_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

//*******************************************************************************************************

        // todo : btn show / hide options layout

        btn_show_share_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (options_layout.getVisibility()==View.GONE)
                    showOptionsLayout();
                else hideOptionsLayout();
            }
        });
        closeOptionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsLayout();
            }
        });
        messageEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                options_layout.setVisibility(View.GONE);
                return false;
            }
        });

        // todo : layout for recycler view

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // todo : show user data into toolbar

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();
                    String typingTo = "" + ds.child("typingTo").getValue();

                    nameTv.setText(name);

                    if (typingTo.equals(MyUid)){
                        userStatus.setText("typing...");
                    }
                    else {
                        String onlineStatus = "" + ds.child("onlineStatus").getValue();

                        if (onlineStatus.equals("online")){
                            userStatus.setText(onlineStatus);
                        } else {
                            // time stamp

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                            userStatus.setText("Last seen at: " + dateTime);
                        }
                    }

                    try {
                        Picasso.get().load(image).placeholder(R.mipmap.profile_icon).into(profileIv);
                    } catch (Exception e){
                        Picasso.get().load(R.mipmap.profile_icon).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // todo : sent chat btn or editText

        sentChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options_layout.setVisibility(View.GONE);
                String message = messageEt.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(Chats_Activity.this, "Cannot sent the empty message", Toast.LENGTH_SHORT).show();
                } 
                else {
                    SendMessage(message);
                }
                messageEt.setText("");
            }

        });

        // todo : onClick toolbar

        nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options_layout.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), User_Profile_Activity.class);
                intent.putExtra("hisId", hisUid);
                startActivity(intent);
            }
        });

        // todo : onClick menu options

        btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options_layout.setVisibility(View.GONE);
                ShowMenuOptions(v);
            }
        });

        // todo : chat edit text addTextChangedListener

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    CheckTypingStatus("noOne");
                    btn_sentVoice_on_chat_page.setVisibility(View.VISIBLE);
                    sentChatBtn.setVisibility(View.GONE);
                }
                else {
                    CheckTypingStatus(hisUid);
                    btn_sentVoice_on_chat_page.setVisibility(View.GONE);
                    sentChatBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // todo select onclick images for share

        chatList_shareOp_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsLayout();
                if (!checkCameraPermission()){
                    requestCameraPermission();
                } else {
                    PickFromCamera();
                }
            }
        });

        chatList_shareOp_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsLayout();
                if (!checkStoragePermission()){
                    requestStoragePermission();
                } else {
                    PickFromGallery();
                }
            }
        });

//**************************************************************************************

        // todo : custom method called here

        readMessages();

        seenMessages();

    }

//*******************************************************************************************************

    // todo : custom method (ShowMenuOptions)

    private void ShowMenuOptions(View v) {
        PopupMenu popupMenu = new PopupMenu(Chats_Activity.this,v);
        popupMenu.getMenuInflater().inflate(R.menu.chat_screen_options,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.chat_option_view_profile){
                    Intent intent = new Intent(getApplicationContext(), User_Profile_Activity.class);
                    intent.putExtra("hisId", hisUid);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.chat_option_clear_chat){
                    Toast.makeText(Chats_Activity.this, "02", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.chat_option_block){
                    Toast.makeText(Chats_Activity.this, "03", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.chat_option_report){
                    Toast.makeText(Chats_Activity.this, "04", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    // todo : custom Methods (readMessages)

    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Chats_Model chat = ds.getValue(Chats_Model.class);

                    if (chat.getReceiver().equals(MyUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", "Seen");
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // todo : custom Methods (readMessages)

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    Chats_Model chat = ds.getValue(Chats_Model.class);

                    if (chat.getReceiver().equals(MyUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(MyUid)) {
                        chatList.add(chat);
                    }

                    adapterChat = new Chats_Adapter(Chats_Activity.this, chatList);
                    adapterChat.notifyDataSetChanged();
                    adapterChat.notifyItemRangeInserted(chatList.size(), chatList.size());
                    adapterChat.notifyItemRemoved(chatList.size());
                    adapterChat.notifyItemChanged(chatList.size());
                    adapterChat.notifyItemInserted(chatList.size());
                    recyclerView.setAdapter(adapterChat);
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                }
                if (chatList.size()==0){

                    emptyState.setVisibility(View.VISIBLE);

                } else {
                    emptyState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // todo : custom Methods (SendMessage)

    private void SendMessage(String message) {

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("ChatList").child(MyUid).child(hisUid);
        DatabaseReference databaseReference4 = firebaseDatabase.getReference("ChatList").child(hisUid).child(MyUid);

        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", MyUid);
        hashMap.put("receiver" , hisUid);
        hashMap.put("message" , message);
        hashMap.put("timestamp" , timeStamp);
        hashMap.put("isSeen" , "Delivered");
        hashMap.put("deleteForMe" , "false");
        hashMap.put("deleteForHis" , "false");
        hashMap.put("emojiMy" , "null");
        hashMap.put("emojiHis" , "null");
        hashMap.put("msgType" , "default");
        databaseReference2.child("Chats").push().setValue(hashMap);

        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap<String,Object> hashMap1 = new HashMap<>();
                    hashMap1.put("timeStamp" , timeStamp);
                    hashMap1.put("lastMsg" , message);
                    databaseReference3.updateChildren(hashMap1);                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap<String,Object> hashMap2 = new HashMap<>();
                    hashMap2.put("timeStamp" , timeStamp);
                    hashMap2.put("lastMsg" , message);
                    databaseReference4.updateChildren(hashMap2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(MyUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();

                    DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(hisUid).child(MyUid);
                    chatRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatRef2.child("id").setValue(MyUid);
                                chatRef2.child("name").setValue(name);
                                chatRef2.child("image").setValue(image);
                                chatRef2.child("lastMsg").setValue(message);
                                chatRef2.child("timeStamp").setValue(timeStamp);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(MyUid).child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                    chatRef1.child("name").setValue(hisName);
                    chatRef1.child("image").setValue(hisImage);
                    chatRef1.child("lastMsg").setValue(message);
                    chatRef1.child("timeStamp").setValue(timeStamp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sentImages(String uri){

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("ChatList").child(MyUid).child(hisUid);
        DatabaseReference databaseReference4 = firebaseDatabase.getReference("ChatList").child(hisUid).child(MyUid);

        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", MyUid);
        hashMap.put("receiver" , hisUid);
        hashMap.put("message" , uri);
        hashMap.put("timestamp" , timeStamp);
        hashMap.put("isSeen" , "Delivered");
        hashMap.put("deleteForMe" , "false");
        hashMap.put("deleteForHis" , "false");
        hashMap.put("emojiMy" , "null");
        hashMap.put("emojiHis" , "null");
        hashMap.put("msgType" , "image");
        databaseReference2.child("Chats").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loading_dialog.dismiss();
            }
        });

        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap<String,Object> hashMap1 = new HashMap<>();
                    hashMap1.put("timeStamp" , timeStamp);
                    hashMap1.put("lastMsg" , "image");
                    databaseReference3.updateChildren(hashMap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap<String,Object> hashMap2 = new HashMap<>();
                    hashMap2.put("timeStamp" , timeStamp);
                    hashMap2.put("lastMsg" , "image");
                    databaseReference4.updateChildren(hashMap2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(MyUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();

                    DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(hisUid).child(MyUid);
                    chatRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatRef2.child("id").setValue(MyUid);
                                chatRef2.child("name").setValue(name);
                                chatRef2.child("image").setValue(image);
                                chatRef2.child("lastMsg").setValue("Image");
                                chatRef2.child("timeStamp").setValue(timeStamp);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(MyUid).child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                    chatRef1.child("name").setValue(hisName);
                    chatRef1.child("image").setValue(hisImage);
                    chatRef1.child("lastMsg").setValue("Image");
                    chatRef1.child("timeStamp").setValue(timeStamp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // todo : custom Methods (showOptionsLayout + hideOptionsLayout)

    private void showOptionsLayout(){
        Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_sheet_in);
        options_layout.startAnimation(animator);
        options_layout.setVisibility(View.VISIBLE);
    }

    private void hideOptionsLayout(){
        Animation animator = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_sheet_out);
        options_layout.startAnimation(animator);
        options_layout.setVisibility(View.GONE);
    }

    // todo : custom Methods (checkUserStatus)

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!= null){
            MyUid = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID" , MyUid);
            editor.apply();

        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    // todo : custom Methods (CheckTypingStatus)

    private void CheckTypingStatus(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("typingTo" , typing);
        dbRef.updateChildren(hashMap);

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
        checkUserStatus();
        CheckOnlineStatus("online");
        super.onStart();
    }

    // todo : custom Methods (onPause)

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(timeStamp);
        CheckTypingStatus("noOne");
        userRefForSeen.removeEventListener(seenListener);
    }

    // todo : custom Methods (onPause)

    @Override
    protected void onResume() {
        CheckOnlineStatus("online");
        super.onResume();
    }


    // todo : Share Images ******************************************************

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(Chats_Activity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
        }
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(Chats_Activity.this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(Chats_Activity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //todo : permission Enabled
                        PickFromCamera();
                    }
                    else {
                        //todo : permission denied
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //todo : permission Enabled
                        PickFromGallery();
                    }
                    else {
                        //todo : permission denied
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();

                    }
                }
            } break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                if (data != null){
                    if (data.getClipData() != null){
                        int count = data.getClipData().getItemCount();
                        for (int i =0 ; i < count ; i++){
                            imagesUri.add(data.getClipData().getItemAt(i).getUri());
                        }
                    } else {
                        imagesUri.add(data.getData());
                    }
                    compressImages();
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                compressImagesCamera();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void compressImages(){
        loading_dialog.show();
        for (int i = 0 ; i < imagesUri.size() ; i++){
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imagesUri.get(i));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG,10,stream);
                byte[] imageByte = stream.toByteArray();
                UploadImage(imageByte);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void compressImagesCamera(){
        loading_dialog.show();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG,10,stream);
                byte[] imageByte = stream.toByteArray();
                UploadImage(imageByte);

            } catch (Exception e){
                e.printStackTrace();
        }
    }

    private void UploadImage(byte[] imageByte) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("ChatImages")
                .child("images"+ System.currentTimeMillis()+".jpg");
        storageReference.putBytes(imageByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        sentImages(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading_dialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading_dialog.dismiss();
                Toast.makeText(Chats_Activity.this, "error ! try again ...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);
    }

    private void PickFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);

        if (imagesUri != null){
            imagesUri.clear();
        }
    }

    // todo : Share Images ******************************************************


}