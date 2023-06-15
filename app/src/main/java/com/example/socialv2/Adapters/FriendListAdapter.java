package com.example.socialv2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialv2.Model.User;
import com.example.socialv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    private final ArrayList<User> userList;
    private final Context mContext;
    private final GetUserToChat getUserToChat;
    private DatabaseReference reference;

    //user id needed FOR creating a firebase friendship and puting the clicked users under that usersid
    private String userId;
    private FirebaseUser firebaseUser;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    //***********
    public interface GetUserToChat{
        void onUserClicked(User user);
    }

    public FriendListAdapter(Context mContext, ArrayList<User> userList, GetUserToChat getUserToChat, String userId) {
        this.userList = userList;
        this.mContext = mContext;
        this.getUserToChat = getUserToChat;
        this.userId=userId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v;

            v= LayoutInflater.from(mContext).inflate(R.layout.friend_recycler_layout,parent,false);


        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      User currentUser=userList.get(position);
      // todo holder.image.setImageResource(currentUser.getImageResource());
        holder.name.setText(currentUser.getName());
        holder.userContainer.setOnClickListener(view -> getUserToChat.onUserClicked(currentUser));
      // if(currentUser.getStatus().equals("false")){
      //    holder.statusImage.setImageResource(R.drawable.ic_point);
   //    }else if(currentUser.getStatus().equals("true")){
      //     holder.statusImage.setImageResource(R.drawable.ic_point_available);
      // }
        holder.status.setText(currentUser.getUsersActivity().getActivityName());

        if(currentUser.getPictureUrl().equals("default")){
            holder.image.setImageResource(R.drawable.ic_profile_black);
        }else{
            Glide.with(mContext).load(currentUser.getPictureUrl()).into(holder.image);
        }

        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database= FirebaseDatabase.getInstance(adress);
                DatabaseReference reference=database.getReference();
                reference=database.getReference("Friends").child(userId);
                HashMap<String,Object> hashMap= new HashMap<>();
                hashMap.put(currentUser.getId().toString().trim(),true);
               reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                        }else{
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });


     //   holder.statusImage.set
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image,statusImage;
        public TextView name,status;
        public RelativeLayout userContainer;
        public Button btnInvite;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image=itemView.findViewById(R.id.profilePictureRec);
            status=itemView.findViewById(R.id.profileStatusRec);
           // statusImage=itemView.findViewById(R.id.profileStatusPictureRec);
            name=itemView.findViewById(R.id.profileNameRec);
            userContainer=itemView.findViewById(R.id.userContainer);
            btnInvite=itemView.findViewById(R.id.btnInvite);
        }
    }




}
