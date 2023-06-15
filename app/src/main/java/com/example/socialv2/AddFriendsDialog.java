package com.example.socialv2;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialv2.Adapters.CreateAddedUsersAdapter;
import com.example.socialv2.Adapters.CreateListAdapter;
import com.example.socialv2.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

public class AddFriendsDialog extends BottomSheetDialogFragment implements CreateListAdapter.GetUserToChat {
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private ImageButton btnBack;
    private ImageButton searchExpand;
    private RelativeLayout btnCreate;
    private EditText edtGetStatus;
    private RecyclerView recyclerView,addedRecView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference,reference2;
    private CreateListAdapter userAdapter;
    private ArrayList<User> userlist;
    private String TAG="AddFriendsDialog";
    private ArrayList<User> mUsers;
    private TextView selectedUserList,chooseYourFriendsText;
    private ArrayList<String> allFriends;
    private ImageView cancelSearch;
    private ArrayList<User> ArrayselectedUserList;
    private CreateAddedUsersAdapter adapter;
    private EditText search_users;
    private TextView btnCreateGroup;
    private goToHomePage goToHomePage;
    private String activityName,date, startMinute, startHour, timeLengthHour, timeLengthMinutes,  address;

    public AddFriendsDialog(String activityName,String date, String startMinute, String startHour, String timeLengthHour, String timeLengthMinutes, String address,goToHomePage goToHomePage){
        this.activityName=activityName;
        this.goToHomePage=goToHomePage;
        this.date=date;
        this.startMinute=startMinute;
        this.startHour=startHour;
        this.timeLengthMinutes=timeLengthMinutes;
        this.timeLengthHour=timeLengthHour;
        this.address=address;
    }

    public interface goToHomePage{
        void onFinish(int x);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_friends_bottom_sheet, container, false);
        Log.d(TAG,"view infalter") ;
        return view;
    }

    @Override
    public void onUserClicked(User user,int x) {
        //getting the user from the adapter through call back interface
        //and sending the data through safe args

        chooseYourFriendsText.setVisibility(View.GONE);
        if(x==1){
                int cnt=0;
                for(User user1:ArrayselectedUserList){
                    if(user.getId()==user1.getId()){
                        cnt++;
                    }
                }
                if(cnt!=0){
                }else{
                    ArrayselectedUserList.add(user);

                }
            adapter=new CreateAddedUsersAdapter(getContext(),ArrayselectedUserList);
            addedRecView.setAdapter(adapter);

        }
        else if(x==0){

                    ArrayselectedUserList.remove(user);

            adapter=new CreateAddedUsersAdapter(getContext(),ArrayselectedUserList);
            addedRecView.setAdapter(adapter);


        }


        //todo ADD users to recycler view
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(ArrayselectedUserList!=null){
            if (ArrayselectedUserList.isEmpty()){
                chooseYourFriendsText.setVisibility(View.VISIBLE);

            }else{
                chooseYourFriendsText.setVisibility(View.GONE);
            }
        }

        initViews(view);

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   String status = edtGetStatus.getText().toString().trim();
               // action.setStatus(status);
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid = firebaseUser.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance(adress);
                DatabaseReference reference = database.getReference("Users").child(userid).child("usersActivity");

                Log.d(TAG,reference.toString());
                HashMap<String,String> hashMap= new HashMap<>();
                hashMap.put("date",date);
                hashMap.put("startTimeHour",startHour);
                hashMap.put("startTimeMinute",startMinute);
                hashMap.put("LengthHours",timeLengthHour);
                hashMap.put("LengthMinutes",timeLengthMinutes);
                hashMap.put("activityName",activityName);
                hashMap.put("location",address);

                //get all friends to an array so then you compare them with the friends chosen and set the values in the firebase-Friends to true or false


                Log.d(TAG,hashMap.toString());
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else{
                            Log.d(TAG,"activity registered in the database");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"activity registry failure");
                    }
                });


                //in the added users  friends list set our value to true to indicate that we want to show our activity
                for (User user :ArrayselectedUserList){
                  database.getReference("Friends").child(user.getId()).child(userid).setValue(true);

                }
                dismiss();
                goToHomePage.onFinish(1);

            }
        });


        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_users.setText("");
                cancelSearch.setVisibility(View.INVISIBLE);
                hideKeyboard(view);
            }
        });
        search_users.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        search_users.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
                cancelSearch.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cancelSearch.setVisibility(View.VISIBLE);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                userlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Object checkIfFriend = dataSnapshot.getKey();
                    if(checkIfFriend!=null){
                        reference2 = FirebaseDatabase.getInstance().getReference("Users").child(checkIfFriend.toString());
                        reference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                User user=snapshot.getValue(User.class);
                                userlist.add(user);
                                Log.d(TAG,user.toString());


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    Log.d("CRAETEACITIVTYX",checkIfFriend.toString());

                    // if(checkIfFriend==true){

                    //  }
                    //  assert checkIfFriend!=null;
                    //  if () {
                    //  userlist.add(user);
                    // }
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchUsers(String s) {
        final FirebaseUser firebaseUserx = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUserx.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance(adress).getReference();
        DatabaseReference userIdRef = rootRef.child("Users");
        Query query = userIdRef.orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);


                    assert user != null;
                    assert firebaseUserx != null;
                    if (!user.getId().equals(firebaseUserx.getUid())) {
                        mUsers.add(user);
                    }
                }
                userAdapter = new CreateListAdapter(getContext(), mUsers, AddFriendsDialog.this, ArrayselectedUserList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfFriend(ArrayList<String> listoffreinds) {
        Log.d("CraeteActivity",listoffreinds.toString());


        //comparing the two arrays of all friends and the CHOSEN ONES and adding them to hasmap so that it can fit in the firebase

        HashMap<String,Object> hashMap= new HashMap<>();

        for(int i=0;i<listoffreinds.size();i++){
            for(int j=0;j<ArrayselectedUserList.size();j++){

                if(ArrayselectedUserList.get(j).getId()==listoffreinds.get(i)){
                    hashMap.put(listoffreinds.get(i),true);
                }

            }
            hashMap.put(listoffreinds.get(i),false);

        }

        final   FirebaseUser firebaseUserx = FirebaseAuth.getInstance().getCurrentUser();
        String userid = firebaseUserx.getUid();
        reference=FirebaseDatabase.getInstance().getReference("Friends").child(userid);
        reference.updateChildren(hashMap);

    }

    private void chatList() {
        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user =snapshot1.getValue(User.class);

                    for( User user1:userlist){


                        if(user1.getId().equals(user.getId())){
                            mUsers.add(user);
                        }
                    }

                }
                Log.d(TAG,"added");
                userAdapter=new CreateListAdapter(getContext(),mUsers, AddFriendsDialog.this,ArrayselectedUserList);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews(View view) {
        chooseYourFriendsText=view.findViewById(R.id.chooseYourFriendsText);
        btnCreate=view.findViewById(R.id.invitefriendslayout);
        btnCreateGroup=view.findViewById(R.id.btnCreateGroup);
        btnBack=view.findViewById(R.id.btnBackFromCreate);
        edtGetStatus=view.findViewById(R.id.editTextCreate);
        cancelSearch=view.findViewById(R.id.cancelSearchCreateActivity);
        ArrayselectedUserList=new ArrayList<>();
        addedRecView=view.findViewById(R.id.createAddedUsersRecView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager.setStackFromEnd(true);
        addedRecView.setLayoutManager(linearLayoutManager);
        recyclerView = view.findViewById(R.id.createRecViewer);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        allFriends=new ArrayList<>();
        userlist = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Friends").child(userId);
        search_users = view.findViewById(R.id.createSearch);
    }
}