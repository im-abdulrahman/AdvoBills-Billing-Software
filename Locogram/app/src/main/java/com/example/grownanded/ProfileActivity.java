package com.example.grownanded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    BottomNavigationFragment bottomNavigationFragment;

    TextView user_name, user_email, user_bio;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView profile_image;
    ImageView btn_settings;
    TextView postCount,FollowersCount, FollowingCount;
    LinearLayout goToFollowers, goToFollowing;

    String hisId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

// todo: bottom Toolbar menu *****************************************************
        Fragment bottom_fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_id);
        if (bottom_fragment instanceof Fragment){
            bottomNavigationFragment = (BottomNavigationFragment)bottom_fragment;
            bottomNavigationFragment.initializecomponents();
            overridePendingTransition(0,0);
        }
// todo: bottom Toolbar menu *****************************************************

//***********************************************************************************************************

        // todo: Set Ids for profile activity

        user_email = findViewById(R.id.profile_email);
        btn_settings = findViewById(R.id.btn_settings);

        postCount = findViewById(R.id.post_count);
        FollowingCount = findViewById(R.id.following_count);
        FollowersCount = findViewById(R.id.followers_count);

        user = FirebaseAuth.getInstance().getCurrentUser();
        hisId = user.getUid();

        // todo: Btn Setting click

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Settings_Activity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
        });

        // todo: Profile data set from firebase

        goToFollowing = findViewById(R.id.btn_show_following);
        goToFollowers = findViewById(R.id.btn_show_followers);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.profile_full_name);
        user_bio = findViewById(R.id.profile_bio);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String bio = "" + ds.child("bio").getValue();
                    String image = "" + ds.child("image").getValue();
                    String name = "" + ds.child("name").getValue();
                    String hisUid = "" + ds.child("uid").getValue();

                    user_bio.setText(bio);
                    user_name.setText(name);

                    Picasso.get().load(image).into(profile_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//***********************************************************************************************************

        // Todo : Show user profile data

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {

            String Email = account.getEmail();
            user_email.setText(Email);
        }

        // todo : setOnClickListener (goToFollowing)

        goToFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFollowing = new Intent(getApplicationContext(), Followed_my_Activity.class);
                intentFollowing.putExtra("hisId", hisId);
                startActivity(intentFollowing);
            }
        });

        // todo : setOnClickListener (goToFollowers)

        goToFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFollowers = new Intent(getApplicationContext(), Followers_my_Activity.class);
                intentFollowers.putExtra("hisId", hisId);
                startActivity(intentFollowers);
            }
        });

        // todo : custom method called (getFollowers)

        getFollowers();

    }


//***********************************************************************************************************

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

    // todo : custom method (onBackPressed)

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }
}