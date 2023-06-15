package com.example.socialv2;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialv2.Adapters.FriendListAdapter;
import com.example.socialv2.Model.User;
import com.example.socialv2.Notification.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class FriendsActivity extends Fragment implements FriendListAdapter.GetUserToChat {
    private String TAG="HOMEACTIVITYX";
    private ImageButton btnSettings;
    private ImageView profilePicture;
    private CoordinatorLayout constraintLayout;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private RecyclerView myRecyclerView;
    private TextView textView2;
    private FloatingActionButton fab;
   // private ImageView point;
    private FirebaseUser firebaseUser;
    private BottomAppBar bottomAppBar;
    private DatabaseReference reference;
    private ArrayList<User> userlist;
    private FriendListAdapter userAdapter;
    private BottomNavigationView bottomNavigationView;
    private EditText search_users;
    private TextView statusTextFriends;

    private ArrayList<User> mUsers;

    @Override
    public void onUserClicked(User user) {
        //getting the user from the adapter through call back interface
        //and sending the data through safe args
        final NavController navController = Navigation.findNavController(getView());
        String id=user.getId().toString();
        FriendsActivityDirections.ActionFriendsActivityToChatActivity action= FriendsActivityDirections.actionFriendsActivityToChatActivity(user) ;
        navController.navigate(action);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        initViews(view);

        Log.d("FriendsActivity","asds");

        //recycler viewer for the friend list display
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //firebase user needed to get the current user that is logged into the app
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId=firebaseUser.getUid();
        //initalize an empty array
        userlist = new ArrayList<>();

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setIcon(R.drawable.ic_friends_blue);


        //get the database reference to access the current users data
        DatabaseReference reference2 = FirebaseDatabase.getInstance(adress)
                .getReference("Users").child(userId);

        //set the profile picture on the home page
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class) ;


                if (user.getPictureUrl().equals("default")) {
                        profilePicture.setImageResource(R.drawable.ic_profile_black);
                } else {
                    Glide.with(getActivity()).load(user.getPictureUrl()).into(profilePicture);
                }

                if(user.getStatus().equals("false")){
                    //point.setImageResource(R.drawable.ic_point);

                }else if(user.getStatus().equals("true")){

                    //point.setImageResource(R.drawable.ic_point_available);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //point.setOnClickListener(new View.OnClickListener() {
            //@Override
           // public void onClick(View view) {
               // reference2.addListenerForSingleValueEvent (new ValueEventListener() {
                 //   @Override
                   // public void onDataChange(@NonNull DataSnapshot snapshot) {

                      //  //depending on the current users' status it's value will be changed
                      //  if(snapshot.child("status").getValue().toString().equals("false")){
                          //  reference2.child("status").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                         //       @Override
                         //       public void onComplete(@NonNull Task<Void> task) {
                          //          point.setImageResource(R.drawable.ic_point_available);
                           //     }
                          //  }).addOnFailureListener(new OnFailureListener() {
                           //     @Override
                           //     public void onFailure(@NonNull Exception e) {

                          //      }
                        //    });
                     //   }else if(snapshot.child("status").getValue().toString().equals("true")){

                        //    reference2.child("status").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                              //  @Override
                            //    public void onComplete(@NonNull Task<Void> task) {
                                //    point.setImageResource(R.drawable.ic_point);
                              //  }
                           // }).addOnFailureListener(new OnFailureListener() {
                               // @Override
                               // public void onFailure(@NonNull Exception e) {

                             //   }
                          //  });
                      //  }
                  //  }
                   // @Override
                    //public void onCancelled(@NonNull DatabaseError error) {

                    //}
             //   });

          //  }
     //   });

        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("FriendsActivity","reference call");


                userlist.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);

                    if(!user.getId().equals(userId)){
                        userlist.add(user);
                    }
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        updateToken(FirebaseMessaging.getInstance().getToken());

        // SEARCH BAR on text changed update the adapter
        search_users.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            //todo finish the fab action
            @Override
            public void onClick(View view) {

            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.action_friendsActivity_to_settingsActivity);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            //settings button
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_friendsActivity_to_settingsActivity);
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.eventsActivity:
                        navController.navigate(R.id.action_friendsActivity_to_eventsActivity);
                        break;
                    case R.id.home:
                        navController.navigate(R.id.action_friendsActivity_to_homeActivity2);
                        break;
                    case R.id.search:
                        navController.navigate(R.id.action_friendsActivity_to_searchActivity);
                        break;
                    default:

                }
                return false;
            }
        });

    }

    private void updateToken(Task <String > task) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        task.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Token token1= new Token(task.getResult());
                reference.child(firebaseUser.getUid()).setValue(token1);
            }
        });


    }
    //
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
                        Log.d("FriendsActivity","filling..");


                        if(user1.getId().equals(user.getId())){
                            mUsers.add(user);
                        }
                    }

                }

                Log.d("FriendsActivity",mUsers.toString());

                FirebaseAuth fAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = fAuth.getCurrentUser();
                userAdapter=new FriendListAdapter(getContext(),mUsers, FriendsActivity.this,firebaseUser.getUid());
                myRecyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s) {
      final   FirebaseUser firebaseUserx = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUserx.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://social-97b69-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference userIdRef = rootRef.child("Users");
        Query query =userIdRef .orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user =snapshot1.getValue(User.class);


                    assert user!=null;
                    assert firebaseUserx!=null;
                    if(!user.getId().equals(firebaseUserx.getUid())){
                        mUsers.add(user);
                    }
                }
                FirebaseAuth fAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = fAuth.getCurrentUser();
                userAdapter=new FriendListAdapter(getContext(),mUsers, FriendsActivity.this,firebaseUser.getUid());
                myRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews(View view) {
        constraintLayout = view.findViewById(R.id.constraintLayout);
        btnSettings = view.findViewById(R.id.btnSettingsFriends);
        textView2=view.findViewById(R.id.statusTextFriends);
        profilePicture = view.findViewById(R.id.profilePictureFriends);
        myRecyclerView = view.findViewById(R.id.friendsListRecViewer);
        fab = view.findViewById(R.id.fab);
        bottomAppBar = view.findViewById(R.id.bottomAppBarFriends);
        //point=view.findViewById(R.id.pointFriends);
        search_users=view.findViewById(R.id.friendsSearch);
        bottomNavigationView=view.findViewById(R.id.bottomNavigationViewFriends);
        bottomNavigationView.setItemIconTintList(null);
        statusTextFriends=view.findViewById(R.id.statusTextFriends);
    }


}
