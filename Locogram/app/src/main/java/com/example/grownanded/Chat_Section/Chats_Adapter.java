package com.example.grownanded.Chat_Section;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grownanded.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Chats_Adapter extends RecyclerView.Adapter<Chats_Adapter.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chats_Model> chatList;
    FirebaseUser fUser;
    FirebaseDatabase firebaseDatabase;

//*****************************************************************************************************************

    public Chats_Adapter(Context context, List<Chats_Model> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_row_chat_right, parent, false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }
    
//*****************************************************************************************************************
    
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        // todo : get data
        String message = chatList.get(i).getMessage();
        String timeStamp = chatList.get(i).getTimestamp();
        String sender = chatList.get(i).getSender();
        String msgType = chatList.get(i).getMsgType();

        // todo : isDelete condition check
        if (chatList.get(i).getSender().equals(fUser.getUid()) && chatList.get(i).getDeleteForMe().equals("true"))
        {
                holder.messageTv.setVisibility(View.GONE);
        }
        if (chatList.get(i).getReceiver().equals(fUser.getUid()) && chatList.get(i).getDeleteForHis().equals("true"))
        {
            holder.messageTv.setVisibility(View.GONE);
        }

        // todo : time stamp
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd MMMM yyyy - hh:mm aa",cal).toString();

        // todo continue ( set data type image or text ) .......................................................................................

        // todo : set data
        if (msgType.equals("default")){
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.Image_Layout.setVisibility(View.GONE);
            holder.messageTv.setText(message);
        } else if (msgType.equals("image")){
            holder.Image_Layout.setVisibility(View.VISIBLE);
            holder.messageTv.setVisibility(View.GONE);
            try {
                Picasso.get().load(message)
                        .placeholder(R.color.white)
                        .into(holder.messageIv);
            }catch (Exception e){}
        } else if (msgType.equals("audio")){
            holder.messageTv.setVisibility(View.GONE);
            holder.Image_Layout.setVisibility(View.GONE);
            Toast.makeText(context, "audio hai", Toast.LENGTH_SHORT).show();
        }

        holder.timeTv.setText(dateTime);

        // todo : set seen / delivered
        if (i == chatList.size() -1){
            if (chatList.get(i).getIsSeen().equals("Seen")){
                holder.isSeenTv.setText("Seen");
            }else {
                holder.isSeenTv.setText("Delivered");
            }
        } else {
            holder.isSeenTv.setVisibility(View.GONE);
        }

        // todo : item click show options

        holder.messageTv.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public boolean onLongClick(View v) {

               String newMessage = holder.messageTv.getText().toString();
               String newTimestamp = holder.timeTv.getText().toString();

                // todo : delete msg dialog
                Dialog deleteDialog = new Dialog(v.getRootView().getContext());
                deleteDialog.setContentView(LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.layout_2_buttons_dialog,null));
                deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                TextView delete_msg_dialog_title,delete_msg_dialog_option1, delete_msg_dialog_option2,delete_msg_dialog_option3;
                LinearLayout btn1_2btn_dialog, btn2_2btn_dialog, btn3_2btn_dialog;
                View view;
                delete_msg_dialog_title = deleteDialog.findViewById(R.id.TV_EditAlertBox_2btn_dialog);
                delete_msg_dialog_option1 = deleteDialog.findViewById(R.id.TV_option1_2btn_dialog);
                delete_msg_dialog_option2 = deleteDialog.findViewById(R.id.TV_option2_2btn_dialog);
                delete_msg_dialog_option3 = deleteDialog.findViewById(R.id.TV_option3_2btn_dialog);
                btn1_2btn_dialog = deleteDialog.findViewById(R.id.btn1_2btn_dialog);
                btn2_2btn_dialog = deleteDialog.findViewById(R.id.btn2_2btn_dialog);
                btn3_2btn_dialog = deleteDialog.findViewById(R.id.btn3_2btn_dialog);
                view = deleteDialog.findViewById(R.id.view_option3_2btn_dialog);
                delete_msg_dialog_title.setText("Delete message ?");
                delete_msg_dialog_option1.setText("Delete for everyone");
                delete_msg_dialog_option2.setText("Delete for me");
                delete_msg_dialog_option3.setText("Cancel");

                // todo : receive msg options dialog
                Dialog receivedOptionsDialog = new Dialog(v.getRootView().getContext());
                receivedOptionsDialog.setContentView(LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.layout_received_message_options,null));
                receivedOptionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    receivedOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                LinearLayout ChatLP_emoji1, ChatLP_emoji2, ChatLP_emoji3, ChatLP_emoji4, ChatLP_emoji5, ChatLP_emoji6, ChatLP_emoji7;
                LinearLayout ChatLP_copy_btn, ChatLP_share_btn, ChatLP_reply_btn,ChatLP_dlt_btn;
                TextView ChatLP_date, ChatLP_msg_received ;
                ChatLP_copy_btn = receivedOptionsDialog.findViewById(R.id.ChatLP_copy_btn);
                ChatLP_share_btn = receivedOptionsDialog.findViewById(R.id.ChatLP_share_btn);
                ChatLP_reply_btn = receivedOptionsDialog.findViewById(R.id.ChatLP_reply_btn);
                ChatLP_dlt_btn = receivedOptionsDialog.findViewById(R.id.ChatLP_dlt_btn);
                ChatLP_date = receivedOptionsDialog.findViewById(R.id.ChatLP_date);
                ChatLP_msg_received = receivedOptionsDialog.findViewById(R.id.ChatLP_msg_received);
                ChatLP_date.setText(newTimestamp);
                ChatLP_msg_received.setText(newMessage);
                ChatLP_emoji1 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji1);
                ChatLP_emoji2 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji2);
                ChatLP_emoji3 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji3);
                ChatLP_emoji4 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji4);
                ChatLP_emoji5 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji5);
                ChatLP_emoji6 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji6);
                ChatLP_emoji7 = receivedOptionsDialog.findViewById(R.id.msg_rec_options_emoji7);

                // todo : send msg options dialog
                Dialog sendOptionsDialog = new Dialog(v.getRootView().getContext());
                sendOptionsDialog.setContentView(LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.layout_sended_message_options,null));
                sendOptionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sendOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                LinearLayout ChatLP_emoji1_s, ChatLP_emoji2_s, ChatLP_emoji3_s, ChatLP_emoji4_s, ChatLP_emoji5_s, ChatLP_emoji6_s, ChatLP_emoji7_s;
                LinearLayout ChatLP_dlt_btn_s , ChatLP_copy_btn_s, ChatLP_share_btn_s;
                TextView ChatLP_date_s, ChatLP_msg_received_s ;
                ChatLP_dlt_btn_s = sendOptionsDialog.findViewById(R.id.ChatLP_dlt_btn_s);
                ChatLP_copy_btn_s = sendOptionsDialog.findViewById(R.id.ChatLP_copy_btn_s);
                ChatLP_share_btn_s = sendOptionsDialog.findViewById(R.id.ChatLP_share_btn_s);
                ChatLP_date_s = sendOptionsDialog.findViewById(R.id.ChatLP_date_s);
                ChatLP_msg_received_s = sendOptionsDialog.findViewById(R.id.ChatLP_msg_send);
                ChatLP_date_s.setText(newTimestamp);
                ChatLP_msg_received_s.setText(newMessage);

                // todo : set options for send ***********************************
                if (sender.equals(fUser.getUid())){
                    sendOptionsDialog.show();

                    // copy btn
                    ChatLP_copy_btn_s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText( "Copy" , holder.messageTv.getText().toString());
                            clipboardManager.setPrimaryClip(clip);
                            Toast.makeText(context, "Copied !", Toast.LENGTH_SHORT).show();
                            sendOptionsDialog.dismiss();
                        }
                    });

                    // delete btn
                    ChatLP_dlt_btn_s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.show();
                            btn3_2btn_dialog.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            sendOptionsDialog.dismiss();

                            btn1_2btn_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String MyUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
                                    Query query = databaseReference.orderByChild("timestamp").equalTo(timeStamp);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                if (ds.child("sender").getValue().equals(MyUid)){
                                                    ds.getRef().removeValue();
                                                    HashMap<String , Object> hashMap = new HashMap<>();
                                                    holder.itemView.setVisibility(View.INVISIBLE);
                                                    ds.getRef().updateChildren(hashMap);
                                                    deleteDialog.dismiss();
                                                }
                                                else {
                                                    Toast.makeText(context, "Error ! try again ...", Toast.LENGTH_SHORT).show();
                                                    deleteDialog.dismiss();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                            btn2_2btn_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference3 = firebaseDatabase.getReference("Chats");

                                    databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String key = snapshot.getRef().getKey(); // todo ................................... continue from here getRef(i)
                                            if (snapshot.exists()){
                                                HashMap<String,Object> hashMap1 = new HashMap<>();
                                                hashMap1.put("message" , "done work");
                                                databaseReference3.child(key).updateChildren(hashMap1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                            btn3_2btn_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteDialog.dismiss();
                                }
                            });

                        }
                    });

                    // share btn
                    ChatLP_share_btn_s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, holder.messageTv.getText().toString());
                            context.startActivity(shareIntent);
                            sendOptionsDialog.dismiss();
                        }
                    });

                // todo : set options for receive ***********************************
                } else {
                    receivedOptionsDialog.show();

                    // copy btn
                    ChatLP_copy_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText( "Copy" , holder.messageTv.getText().toString());
                            clipboardManager.setPrimaryClip(clip);
                            Toast.makeText(context, "Copied !", Toast.LENGTH_SHORT).show();
                            receivedOptionsDialog.dismiss();
                        }
                    });
                    // share btn
                    ChatLP_share_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, holder.messageTv.getText().toString());
                            context.startActivity(shareIntent);
                            receivedOptionsDialog.dismiss();
                        }
                    });

                    // delete btn
                    ChatLP_dlt_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            deleteDialog.show();
                            btn1_2btn_dialog.setVisibility(View.GONE);
                            view.setVisibility(View.GONE);
                            btn3_2btn_dialog.setVisibility(View.VISIBLE);
                            receivedOptionsDialog.dismiss();

                            btn2_2btn_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                                }
                            });
                            btn3_2btn_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteDialog.dismiss();
                                }
                            });

                        }
                    });

                    //emoji reaction
                    ChatLP_emoji1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "R1", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatLP_emoji2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "R2", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatLP_emoji3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "R3", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatLP_emoji4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "R4", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatLP_emoji5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "R5", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatLP_emoji6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "R6", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatLP_emoji7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                }

                return true;
            }
        });

    }

//*****************************************************************************************************************

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return  MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

//**********************************************************************************************************

    class MyHolder extends RecyclerView.ViewHolder{

        // msg text views
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout message_layout;
        RelativeLayout Image_Layout;
        ImageView sender_reaction,receiver_reaction, messageIv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTv_receive);
            messageIv = itemView.findViewById(R.id.messageIv_receive);
            timeTv = itemView.findViewById(R.id.messageTime_receive);
            isSeenTv = itemView.findViewById(R.id.messageSeen_TV);
            message_layout = itemView.findViewById(R.id.message_layout);
            Image_Layout = itemView.findViewById(R.id.messageIv_receive_Layout);

            sender_reaction = itemView.findViewById(R.id.sender_reaction);
            receiver_reaction = itemView.findViewById(R.id.receiver_reaction);

        }
    }
}
