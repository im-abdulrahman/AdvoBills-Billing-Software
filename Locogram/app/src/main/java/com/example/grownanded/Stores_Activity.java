package com.example.grownanded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Stores_Activity extends AppCompatActivity {

    BottomNavigationFragment bottomNavigationFragment;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private StoresFragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

//***********************************************************************************************************

        // set ids for tab layout and view pager
        tabLayout = findViewById(R.id.tabLayout_stores);
        viewPager = findViewById(R.id.viewPager_stores_activity);
        tabLayout.addTab(tabLayout.newTab().setText("Followed"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.setElevation(3);

        // set id for users list recycler view

//***********************************************************************************************************

        // todo: retrieve Users list in recycler view
        

        // todo: tab layout and view pager

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new StoresFragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        // todo: bottom Toolbar menu

        Fragment bottom_fragment = getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_id);
        if (bottom_fragment instanceof Fragment){
            bottomNavigationFragment = (BottomNavigationFragment)bottom_fragment;
            bottomNavigationFragment.initializecomponents();
            overridePendingTransition(0,0);
        }

    }

//***********************************************************************************************************

    // todo : custom methods ( users List )




    // todo : custom methods ( onBackPress )

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Stores_Activity.this,HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }
}