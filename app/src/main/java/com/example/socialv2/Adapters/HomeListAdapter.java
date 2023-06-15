package com.example.socialv2.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialv2.Model.User;
import com.example.socialv2.Model.UserActivity;
import com.example.socialv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder>{
    private final ArrayList<User> userList;
    private final Context mContext;
    private final GetUserToChat getUserToChat;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference reference;
    private final String TAG="homeListAdapter";
    //***********
    public interface GetUserToChat{
        void onUserClicked(User user);
    }

    public HomeListAdapter(Context mContext, ArrayList<User> userList, GetUserToChat getUserToChat) {
        this.userList = userList;
        this.mContext = mContext;
        this.getUserToChat = getUserToChat;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v;

        v= LayoutInflater.from(mContext).inflate(R.layout.home_friend_rec_lyout,parent,false);


        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser=userList.get(position);
        if (currentUser != null) {
            Log.d(TAG,"currentUser"+currentUser.getName());
             String userId =currentUser.getId();
             reference= FirebaseDatabase.getInstance().getReference("Users").child(userId).child("usersActivity");
           //main users reference and we then iterate through children soo his friends and check if true meaning that they want to show their activity to us
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserActivity userActivity=snapshot.getValue(UserActivity.class);
                    if(userActivity.getActivityName()!="none"){

                        Log.d(TAG,userActivity.getLengthHours()+"adasdasdasa");
                        try {
                            Log.d(TAG,"settingProgress"+currentUser.getName());
                            setProgressBar(  userActivity.getDate(), userActivity.getLengthHours(), userActivity.getLengthMinutes(),userActivity.getStartTimeHour(),userActivity.getStartTimeMinute(),holder);

                            Log.d(TAG,"setProgress");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

             @Override
                 public void onCancelled(@NonNull DatabaseError error) {

             }
           });


            // todo holder.image.setImageResource(currentUser.getImageResource());
            holder.name.setText(currentUser.getName());
            holder.activity.setText(currentUser.getUsersActivity().getActivityName());
            holder.userContainer.setOnClickListener(view -> getUserToChat.onUserClicked(currentUser));

            if(currentUser.getPictureUrl().equals("default")){
                holder.homeProfileRecView.setImageResource(R.drawable.ic_profile_black);
            }else{
                Glide.with(mContext).load(currentUser.getPictureUrl()).into(holder.homeProfileRecView);
            }

            if(currentUser.getStatus().equals("false")){
            }else if(currentUser.getStatus().equals("true")){
            }

            if(currentUser.getPictureUrl().equals("default")){
            }else{


            }

            Log.d(TAG,"holder set");

            //   holder.statusImage.set
        }

    }
    public void setProgressBar(String date,String lengthHour,String lengthMinute,String startHour,String startMinute,ViewHolder holder) throws ParseException {
        Log.d(TAG,"setProgress ACTION");
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int dateDay=calendar.get(Calendar.DAY_OF_MONTH);
        int dateMonth=calendar.get(Calendar.MONTH);
        dateMonth++;
        int dateYear=calendar.get(Calendar.YEAR);
        String fulldate=String.valueOf(dateDay)+"/"+String.valueOf(dateMonth)+"/"+String.valueOf(dateYear);
        Log.d(TAG,fulldate);
        Log.d(TAG,date);
        if(fulldate.equals(date)){
            Log.d(TAG,String.valueOf(startHour));
            Log.d(TAG,String.valueOf(hour24hrs));
            if(hour24hrs==Integer.parseInt(startHour)){
                if(minutes>=Integer.parseInt(startMinute)){
                    //already started
                }else{
                    Log.d(TAG,String.valueOf(hour24hrs));
                    int minutesLeft=Integer.parseInt(startMinute)-minutes;
                    holder.progressBar.setText(String.valueOf(minutesLeft)+" minutes left");
                }
            }else if(hour24hrs>Integer.parseInt(startHour)){
                //already started
            }else{
                int minutesLeft=60-minutes+Integer.parseInt(startMinute);
                int hoursLeft= Integer.parseInt(startHour)-hour24hrs;
                if(hoursLeft==1){
                    holder.progressBar.setText(String.valueOf(minutesLeft+" minutes left"));
                }else{
                    holder.progressBar.setText(String.valueOf(hoursLeft+" hours "+minutesLeft+" minuntes left"));
                }

            }

        }else{

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
            Date date1 = simpleDateFormat.parse(fulldate);
            Date date2 = simpleDateFormat.parse(date);
            if(date2.getYear()>date1.getYear()){
                //return year difference date2.getYear()-date1.getYear()
                int yearsLeft= date2.getYear()-date1.getYear();
                holder.progressBar.setText(String.valueOf( yearsLeft+" years left"));
            }else if(date2.getYear()<date1.getYear()){

                //error picked the year beffore
            }else{
                if(date2.getMonth()>date1.getMonth()){
                    int monthLeft=date2.getYear()-date1.getYear();
                    holder.progressBar.setText(String.valueOf( monthLeft+" months left"));
                }else if(date2.getMonth()<date1.getMonth()){
                    //error picked the year beffore
                }else{
                    if(date2.getDay()>date1.getDay()){
                        int daysLeft=date2.getDay()-date1.getDay();
                        Log.d(TAG,String.valueOf(date2.getDay()));
                        holder.progressBar.setText(String.valueOf( daysLeft+" days left"));
                    }else if(date2.getDay()<date1.getDay()){
                        //error picked the year beffore
                    }else{

                        //the case is covered whend dates are equall
                    }

                }
            }

       /*     Log.d(TAG,String.valueOf("timeset"));
            Long different=date2.getTime()-date1.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;
            Log.d(TAG,String.valueOf(elapsedDays));
            //return the date value to holder
          */
        }


    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image,statusImage;
        public TextView name,status;
        public TextView activity;
        public RelativeLayout userContainer;
        private CircleImageView   homeProfileRecView;
        private TextView progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            activity=itemView.findViewById(R.id.profileActivityRec);
            progressBar=itemView.findViewById(R.id.activityProgressBar);
            name=itemView.findViewById(R.id.profileNameRec);
            homeProfileRecView=itemView.findViewById(R.id.homeProfileRecView);
            userContainer=itemView.findViewById(R.id.userContainer);
        }
    }




}
