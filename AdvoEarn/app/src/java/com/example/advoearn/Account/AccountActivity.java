package com.example.advoearn.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

import com.example.advoearn.BottomNavigationFragment;
import com.example.advoearn.Home.HomeActivity;
import com.example.advoearn.Intro.IntroActivity;
import com.example.advoearn.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    BottomNavigationFragment bottomNavigationFragment;

    TextView user_name, user_email;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView profile_image;
    LinearLayout btn_logout;
    private Dialog LogOutDialog;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;

    String MyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // todo: Set Ids for profile activity

        user_email = findViewById(R.id.profile_email);
        btn_logout = findViewById(R.id.Acc_btn_logout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        MyID = user.getUid();

        // todo: bottom Toolbar menu
        Fragment bottom_fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_id);
        if (bottom_fragment instanceof Fragment){
            bottomNavigationFragment = (BottomNavigationFragment)bottom_fragment;
            bottomNavigationFragment.initializeComponents();
            overridePendingTransition(0,0);
        }

        // todo : logout dialog

        // adsWatchDialog
        LogOutDialog = new Dialog(this);
        LogOutDialog.setContentView(R.layout.layout_dialog_alert);
        LogOutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LogOutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView alertimage_LogOutDialog = LogOutDialog.findViewById(R.id.alertDialog_imageView);
        TextView alertText_LogOutDialog = LogOutDialog.findViewById(R.id.alertDialog_alertText);
        TextView confirm_LogOutDialog = LogOutDialog.findViewById(R.id.alertDialog_WatchAgainText);
        TextView cancel_LogOutDialog = LogOutDialog.findViewById(R.id.alertDialog_noThanksText);
        confirm_LogOutDialog.setText("Log out");
        alertText_LogOutDialog.setText("Are you sure, you want to logout !");
        alertimage_LogOutDialog.setImageResource(R.drawable.logout_icon);

        // Todo : LogOut Btn click

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutDialog.show();
            }
        });

        cancel_LogOutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutDialog.dismiss();
            }
        });

        confirm_LogOutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                gsc = GoogleSignIn.getClient(getApplicationContext(), gso);
                gsc.signOut();
                startActivity(new Intent(getApplicationContext(), IntroActivity.class));
            }
        });

        // todo: Profile data set from firebase

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.profile_full_name);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String image = "" + ds.child("image").getValue();
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();

                    user_name.setText(name);
                    user_email.setText(email);
                    Picasso.get().load(image).into(profile_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//***********************************************************************************************************

    }

    // todo : custom method (onBackPressed)

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }
}