package com.example.grownanded;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Followers_my_adapter extends RecyclerView.Adapter<Followers_my_adapter.MyHolder> {
    Context context;
    List<Users_Model> userList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    FirebaseUser firebaseUser;

//***********************************************************************************************************************

    public Followers_my_adapter(Context context, List<Users_Model> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_follow_list, parent, false);
        return new MyHolder(view);
    }

//***********************************************************************************************************************

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        // todo : firebase ids set *************************************************

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // todo : Model get data ***************************************************

        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        String userId = userList.get(i).getUid();

        holder.mNameTV.setText(userName);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.mipmap.profile_icon)
                    .into(holder.mImageV);
        }catch (Exception e){}

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, User_Profile_Activity.class);
                intent.putExtra("hisId", userId);
                context.startActivity(intent);
            }
        });

        // todo : check current user ************************************************

        if (userId.equals(firebaseUser.getUid())){
            holder.FollowBtn.setVisibility(View.GONE);
        }

        // todo : check user followed or not *************************************************

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.FollowBtn.setText("Remove");
                    holder.FollowBtn.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    holder.FollowBtn.setTextColor(Color.BLACK);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // todo : remove follower btn click *************************************************

        holder.FollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dialog.show();
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.dialog.dismiss();
                    }
                });

                holder.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                                .child("Following").child(firebaseUser.getUid()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("Followers").child(userId).removeValue();
                        holder.dialog.dismiss();
                    }
                });
            }
        });

    }



//***********************************************************************************************************************

    @Override
    public int getItemCount() {
        return userList.size();
    }

//***********************************************************************************************************************

    class MyHolder extends RecyclerView.ViewHolder {


        ImageView mImageV;
        TextView mNameTV, AlertDialog_title;
        Button FollowBtn,confirm,cancel;
        Dialog dialog;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mImageV = itemView.findViewById(R.id.FollowList_Image);
            mNameTV = itemView.findViewById(R.id.FollowList_Name);
            FollowBtn = itemView.findViewById(R.id.btn_follow_on_profile);

            // todo : unfollow dialog

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_dialog_alert);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
             confirm = dialog.findViewById(R.id.btn_Logout_confirm);
             cancel = dialog.findViewById(R.id.btn_logOut_cancel);

            AlertDialog_title = dialog.findViewById(R.id.AlertDialog_title);
            AlertDialog_title.setText("Are you sure ! you want to remove");
            confirm.setText("Remove");

        }
    }
}
