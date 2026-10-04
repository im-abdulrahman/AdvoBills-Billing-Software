package com.example.grownanded;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class Intro_Screen_Adapter extends PagerAdapter {

    Context context;
    List<Intro_Screen_Item> mListScreen;

    public Intro_Screen_Adapter(Context context, List<Intro_Screen_Item> mListScreen) {
        this.context = context;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @SuppressLint("MissingInflatedId")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_intro_screen,null);

        ImageView imageSlide = layoutScreen.findViewById(R.id.IV_Intro_Image);
        TextView title = layoutScreen.findViewById(R.id.TV_Intro_Title);
        TextView description = layoutScreen.findViewById(R.id.TV_Intro_Des);
        TextView description02 = layoutScreen.findViewById(R.id.TV_Intro_Des02);
        TextView description03 = layoutScreen.findViewById(R.id.TV_Intro_Des03);
        TextView description04 = layoutScreen.findViewById(R.id.TV_Intro_Des04);
        TextView description05 = layoutScreen.findViewById(R.id.TV_Intro_Des05);


        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        description02.setText(mListScreen.get(position).getDescription02());
        description03.setText(mListScreen.get(position).getDescription03());
        description04.setText(mListScreen.get(position).getDescription04());
        description05.setText(mListScreen.get(position).getDescription05());

        imageSlide.setImageResource(mListScreen.get(position).getScreenImg());


        container.addView(layoutScreen);
        return layoutScreen;

    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
