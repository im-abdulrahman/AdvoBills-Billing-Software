package com.example.grownanded;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StoresFragmentAdapter extends FragmentStateAdapter {

    public StoresFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new StoresTab02Fragment();
        }
        return new StoresTab01Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
