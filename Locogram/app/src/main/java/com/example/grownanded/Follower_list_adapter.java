package com.example.grownanded;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Follower_list_adapter extends RecyclerView.Adapter<Follower_list_adapter.MyHolder> {

    Context context;
    List<Users_Model> userList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    FirebaseUser firebaseUser;

//***********************************************************************************************************************

    public Follower_list_adapter(Context context, List<Users_Model> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public Follower_list_adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_follow_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Follower_list_adapter.MyHolder holder, int i) {

//***********************************************************************************************************************

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
                    if (snapshot.child(userId).exists()){
                        holder.FollowBtn.setText("Following");
                        holder.FollowBtn.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        holder.FollowBtn.setTextColor(Color.BLACK);
                    } else {
                        holder.FollowBtn.setText("Follow");
                        holder.FollowBtn.setBackgroundColor(Color.parseColor("#5f9ea0"));
                        holder.FollowBtn.setTextColor(Color.WHITE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        // todo : follow btn click *************************************************

        holder.FollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = holder.FollowBtn.getText().toString();
                if (btn.equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);
                } else if (btn.equals("Following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }

        });

    }

//***********************************************************************************************************************

    @Override
    public int getItemCount() {
        return userList.size();
    }

//***********************************************************************************************************************

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mImageV;
        TextView mNameTV;
        Button FollowBtn;

        public MyHolder (@NonNull View itemView) {
            super(itemView);
            mImageV = itemView.findViewById(R.id.FollowList_Image);
            mNameTV = itemView.findViewById(R.id.FollowList_Name);
            FollowBtn = itemView.findViewById(R.id.btn_follow_on_profile);
        }
    }
}
