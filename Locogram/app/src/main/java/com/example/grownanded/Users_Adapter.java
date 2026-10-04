package com.example.grownanded;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grownanded.Chat_Section.Chats_Activity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Users_Adapter extends RecyclerView.Adapter<Users_Adapter.MyHolder>{

    Context context;
    List<Users_Model> userList;

    public Users_Adapter(Context context, List<Users_Model> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_user_search, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        // todo : Model get data
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        String userBio = userList.get(i).getBio();
        String userId = userList.get(i).getUid();

        myHolder.mNameTV.setText(userName);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.mipmap.profile_icon)
                    .into(myHolder.mImageTV);
        }catch (Exception e){}

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myHolder.layoutMain.getVisibility()==View.GONE) {
                    myHolder.view1.setVisibility(View.VISIBLE);
                    myHolder.layoutMain.setVisibility(View.VISIBLE);
                }
                else {
                    myHolder.view1.setVisibility(View.GONE);
                    myHolder.layoutMain.setVisibility(View.GONE);
                }

            }
        });

        myHolder.layout01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, User_Profile_Activity.class);
                intent.putExtra("hisId", userId);
                context.startActivity(intent);
            }
        });

        myHolder.layout02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chats_Activity.class);
                intent.putExtra("hisId", userId);
                intent.putExtra("hisName", userName);
                intent.putExtra("hisImage", userImage);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mImageTV;
        TextView mNameTV;
        View view1;
        LinearLayout layout01 , layout02 , layout03 , layoutMain;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mImageTV = itemView.findViewById(R.id.UsersList_Image);
            mNameTV = itemView.findViewById(R.id.UsersList_Name);
            view1 = itemView.findViewById(R.id.divider_users_search_dialog01);
            layout01 = itemView.findViewById(R.id.button_users_search_profile);
            layout02 = itemView.findViewById(R.id.button_users_search_chat);
            layout03 = itemView.findViewById(R.id.button_users_search_share);
            layoutMain = itemView.findViewById(R.id.layout_more_options_user_search);

        }

    }

}
