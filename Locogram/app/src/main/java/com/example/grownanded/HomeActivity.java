package com.example.grownanded;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    String mUID;
    String MyUid;
    FirebaseUser firebaseUser;
    ImageView btn_notifications_home;

    BottomNavigationFragment bottomNavigationFragment;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MyUid = firebaseUser.getUid();

        btn_notifications_home = findViewById(R.id.btn_notifications_home);

//****************************************************************************************************************************

        // todo: bottom Toolbar menu
        Fragment bottom_fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_id);
        if (bottom_fragment instanceof Fragment){
            bottomNavigationFragment = (BottomNavigationFragment)bottom_fragment;
            bottomNavigationFragment.initializecomponents();
            overridePendingTransition(0,0);
        }

        btn_notifications_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        checkUserStatus();

    }

//****************************************************************************************************************************

    // todo : custom Methods (CheckOnlineStatus)

    private void CheckOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(MyUid);
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus" , status);
        dbRef.updateChildren(hashMap);

    }

    // todo : custom method (onResume)

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    // todo : custom method (checkUserStatus)

    private void checkUserStatus() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            mUID = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

        } else {
            startActivity(new Intent(HomeActivity.this,IntroActivity.class));
            finish();
        }
    }


    // todo : custom method (onBackPressed)

    @Override
    public void onBackPressed() {

        if(pressedTime >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press back again to Exit !", Toast.LENGTH_SHORT).show();
            pressedTime++;

        }
        overridePendingTransition(0,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        CheckOnlineStatus(timeStamp);
    }
}