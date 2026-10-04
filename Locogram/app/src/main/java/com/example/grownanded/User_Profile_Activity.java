package com.example.grownanded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grownanded.Chat_Section.Chats_Activity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class User_Profile_Activity extends AppCompatActivity {

    ImageView imageView;
    TextView nameTv , bioTv ;
    TextView postCount,FollowersCount, FollowingCount;
    LinearLayout goToFollowers, goToFollowing , btns_container;
    Button btn_msg_on_profile ,btn_follow_on_profile ;
    ImageView btn_options_on_toolbar;

    TextView AlertDialog_title;
    Dialog dialog_remove_alert;
    Button confirm,cancel;

    String hisId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
//*******************************************************************************************************

        imageView = findViewById(R.id.click_profile_image);
        nameTv = findViewById(R.id.click_profile_full_name);
        bioTv = findViewById(R.id.click_profile_bio);
        btn_msg_on_profile = findViewById(R.id.btn_msg_on_profile);
        btn_follow_on_profile = findViewById(R.id.btn_follow_on_profile);
        postCount = findViewById(R.id.post_count);
        FollowingCount = findViewById(R.id.following_count);
        FollowersCount = findViewById(R.id.followers_count);
        goToFollowing = findViewById(R.id.btn_show_following);
        goToFollowers = findViewById(R.id.btn_show_followers);
        btns_container = findViewById(R.id.Layout_userProfile_msg_follow_btns);
        btn_options_on_toolbar = findViewById(R.id.btn_options_on_toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        hisId = intent.getStringExtra("hisId");

        // todo : unfollow dialog

        dialog_remove_alert = new Dialog(this);
        dialog_remove_alert.setContentView(R.layout.layout_dialog_alert);
        dialog_remove_alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog_remove_alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        confirm = dialog_remove_alert.findViewById(R.id.btn_Logout_confirm);
        cancel = dialog_remove_alert.findViewById(R.id.btn_logOut_cancel);

        AlertDialog_title = dialog_remove_alert.findViewById(R.id.AlertDialog_title);
        AlertDialog_title.setText("Are you sure ! you want to remove");
        confirm.setText("Remove");


//*******************************************************************************************************

        // todo : custom method called 1

        checkFollow();

        checkUser();

//*******************************************************************************************************

        // todo : show user data into toolbar

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisId);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = "" + ds.child("name").getValue();
                    String bio = "" + ds.child("bio").getValue();
                    String image = "" + ds.child("image").getValue();

                    nameTv.setText(name);
                    bioTv.setText(bio);

                    try {
                        Picasso.get().load(image).placeholder(R.mipmap.profile_icon).into(imageView);
                    } catch (Exception e){
                        Picasso.get().load(R.mipmap.profile_icon).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//*******************************************************************************************************

        // todo : setOnClickListener (msg btn)

        btn_msg_on_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chats_Activity.class);
                intent.putExtra("hisId", hisId);
                startActivity(intent);
            }
        });

        // todo : setOnClickListener (follow btn)

        btn_follow_on_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String btn = btn_follow_on_profile.getText().toString();
                    if (btn.equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("Following").child(hisId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(hisId)
                                .child("Followers").child(firebaseUser.getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(User_Profile_Activity.this, "followed you msg", Toast.LENGTH_SHORT).show(); // todo sent notification code here..................
                                    }
                                });
                    } else if (btn.equals("Following")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("Following").child(hisId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(hisId)
                                .child("Followers").child(firebaseUser.getUid()).removeValue();
                    }
            }
        });

        // todo : setOnClickListener (goToFollowing)

        goToFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hisId.equals(firebaseUser.getUid())) {
                    Intent intentFollowers = new Intent(getApplicationContext(), Followed_my_Activity.class);
                    intentFollowers.putExtra("hisId", hisId);
                    startActivity(intentFollowers);
                }else {
                    Intent intentFollowing = new Intent(getApplicationContext(), Followed_list_Activity.class);
                    intentFollowing.putExtra("hisId", hisId);
                    startActivity(intentFollowing);
                }
            }
        });

        // todo : setOnClickListener (goToFollowers)

        goToFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hisId.equals(firebaseUser.getUid())) {
                    Intent intentFollowers = new Intent(getApplicationContext(), Followers_my_Activity.class);
                    intentFollowers.putExtra("hisId", hisId);
                    startActivity(intentFollowers);
                } else {
                    Intent intentFollowers = new Intent(getApplicationContext(), Followers_list_Activity.class);
                    intentFollowers.putExtra("hisId", hisId);
                    startActivity(intentFollowers);
                }
            }
        });

        // todo : setOnClickListener (btn_options_on_toolbar)

        btn_options_on_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hisId.equals(firebaseUser.getUid())){
                    Intent intent = new Intent(getApplicationContext(), Settings_Activity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }else {
                    ShowMenuOptions(v);
                }
            }
        });

//*******************************************************************************************************

        // todo : custom method called 2

        getFollowers();

    }

//*******************************************************************************************************

    // todo : custom method (ShowMenuOptions)

    private void ShowMenuOptions(View v) {
        PopupMenu popupMenu = new PopupMenu(User_Profile_Activity.this,v);
        popupMenu.getMenuInflater().inflate(R.menu.user_profile_options,popupMenu.getMenu());

        DatabaseReference reference58 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisId).child("Following");
        reference58.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    popupMenu.getMenu().findItem(R.id.option_remove_follower).setVisible(true);
                } else {
                    popupMenu.getMenu().findItem(R.id.option_remove_follower).setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.option_report){
                    Toast.makeText(User_Profile_Activity.this, "report", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.option_block){
                    Toast.makeText(User_Profile_Activity.this, "Block", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.option_send_msg){
                    Intent intent = new Intent(getApplicationContext(), Chats_Activity.class);
                    intent.putExtra("hisId", hisId);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.option_remove_follower){
                    dialog_remove_alert.show();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_remove_alert.dismiss();
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisId)
                                    .child("Following").child(firebaseUser.getUid()).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                    .child("Followers").child(hisId).removeValue();
                            dialog_remove_alert.dismiss();
                        }
                    });
                }
                if (item.getItemId() == R.id.option_copy_url){
                    Toast.makeText(User_Profile_Activity.this, "Copy Profile URL", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.option_share_profile){
                    Toast.makeText(User_Profile_Activity.this,"Send Profile URL", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    // todo : custom method (checkUser)

    private void checkUser() {
        if (hisId.equals(firebaseUser.getUid())){
           btns_container.setVisibility(View.GONE);
            btn_options_on_toolbar.setImageDrawable(getDrawable(R.drawable.ic_baseline_settings_24));
        }
    }

    // todo : custom method (checkFollow)

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(hisId).exists()){
                    btn_follow_on_profile.setText("Following");
                    btn_follow_on_profile.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    btn_follow_on_profile.setTextColor(Color.BLACK);
                } else {
                    btn_follow_on_profile.setText("Follow");
                    btn_follow_on_profile.setBackgroundColor(Color.parseColor("#5f9ea0"));
                    btn_follow_on_profile.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // todo : custom method (getFollowers)

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisId).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowersCount.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisId).child("Following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowingCount.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // todo : custom method (getPosts)

    private void getPosts(){
        // post method here
    }

}