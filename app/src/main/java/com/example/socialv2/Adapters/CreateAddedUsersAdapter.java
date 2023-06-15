package com.example.socialv2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialv2.Model.User;
import com.example.socialv2.R;

import java.util.ArrayList;

public class CreateAddedUsersAdapter extends RecyclerView.Adapter<CreateAddedUsersAdapter.ViewHolder> {


    private final ArrayList<User> userList;
    private final Context mContext;

    public CreateAddedUsersAdapter(Context mContext, ArrayList<User> userList) {

        this.userList = userList;
        this.mContext = mContext;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.create_added_users_recycler_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser=userList.get(position);
            holder.name.setText(currentUser.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.createUsersListName);
        }
    }

}
