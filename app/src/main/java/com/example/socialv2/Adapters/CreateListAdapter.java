package com.example.socialv2.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialv2.Model.User;
import com.example.socialv2.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


public class CreateListAdapter extends RecyclerView.Adapter<CreateListAdapter.ViewHolder>{
        private final ArrayList<User> userList;
        private final ArrayList<User> selectedList;
        private final Context mContext;
        private final GetUserToChat getUserToChat;
    private String TAG="AddFriendsDialog";
        //***********
        public interface GetUserToChat{
            void onUserClicked(User user,int x);
        }

        public CreateListAdapter(Context mContext, ArrayList<User> userList, GetUserToChat getUserToChat ,ArrayList<User> selectedList) {
            this.userList = userList;
            this.mContext = mContext;
            this.getUserToChat = getUserToChat;
            this.selectedList = selectedList;

        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View v;

                v = LayoutInflater.from(mContext).inflate(R.layout.create_recycler_layout, parent, false);

            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User currentUser=userList.get(position);
            // todo holder.image.setImageResource(currentUser.getImageResource());
            holder.name.setText(currentUser.getName());
            holder.nameChecked.setText(currentUser.getName());

            if(currentUser.getPictureUrl().equals("default")){
                holder.image.setImageResource(R.drawable.ic_profile_black);
                holder.imageChecked.setImageResource(R.drawable.ic_profile_black);

            }else{
                Glide.with(mContext).load(currentUser.getPictureUrl()).into(holder.image);
                Glide.with(mContext).load(currentUser.getPictureUrl()).into(holder.imageChecked);
            }



            for(User user :selectedList){


                if(user.getName()==holder.name.getText()){
                    Log.d(TAG,"UserGETNAEM"+user.getName());
                    holder.friendChecked.setVisibility(View.VISIBLE);
                    holder.friendNotChecked.setVisibility(View.GONE);
                    holder.imageChecked.setVisibility(View.VISIBLE);
                    holder.nameChecked.setVisibility(View.VISIBLE);

                    holder.image.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);

                }

            }


            holder.friendChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //remove the checked from recyclerview
                    List<User> toRemove = new ArrayList<User>();
                    for (User user : selectedList) {
                        if (user.getName()==holder.name.getText()) {
                            toRemove.add(user);
                        }
                    }
                    selectedList.removeAll(toRemove);




                    getUserToChat.onUserClicked(currentUser,0);
                    holder.friendChecked.setVisibility(View.GONE);
                    holder.friendNotChecked.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.image.setVisibility(View.VISIBLE);

                    holder.nameChecked.setVisibility(View.GONE);
                    holder.imageChecked.setVisibility(View.GONE);
                }
            });



            holder.friendNotChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getUserToChat.onUserClicked(currentUser,1);
                    holder.friendChecked.setVisibility(View.VISIBLE);
                    holder.friendNotChecked.setVisibility(View.GONE);
                    holder.imageChecked.setVisibility(View.VISIBLE);
                    holder.nameChecked.setVisibility(View.VISIBLE);

                    holder.image.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);





                }
            });




        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
        public static class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView image,imageChecked;
            public TextView name,nameChecked;
            public RelativeLayout userContainer;
            private ConstraintLayout friendNotChecked;
            private MaterialCardView    friendChecked;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);



                image=itemView.findViewById(R.id.profilePictureRec);
                imageChecked=itemView.findViewById(R.id.profilePictureRecChecked);
                nameChecked=itemView.findViewById(R.id.profileNameRecChecked);
                friendChecked=itemView.findViewById(R.id.friendChecked);
                friendNotChecked=itemView.findViewById(R.id.friendNotChecked);
                name=itemView.findViewById(R.id.profileNameRec);
                userContainer=itemView.findViewById(R.id.userContainer);

            }
        }




    }

