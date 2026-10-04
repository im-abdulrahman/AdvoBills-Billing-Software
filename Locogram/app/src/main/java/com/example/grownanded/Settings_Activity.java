package com.example.grownanded;

import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Settings_Activity extends AppCompatActivity {

    LinearLayout btn_logout, btn_edit_profile;
    private Dialog dialog;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    ImageView btn_close_setting;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//***********************************************************************************************************

        // todo: Set Ids for profile activity

        btn_logout = findViewById(R.id.btn_logout);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        btn_close_setting = findViewById(R.id.toolbar_btn_back_settings);

        // todo: LogOut with google

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        // todo : logout dialog

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog_alert);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Button confirm = dialog.findViewById(R.id.btn_Logout_confirm);
        Button cancel = dialog.findViewById(R.id.btn_logOut_cancel);

        // Todo : LogOut Btn click

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogOut();
            }
        });

        // Todo : Edit Profile Btn click

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Activity.this,EditProfile_Activity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
        });

        // Todo : Close setting Btn click

        btn_close_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Activity.this,ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
        });

    }

//***********************************************************************************************************

    // Todo : Custom Method (LogOut)

    private void LogOut() {
        finish();
        gsc.signOut();
        startActivity(new Intent(getApplicationContext(), IntroActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}