package com.example.grownanded.Chat_Section;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grownanded.Followed_my_Activity;
import com.example.grownanded.R;
import com.example.grownanded.User_Profile_Activity;
import com.example.grownanded.Users_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Chat_List_Adapter extends RecyclerView.Adapter<Chat_List_Adapter.MyHolder> {

    Context context;
    List<Chat_List_Model> chatList;
    private HashMap<String, String> lastMsgMap;
    private HashMap<String, String> lastTimeMap;
    private HashMap<String, String> newMsgMap;
    private HashMap<String, String> onlineMap;
    private HashMap<String, String> typingMap;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    FirebaseUser firebaseUser;

    public Chat_List_Adapter(Context context, List<Chat_List_Model> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.lastMsgMap = new HashMap<>();
        this.lastTimeMap = new HashMap<>();
        this.newMsgMap = new HashMap<>();
        this.onlineMap = new HashMap<>();
        this.typingMap = new HashMap<>();

    }

    //*********************************************************************************************************

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_row_chat_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        // todo : firebase ids set
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // todo: getData
        String name = chatList.get(position).getName();
        String image = chatList.get(position).getImage();
        String hidId = chatList.get(position).getId();
        String lastMsg = lastMsgMap.get(hidId);
        String lastTime = lastTimeMap.get(hidId);
        String newMsg = newMsgMap.get(hidId);
        String onlineSts = onlineMap.get(hidId);
        String typingSts = typingMap.get(hidId);

        if (lastTime != null ) {
          holder.timeStamp.setVisibility(View.VISIBLE);
            // todo : time stamp
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(lastTime));
            String dateTime = DateFormat.format("hh:mm aa",cal).toString();
            holder.timeStamp.setText(dateTime);
        } else {
            holder.timeStamp.setVisibility(View.GONE);
        }

        // todo: setData last msg + typing status
        if (typingSts != null){
            holder.lastMsgTv.setText("typing...");
            holder.lastMsgTv.setTextColor(Color.parseColor("#008000"));
        } else {
            holder.lastMsgTv.setText(lastMsg);
            holder.lastMsgTv.setTextColor(Color.parseColor("#808080"));
        }

        // todo: setData new msg
        if (newMsg != null){
            holder.newMsgTv.setVisibility(View.VISIBLE);
            holder.lastMsgTv.setTextColor(Color.BLACK);
        } else {
            holder.newMsgTv.setVisibility(View.GONE);
        }

        // todo: setData online status
        if (onlineSts != null){
            holder.onlineIV.setVisibility(View.VISIBLE);
        } else {
            holder.onlineIV.setVisibility(View.GONE);
        }

        // todo: setData name user's
        holder.nameTv.setText(name);
        try {
            Picasso.get().load(image)
                    .placeholder(R.mipmap.profile_icon)
                    .into(holder.profileIV);
        }catch (Exception e){}

        // todo: itemClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chats_Activity.class);
                intent.putExtra("hisId", hidId);
                context.startActivity(intent);
            }
        });

    }

//*********************************************************************************************************

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void setLastMsgMap (String userId, String lastMsg){
        lastMsgMap.put(userId, lastMsg);
    }
    public void setLastTimeMap (String userId, String lastTime){
        lastTimeMap.put(userId, lastTime);
    }
    public void setNewMsgMap (String userId, String newMsg){
        newMsgMap.put(userId, newMsg);
    }
    public void setOnlineMap (String userId, String onlineSts){
        onlineMap.put(userId, onlineSts);
    }
    public void setTypingMap (String userId, String typingSts){
        typingMap.put(userId, typingSts);
    }

//*********************************************************************************************************

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView profileIV, onlineIV;
        TextView nameTv, lastMsgTv;
        ImageView newMsgTv;
        TextView timeStamp;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIV = itemView.findViewById(R.id.chatList_icon);
            onlineIV = itemView.findViewById(R.id.chatList_online_ind);
            nameTv = itemView.findViewById(R.id.chatList_name);
            lastMsgTv = itemView.findViewById(R.id.chatList_message);
            newMsgTv = itemView.findViewById(R.id.chatList_new_msg_ind);
            timeStamp = itemView.findViewById(R.id.chatList_new_msg_timeStamp);

        }

    }
}
