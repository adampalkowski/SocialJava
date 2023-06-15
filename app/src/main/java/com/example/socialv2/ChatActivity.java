package com.example.socialv2;


import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialv2.Adapters.MessageAdapter;
import com.example.socialv2.Model.Chat;
import com.example.socialv2.Model.User;
import com.example.socialv2.Notification.Notification.Client;
import com.example.socialv2.Notification.Notification.Data;
import com.example.socialv2.Notification.Notification.MyResponse;
import com.example.socialv2.Notification.Notification.Sender;
import com.example.socialv2.Notification.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends Fragment {
    private final String TAG = "MainActivityx";
    private DatabaseReference reference;
    private ImageView sendButton,chatterStatus;
    private TextView chatterName;
    private ImageButton btnBack;
    private FirebaseUser fuser;
    private EditText edtMessage;
    private MessageAdapter messageAdapter;
    private        DatabaseReference referencex;
    private ArrayList<Chat> mchat;
    private   RecyclerView recyclerView;
    private APIService apiService;
    private CircleImageView chatTopProfilePicture;
    private ValueEventListener seenListener;
    private String userid;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private boolean notify=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);


        initViews(view);

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        //args could potentialy be null do sth about it in the future
        ChatActivityArgs args= ChatActivityArgs.fromBundle(getArguments());
        User chatter =args.getChatter();
        userid=chatter.getId();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatterName.setText(chatter.getName());

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    sendButton.setImageResource(R.drawable.ic_send_icon_red);


            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendButton.setImageResource(R.drawable.ic_send_icon);
            }
        });


        Glide.with(getActivity()).load(chatter.getPictureUrl()).into(chatTopProfilePicture);

        DatabaseReference reference = FirebaseDatabase.getInstance(adress).getReference("Users").child(chatter.getId());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Objects.equals(snapshot.child("status").getValue(), "false")){
                    chatterStatus.setImageResource(R.drawable.ic_point);
                }else if(Objects.equals(snapshot.child("status").getValue(), "true")){
                    chatterStatus.setImageResource(R.drawable.ic_point_available);
                }
                chatterName.setText(chatter.getName());

                if(chatter.getPictureUrl().equals("default")){

                }
                readMessages(fuser.getUid(),chatter.getId(),chatter.getPictureUrl());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendButton.setOnClickListener(view1 -> {
            notify=true;
            String msg=edtMessage.getText().toString().trim();
            if(!msg.equals("")){
                sendMessage(fuser.getUid(),chatter.getId(),msg);
            }
            edtMessage.setText("");
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_chatActivity_to_friendsActivity);
            }
        });
        seenMessage(chatter.getId());


        edtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void seenMessage(final String userid){
        referencex=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=referencex.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);


                    if(chat.getReceiver().equals(fuser.getUid())&&chat.getSender().equals(userid)){

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String sender, String receiver, String message){
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);
        final String msg = message;
        DatabaseReference referenceMessage;
        referenceMessage=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        referenceMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                if(notify){
                    sendNotification(receiver,user.getName(),msg);
                }

                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, String username, final String message) {
        Log.d(TAG,"notificaiton sented");
        DatabaseReference tokens =FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);

                    Data data=new Data(fuser.getUid(),R.drawable.ic_profile,username+":"+message,"New Message",
                            userid);
                    Sender sender= new Sender(data,token.getToken());
                    Log.d(TAG,sender.toString()+"X");
                    Log.d(TAG,data.toString()+"xx");

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Log.d(TAG,"notificaiton failed1");

                                            //FAILED
                                        }
                                        Log.d(TAG,"GOOOD");
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.d(TAG,"notificaiton failed2");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,"notificaiton failed4");
            }
        });
    }

    private void initViews(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        reference=FirebaseDatabase.getInstance(adress).getReference();
        sendButton=view.findViewById(R.id.sendButton);
        fuser= auth.getCurrentUser();
        edtMessage=view.findViewById(R.id.messageEditText);
        btnBack=view.findViewById(R.id.btnChatBack);
        chatterName=view.findViewById(R.id.chatChatterName);
        chatterStatus=view.findViewById(R.id.chatChatterStatus);
        recyclerView=view.findViewById(R.id.messageRecyclerView);
        chatTopProfilePicture=view.findViewById(R.id.chatTopProfilePicture);

    }

    private void readMessages (String myid,String userid,String imageUrl){
        mchat=new ArrayList<>();
        DatabaseReference messageReference = reference.child("Chats");

        messageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);



                    if(chat.getReceiver().equals(myid)&&chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid)&&chat.getSender().equals(myid))
                    {
                        mchat.add(chat);
                    }
                    messageAdapter= new MessageAdapter(getContext(),mchat,imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void currentUser(String userid){
        SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE ).edit();
        editor.putString("currentuser",userid);
        editor.apply();

    }

    @Override
    public void onPause() {
        super.onPause();
        referencex.removeEventListener(seenListener);
        currentUser("none");
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser(userid);
    }
}
