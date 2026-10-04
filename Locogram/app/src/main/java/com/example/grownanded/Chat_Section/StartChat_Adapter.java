package com.example.grownanded.Chat_Section;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grownanded.R;
import com.example.grownanded.User_Profile_Activity;
import com.example.grownanded.Users_Model;
import com.example.grownanded.HomeActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StartChat_Adapter extends RecyclerView.Adapter<StartChat_Adapter.MyHolder> {

    Context context;
    List<Users_Model> userList;

    public StartChat_Adapter (Context context, List<Users_Model> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_start_chat_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        // todo : Model get data
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
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
                Toast.makeText(context, userId + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Chats_Activity.class);
                intent.putExtra("hisId", userId);
                context.startActivity(intent);  Intent intent = new Intent(context, Chats_Activity.class);
                intent.putExtra("hisId", userId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView mImageTV;
        TextView mNameTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mImageTV = itemView.findViewById(R.id.UsersList_Image_SC);
            mNameTV = itemView.findViewById(R.id.UsersList_Name_SC);

        }
    }
}
