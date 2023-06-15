package com.example.socialv2.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialv2.Model.Chat;
import com.example.socialv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private static final String TAG = "MESSAGE ADAPTER";
    private final ArrayList<Chat> mChat;
    private final Context mContext;
    private final String imageUrl;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private int LeftOrRight=0;


    public MessageAdapter(Context mContext, ArrayList<Chat> mChat, String imageUrl) {
        this.mChat = mChat;
         this.imageUrl=imageUrl;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType==MSG_TYPE_RIGHT){
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            LeftOrRight=1;
        }else{
            LeftOrRight=0;
            v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat= mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        if(LeftOrRight==1){
            Log.d(TAG,"here I was putting a profile pic to the right message");
        }else{

            if(imageUrl.equals("default")){
                holder.profile_image.setImageResource(R.drawable.ic_profile);
            }else{
                Glide.with(mContext).load(imageUrl).into(holder.profile_image);

            }
        }


        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            }else {
                holder.txt_seen.setText("Delivered");
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profile_image;
        public TextView show_message,message_name;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image=itemView.findViewById(R.id.chatRightProfilePicture);
            show_message=itemView.findViewById(R.id.chatRightText);
            txt_seen= itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fuser != null;
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
