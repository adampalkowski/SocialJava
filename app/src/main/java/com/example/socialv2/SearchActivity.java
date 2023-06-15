package com.example.socialv2;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.example.socialv2.Adapters.UsersListAdapter;
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

public class SearchActivity extends Fragment implements  UsersListAdapter.GetUserToChatSearch{
    private String TAG="SearchActivity";


    private CoordinatorLayout constraintLayout;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private RecyclerView invitesRecViewer,proposedRecViewer;


    private ImageView cancelSearch;
    private FirebaseUser firebaseUser;
    private BottomAppBar bottomAppBar;
    private DatabaseReference reference;
    private ArrayList<User> userlist;
    private UsersListAdapter userAdapter;
    private BottomNavigationView bottomNavigationView;
    private EditText search_users;
    private TextView statusTextFriends;

    private ArrayList<User> mUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        initViews(view);
        initiateBottomAppBar(navController);
        Log.d(TAG,"INFLATOR");
        //recycler viewer for the friend list display
        proposedRecViewer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //firebase user needed to get the current user that is logged into the app
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId=firebaseUser.getUid();
        //initalize an empty array
        userlist = new ArrayList<>();

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setIcon(R.drawable.ic_icon_search_blue);


        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_users.setText("");
            }
        });

        //get the database reference to access the current users data
        DatabaseReference reference2 = FirebaseDatabase.getInstance(adress)
                .getReference("Users").child(userId);

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



        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d(TAG,"reference call");


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
    }



    private void initiateBottomAppBar(NavController navController){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //    case R.id.eventsActivity:
                    //  navController.navigate(R.id.action_homeActivity_to_eventsActivity);
                    // break;
                    case R.id.friends:
                        navController.navigate(R.id.action_searchActivity_to_friendsActivity);

                        break;
                    case R.id.home:
                        navController.navigate(R.id.action_searchActivity_to_homeActivity);

                        break;
                    case R.id.eventsActivity:
                        navController.navigate(R.id.action_searchActivity_to_eventsActivity);
                        break;
                    default:
                }
                return false;
            }
        });
    }

    private void updateToken(Task<String > task) {
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
                userAdapter=new UsersListAdapter(getContext(),mUsers, SearchActivity.this,firebaseUser.getUid());
                proposedRecViewer.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void searchUsers(String s) {
        final   FirebaseUser firebaseUserx = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUserx.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance(adress).getReference();
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
                userAdapter=new UsersListAdapter(getContext(),mUsers, SearchActivity.this,firebaseUser.getUid());
                proposedRecViewer.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews(View view) {
        constraintLayout = view.findViewById(R.id.searchContainer);
        invitesRecViewer = view.findViewById(R.id.recyclerInvitesFromFriends);
        proposedRecViewer = view.findViewById(R.id.recyclerProposedFriends);
        bottomAppBar = view.findViewById(R.id.bottomAppBarFriendsSearch);
        //point=view.findViewById(R.id.pointFriends);
        search_users=view.findViewById(R.id.friendsSearch2);
        bottomNavigationView=view.findViewById(R.id.bottomNavigationViewFriends);
        bottomNavigationView.setItemIconTintList(null);
        cancelSearch=view.findViewById(R.id.cancelSearch);
    }

    @Override
    public void onUserClicked(User user) {
        final NavController navController = Navigation.findNavController(getView());
        String id=user.getId().toString();

        SearchActivityDirections.ActionSearchActivityToChatActivity action= SearchActivityDirections.actionSearchActivityToChatActivity(user) ;

        navController.navigate(action);
    }
}