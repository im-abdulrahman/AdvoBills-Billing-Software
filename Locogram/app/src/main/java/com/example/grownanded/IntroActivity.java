package com.example.grownanded;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    Intro_Screen_Adapter intro_screen_adapter;
    TabLayout tabIndicator;
    Button btnNext , btnLogin;
    int position = 0 ;
    Animation btnLogin_Animation;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth mAuth;
    private Dialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro1);

//***********************************************************************************************************     
        
        // todo: Set Ids for intro activity
        
        tabIndicator = findViewById(R.id.Intro_indicator);
        btnNext = findViewById(R.id.btn_next_intro);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin_Animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btn_login_animation);

        // todo: loading dialog box

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_loading_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

//***********************************************************************************************************
        
        //todo: Intro screen List
        
        List<Intro_Screen_Item> mList = new ArrayList<>();
        mList.add(new Intro_Screen_Item("If you are a customer !","","","","","You can find everything you need on Grow Nanded. You can see the products available at the store in your city. And you can talk to shopkeeper on message and call.",R.mipmap.intro01));
        mList.add(new Intro_Screen_Item("If you are a seller !","","","","","You can easily sell your products on Grow Nanded. Can further promote your shop or business. And can stay connected with your buyers.",R.mipmap.intro02));
        mList.add(new Intro_Screen_Item("Get Started !","","","","","Select your google account to continue ( grow nanded can generate your profile automatically )",R.mipmap.intro03));
        
        screenPager = findViewById(R.id.ViewPager_Intro);
        intro_screen_adapter = new Intro_Screen_Adapter(this,mList);
        screenPager.setAdapter(intro_screen_adapter);

        // todo : Intro screen Indicator

        tabIndicator.setupWithViewPager(screenPager);

        // todo : Button Next

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }

                // todo : visible login btn and hide next btn

                if (position == mList.size()-1){
                    loadLastScreen();
                }
            }
        });

        // todo : tabLayout change listener

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // todo : SignIn Btn click

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                        .build();
        gsc = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInMethode();
            }
        });
    }

//**********************************************************************************************************************************

    // todo: custom Method ( SignInMethode )

    private void SignInMethode() {

        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                dialog.show();
                task.getResult(ApiException.class);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e){
                dialog.dismiss();
                Toast.makeText(this, "Error ! try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                             //   Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (task.getResult().getAdditionalUserInfo().isNewUser()){

                                    String uEmail = user.getEmail();
                                    String uName = user.getDisplayName();
                                    String uId = user.getUid();
                                    String image = user.getPhotoUrl().toString();

                                    HashMap<Object, String> hashMap = new HashMap<>();

                                    hashMap.put("email",uEmail);
                                    hashMap.put("uid" , uId);
                                    hashMap.put("name" , uName);
                                    hashMap.put("onlineStatus" , "online" );
                                    hashMap.put("typingTo" , "noOne");
                                    hashMap.put("bio" , uName + " has not updated his address.");
                                    hashMap.put("image" , image);

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uId).setValue(hashMap);

                                }



                                Intent intent = new Intent(IntroActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(IntroActivity.this, "Error ! try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
         }
        
    // todo: custom Method ( loadLastScreen )

    private void loadLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        // todo : Animation login button
        btnLogin.setAnimation(btnLogin_Animation);
    }

//**********************************************************************************************************************************

    // todo : custom Methods (onStart)

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

}