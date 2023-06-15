package com.example.socialv2.Adapters;

import android.content.Context;
import android.util.Log;
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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder>{
    private static final String TAG ="UsersListAdapter" ;
    private final ArrayList<User> userList;
    private final Context mContext;
    private final GetUserToChatSearch getUserToChat;


    //user id needed FOR creating a firebase friendship and puting the clicked users under that usersid
    private String userId;
    private FirebaseUser firebaseUser;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    //***********
    public interface GetUserToChatSearch{
        void onUserClicked(User user);
    }

    public UsersListAdapter(Context mContext, ArrayList<User> userList, GetUserToChatSearch getUserToChat, String userId) {
        this.userList = userList;
        this.mContext = mContext;
        this.getUserToChat = getUserToChat;
        this.userId=userId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v;
        Log.d(TAG,"INFLATO23R");
            v= LayoutInflater.from(mContext).inflate(R.layout.proposed_users_recycler_layout,parent,false);
            Log.d(TAG,"INFLATOR");

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser=userList.get(position);

        Glide.with(mContext).load(currentUser.getPictureUrl()).into(holder.image);

        holder.name.setText(currentUser.getName());
        holder.userContainer.setOnClickListener(view -> getUserToChat.onUserClicked(currentUser));
        Log.d(TAG,"bindviewhodlder");


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
        public CircleImageView image;
        public TextView name,status;
        public RelativeLayout userContainer;
        public MaterialCardView btnInvite;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image=itemView.findViewById(R.id.proposedProfilePictureRec);
            status=itemView.findViewById(R.id.proposedProfileName);

            name=itemView.findViewById(R.id.proposedProfileNickName);
            userContainer=itemView.findViewById(R.id.proposedUserContainer);
            btnInvite=itemView.findViewById(R.id.btnInviteProposed);
        }
    }




}
