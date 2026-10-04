package com.example.grownanded;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grownanded.Chat_Section.Chat_List_Activity;
import com.example.grownanded.Chat_Section.Chat_List_Adapter;
import com.example.grownanded.Chat_Section.Chat_List_Model;
import com.example.grownanded.Chat_Section.Chats_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BottomNavigationFragment extends Fragment implements View.OnClickListener {

    View view;
    RelativeLayout home_ll, stores_ll, post_ll,chats_ll,profile_ll;
    ImageView bottom_toolbar_imageview_1,bottom_toolbar_imageview_2,bottom_toolbar_imageview_3,bottom_toolbar_imageview_4,bottom_toolbar_imageview_5;
    TextView bottom_toolbar_textview_1,bottom_toolbar_textview_2,bottom_toolbar_textview_3,bottom_toolbar_textview_4,bottom_toolbar_textview_5;
    Dialog BottomSheetDialog;
    List<Chat_List_Model> chatList;
    Chat_List_Adapter adapterChat;
    String MyUid;

    DatabaseReference reference;
    FirebaseUser currentUser;
    ImageView chatList_bottom_bar_ind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getChildFragmentManager();
        view = inflater.inflate(R.layout.layout_bottom_toolbar,container,false);
        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializecomponents();
    }

    public void initializecomponents() {
        if (getView() == null)return;
        home_ll = getView().findViewById(R.id.home_ll);
        stores_ll = getView().findViewById(R.id.stores_ll);
        post_ll = getView().findViewById(R.id.post_ll);
        chats_ll = getView().findViewById(R.id.chats_ll);
        profile_ll = getView().findViewById(R.id.profile_ll);


        bottom_toolbar_imageview_1 = getView().findViewById(R.id.bottom_toolbar_imageview_1);
        bottom_toolbar_imageview_2 = getView().findViewById(R.id.bottom_toolbar_imageview_2);
        bottom_toolbar_imageview_3 = getView().findViewById(R.id.bottom_toolbar_imageview_3);
        bottom_toolbar_imageview_4 = getView().findViewById(R.id.bottom_toolbar_imageview_4);
        bottom_toolbar_imageview_5 = getView().findViewById(R.id.bottom_toolbar_imageview_5);

        bottom_toolbar_textview_1 = getView().findViewById(R.id.bottom_toolbar_textview_1);
        bottom_toolbar_textview_2 = getView().findViewById(R.id.bottom_toolbar_textview_2);
        bottom_toolbar_textview_3 = getView().findViewById(R.id.bottom_toolbar_textview_3);
        bottom_toolbar_textview_4 = getView().findViewById(R.id.bottom_toolbar_textview_4);
        bottom_toolbar_textview_5 = getView().findViewById(R.id.bottom_toolbar_textview_5);

        home_ll.setOnClickListener(this);
        stores_ll.setOnClickListener(this);
        post_ll.setOnClickListener(this);
        chats_ll.setOnClickListener(this);
        profile_ll.setOnClickListener(this);

        // todo : set ids and inti

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        MyUid = currentUser.getUid();
        chatList = new ArrayList<>();
        adapterChat = new Chat_List_Adapter(getContext(),chatList);
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(MyUid);
        chatList_bottom_bar_ind = getView().findViewById(R.id.chatList_bottom_bar_ind);

        setBottomNavigationView();

        newMsgInd();
    }

    private void newMsgInd() {
        // todo : Chat list
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat_List_Model chat_list_model = ds.getValue(Chat_List_Model.class);
                    chatList.add(chat_list_model);
                }
                adapterChat.notifyDataSetChanged();
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyItemInserted(chatList.size());


                for ( int i=0 ; i <chatList.size(); i++){
                    newmessage(chatList.get(i).getId());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void newmessage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Chats_Model chat = ds.getValue(Chats_Model.class);

                    if (chat==null){
                        continue;
                    }

                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }

                    if (chat.getSender().equals(userId) && chat.getReceiver().equals(currentUser.getUid()) && chat.getIsSeen().equals("Delivered")){
                        chatList_bottom_bar_ind.setVisibility(View.VISIBLE);
                    }
                    if (chat.getSender().equals(userId) && chat.getReceiver().equals(currentUser.getUid()) && chat.getIsSeen().equals("Seen")){
                        chatList_bottom_bar_ind.setVisibility(View.GONE);
                    }
                }
                adapterChat.notifyDataSetChanged();
                adapterChat.notifyItemRangeInserted(chatList.size(),chatList.size());
                adapterChat.notifyItemInserted(chatList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setBottomNavigationView() {

        int white_color = getActivity().getResources().getColor(R.color.purple_200);
        int yellow_color = getActivity().getResources().getColor(R.color.Dark_grey);

        if (getActivity() != null && getActivity() instanceof HomeActivity){
            bottom_toolbar_imageview_1.setColorFilter(white_color);
            bottom_toolbar_imageview_2.setColorFilter(yellow_color);
            bottom_toolbar_imageview_3.setColorFilter(yellow_color);
            bottom_toolbar_imageview_4.setColorFilter(yellow_color);
            bottom_toolbar_imageview_5.setColorFilter(yellow_color);

            bottom_toolbar_textview_1.setTextColor(white_color);
            bottom_toolbar_textview_2.setTextColor(yellow_color);
            bottom_toolbar_textview_3.setTextColor(yellow_color);
            bottom_toolbar_textview_4.setTextColor(yellow_color);
            bottom_toolbar_textview_5.setTextColor(yellow_color);


        }else if (getActivity() != null && getActivity() instanceof Stores_Activity){
            bottom_toolbar_imageview_1.setColorFilter(yellow_color);
            bottom_toolbar_imageview_2.setColorFilter(white_color);
            bottom_toolbar_imageview_3.setColorFilter(yellow_color);
            bottom_toolbar_imageview_4.setColorFilter(yellow_color);
            bottom_toolbar_imageview_5.setColorFilter(yellow_color);

            bottom_toolbar_textview_1.setTextColor(yellow_color);
            bottom_toolbar_textview_2.setTextColor(white_color);
            bottom_toolbar_textview_3.setTextColor(yellow_color);
            bottom_toolbar_textview_4.setTextColor(yellow_color);
            bottom_toolbar_textview_5.setTextColor(yellow_color);

        }else if (getActivity() != null && getActivity() instanceof Settings_Activity){
            bottom_toolbar_imageview_1.setColorFilter(yellow_color);
            bottom_toolbar_imageview_2.setColorFilter(yellow_color);
            bottom_toolbar_imageview_3.setColorFilter(white_color);
            bottom_toolbar_imageview_4.setColorFilter(yellow_color);
            bottom_toolbar_imageview_5.setColorFilter(yellow_color);

            bottom_toolbar_textview_1.setTextColor(yellow_color);
            bottom_toolbar_textview_2.setTextColor(yellow_color);
            bottom_toolbar_textview_3.setTextColor(white_color);
            bottom_toolbar_textview_4.setTextColor(yellow_color);
            bottom_toolbar_textview_5.setTextColor(yellow_color);

        }else if (getActivity() != null && getActivity() instanceof Chat_List_Activity){
            bottom_toolbar_imageview_1.setColorFilter(yellow_color);
            bottom_toolbar_imageview_2.setColorFilter(yellow_color);
            bottom_toolbar_imageview_3.setColorFilter(yellow_color);
            bottom_toolbar_imageview_4.setColorFilter(white_color);
            bottom_toolbar_imageview_5.setColorFilter(yellow_color);

            bottom_toolbar_textview_1.setTextColor(yellow_color);
            bottom_toolbar_textview_2.setTextColor(yellow_color);
            bottom_toolbar_textview_3.setTextColor(yellow_color);
            bottom_toolbar_textview_4.setTextColor(white_color);
            bottom_toolbar_textview_5.setTextColor(yellow_color);


        }else if (getActivity() != null && getActivity() instanceof ProfileActivity){
            bottom_toolbar_imageview_1.setColorFilter(yellow_color);
            bottom_toolbar_imageview_2.setColorFilter(yellow_color);
            bottom_toolbar_imageview_3.setColorFilter(yellow_color);
            bottom_toolbar_imageview_4.setColorFilter(yellow_color);
            bottom_toolbar_imageview_5.setColorFilter(white_color);

            bottom_toolbar_textview_1.setTextColor(yellow_color);
            bottom_toolbar_textview_2.setTextColor(yellow_color);
            bottom_toolbar_textview_3.setTextColor(yellow_color);
            bottom_toolbar_textview_4.setTextColor(yellow_color);
            bottom_toolbar_textview_5.setTextColor(white_color);

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getChildFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_ll:
                Intent intent = new Intent(getActivity(),HomeActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.stores_ll:
                Intent chats_intent = new Intent(getActivity(), Stores_Activity.class);
                getActivity().startActivity(chats_intent);

                break;
            case R.id.post_ll:
                ShowBottomSheet();
                break;
            case R.id.chats_ll:
                Intent my_ads_intent = new Intent(getActivity(), Chat_List_Activity.class);
                getActivity().startActivity(my_ads_intent);
                break;
            case R.id.profile_ll:
                Intent account_intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(account_intent);
                break;
        }

    }

    private void ShowBottomSheet() {
        // loading dialog
        BottomSheetDialog = new Dialog(getContext());
        BottomSheetDialog.setContentView(R.layout.layout_bottom_sheet);
        BottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        BottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.Bottom_sheet_animation;
        BottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView bottom_sheet_close_btn = BottomSheetDialog.findViewById(R.id.bottom_sheet_close_btn);
        LinearLayout bottom_sheet_btn1 = BottomSheetDialog.findViewById(R.id.bottom_sheet_btn1);
        LinearLayout bottom_sheet_btn2 = BottomSheetDialog.findViewById(R.id.bottom_sheet_btn2);
        LinearLayout bottom_sheet_btn3 = BottomSheetDialog.findViewById(R.id.bottom_sheet_btn3);

        bottom_sheet_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog.dismiss();
            }
        });

        bottom_sheet_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Create Advertisement", Toast.LENGTH_SHORT).show();
            }
        });

        bottom_sheet_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Upload Product", Toast.LENGTH_SHORT).show();
            }
        });

        bottom_sheet_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Upload Story", Toast.LENGTH_SHORT).show();
            }
        });

        BottomSheetDialog.show();
    }

}
