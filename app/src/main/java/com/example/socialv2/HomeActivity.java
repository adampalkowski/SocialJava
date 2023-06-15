package com.example.socialv2;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;
import com.example.socialv2.Adapters.HomeListAdapter;
import com.example.socialv2.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends Fragment implements HomeListAdapter.GetUserToChat {
    private BottomNavigationView bottomNavigationView;
    private ExtendedFloatingActionButton fab;
    private CircleImageView profilePicture;
    private ImageView btnSettings;
    private ArrayList<User> userlist;
    private ImageView point;


    private FirebaseUser firebaseUser;
    private CoordinatorLayout constraintLayout;
    private RecyclerView homeRecView;
    private static final String Encrypted_prefs = "encrypted_storage.txt";
    private DatabaseReference reference;
    private ArrayList<User> mUsers;
    private ArrayList<String> friendsActivitiesList;
    private String TAG="HomeActivity";
    private HomeListAdapter userAdapter;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        final NavController navController = Navigation.findNavController(getView());
        HomeActivityArgs args = HomeActivityArgs.fromBundle(getArguments());
        homeRecView.setLayoutManager(new GridLayoutManager(getContext(),1));
        userlist=new ArrayList<>();


        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setIcon(R.drawable.ic_home_blue);


        //friend activities list array to save all the id of the friends needed to show on the screen
        friendsActivitiesList=new ArrayList<>();
        homeRecView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.shrink();
                else if (dy < 0)
                    fab.extend();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId=firebaseUser.getUid();
        reference=FirebaseDatabase.getInstance().getReference("Friends").child(userId);

        //main users reference and we then iterate through children soo his friends and check if true meaning that they want to show their activity to us
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    if (dataSnapshot.getValue().toString()=="true"){
                        //if true add them to the array
                        friendsActivitiesList.add(dataSnapshot.getKey().toString());
                    }

                }

                showFriendsActivities();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //get current users activity on top
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database=FirebaseDatabase.getInstance(adress);
        String userid=firebaseUser.getUid();


        homeRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    fab.shrink();
                }else{
                    fab.extend();
                }
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              navController.navigate(R.id.action_homeActivity_to_createActivity);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                //    case R.id.eventsActivity:
                      //  navController.navigate(R.id.action_homeActivity_to_eventsActivity);
                       // break;
                   case R.id.friends:
                       navController.navigate(R.id.action_homeActivity_to_friendsActivity);
                       break;
                    case R.id.eventsActivity:
                        navController.navigate(R.id.action_homeActivity_to_eventsActivity);
                        break;

                    case R.id.search:
                        navController.navigate(R.id.action_homeActivity_to_searchActivity);

                        break;
                   default:
                }
                return false;
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            //settings button
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_homeActivity_to_settingsActivity);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get the database reference to access the current users data
        DatabaseReference reference = FirebaseDatabase.getInstance(adress)
                .getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG,"getting users");

                User user= snapshot.getValue(User.class) ;
                Log.d(TAG,user.toString());


                if (user.getPictureUrl().equals("default")) {
                    profilePicture.setImageResource(R.drawable.ic_profile_black);
                } else {
                    Glide.with(getActivity()).load(user.getPictureUrl()).into(profilePicture);
                }

                if(user.getStatus().equals("false")){
                     point.setImageResource(R.drawable.ic_point);

                }else if(user.getStatus().equals("true")){

                    point.setImageResource(R.drawable.ic_point_available);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //depending on the current users' status it's value will be changed
                        if(snapshot.child("status").getValue().toString().equals("false")){
                            reference.child("status").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    point.setImageResource(R.drawable.ic_point_available);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }else if(snapshot.child("status").getValue().toString().equals("true")){

                            reference.child("status").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    point.setImageResource(R.drawable.ic_point);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

       });

        //   if (!firebaseUser.isEmailVerified()) {
        //      //checking if user's email is verified if not display snack bar
        //      showSnackbar();
        // }
        //
        profilePicture.setOnClickListener(new View.OnClickListener() {
               @Override
                  public void onClick(View view) {

                         navController.navigate(R.id.action_homeActivity_to_profileActivity);
                   }
              });


    }

    private void initViews(View view) {
        fab=view.findViewById(R.id.extended_fab);
        fab.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.white)));
        bottomNavigationView=view.findViewById(R.id.bottomNavigationViewHome);
        bottomNavigationView.setItemIconTintList(null);
        profilePicture=view.findViewById(R.id.profilePictureHome);
        btnSettings=view.findViewById(R.id.btnSettingsHome);
        point=view.findViewById(R.id.pointHome);
        homeRecView=view.findViewById(R.id.homeRecView);
        constraintLayout=view.findViewById(R.id.constraintLayoutHome);

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
                if(mUsers.isEmpty()){

                }else{
                    userAdapter=new HomeListAdapter(getContext(),mUsers, HomeActivity.this);
                    homeRecView.setAdapter(userAdapter);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void showFriendsActivities (){
        //display the friends activities that want us to be displayed
        //show the list from the
        for(int i=0;i<friendsActivitiesList.size();i++){
            //for every friend in the list that was filled
            //we go to the id's savad in the array and add them to the adapter

            userlist=new ArrayList<>();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference=FirebaseDatabase.getInstance().getReference("Users").child(friendsActivitiesList.get(i));

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);
                    userlist.add(user);
                    // TODO :there is an issue here that the reference is called for every friend and the friends is added to userlist
                    // TOdo : but userlist is somehow empty outside of addvalueeventListener and we set the adapter the same
                    // amount of times as we there are friends which might take a lot of space or time when the friends list is Long ????

                    //Reason
                    //
                    //Firebase has it's own async mechanism like volley library onDataChange function will
                    // be call when firebase gets some result from internet and execute the below code as normally
                    // in your case below code is your Log.d("after","value"+newDay.toString()); line.
                    //https://stackoverflow.com/questions/49092167/data-being-lost-after-addvalueeventlistener-firebase-android
                    if(userlist.isEmpty()){

                    }else{
                        userAdapter=new HomeListAdapter(getContext(),userlist, HomeActivity.this);
                        homeRecView.setAdapter(userAdapter);
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    public void showSnackbar() {
        //display a message about email verification if not verified .
        // It will display it every time you log in if you are not verified and there is no way to close it.
        Snackbar snackbar = Snackbar.make(constraintLayout, "Please, verify your e-mail address", Snackbar.LENGTH_INDEFINITE)
                .setAction("VERIFY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Snackbar snackbar1 = Snackbar.make(constraintLayout, "Email Sent", Snackbar.LENGTH_SHORT);
                                snackbar1.show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showSnackbar();
                            }
                        });


                    }
                });
        snackbar.show();
    }

    @Override
    public void onUserClicked(User user) {

    }
}