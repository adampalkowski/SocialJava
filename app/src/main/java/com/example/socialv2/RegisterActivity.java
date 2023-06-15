package com.example.socialv2;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class RegisterActivity extends Fragment {
    private EditText edtEmail,edtPassword,edtPasswordConfirm,edtName,edtLastName;
    private TextView goToSignIn;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private FirebaseAuth auth;
    private Button registerBtn;
    private ImageButton btnBack;
    private FirebaseAuth fAuth;
     private String TAG="REGISTERACTIVITYX";

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        final NavController navController= Navigation.findNavController(view);


        DatabaseReference connectedRef = FirebaseDatabase.getInstance(adress).getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "connected");
                } else {
                    Log.d(TAG, "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });


        registerBtn.setOnClickListener(view1 -> {
            String email =edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String passwordConfirm=edtPasswordConfirm.getText().toString().trim();
            if(TextUtils.isEmpty(email)) {
                edtEmail.setError("email is required");
                return;
            }
            if(TextUtils.isEmpty(password)) {
                edtPassword.setError("password is required");
                return;
            }
            if(password.length()<6){
                edtPassword.setError("password not long enough");
                return;
            }
           // if(!passwordConfirm.equals(password)){
          //      edtPassword.setError("password not long enough");
           //     return;
           // }




            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){


                        FirebaseDatabase database= FirebaseDatabase.getInstance(adress);
                        DatabaseReference reference=database.getReference();


                        FirebaseUser firebaseUser = fAuth.getCurrentUser();
                        Log.d(TAG,"firebase user");
                        String userid=firebaseUser.getUid();


                        reference=database.getReference("Users").child(userid);
                        Log.d(TAG,reference.toString());
                        HashMap<String,String> hashMap= new HashMap<>();
                        hashMap.put("id",userid);
                        hashMap.put("name",edtName.getText().toString().trim());
                        hashMap.put("pictureUrl","default");
                        hashMap.put("status","false");
                        hashMap.put("search",edtName.getText().toString().trim().toLowerCase());


                        Log.d(TAG,hashMap.toString());
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                   navController.navigate(R.id.action_registerActivity_to_homeActivity);
                                }else{
                                    Log.d(TAG,"user register in the database");
                                }
                                Log.d(TAG,"YES");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"NO");
                            }
                        });
                        FirebaseDatabase database2 = FirebaseDatabase.getInstance(adress);
                        DatabaseReference reference2 = database2.getReference("Users").child(userid).child("usersActivity");

                        Log.d(TAG,reference2.toString());
                        HashMap<String,String> hashMap2= new HashMap<>();
                        hashMap2.put("date","");
                        hashMap2.put("startTimeHour","");
                        hashMap2.put("startTimeMinute","");
                        hashMap2.put("LengthHours","");
                        hashMap2.put("LengthMinutes","");
                        hashMap2.put("activityName","none");
                        hashMap2.put("location","");

                        //get all friends to an array so then you compare them with the friends chosen and set the values in the firebase-Friends to true or false


                        Log.d(TAG,hashMap2.toString());
                        reference2.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"NOFROM CReATING");
                }
            });

          //  register(edtName.getText().toString().trim(),email,password,view);

        });
        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_registerActivity_to_loginScreen);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_registerActivity_to_loginScreen);
            }
        });

    }

  /*  private void register(String username,String email,String password,View view){
        final NavController navController= Navigation.findNavController(view);

        fAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = fAuth.getCurrentUser();
                            Log.d(TAG,"firebase user");
                            String userid=firebaseUser.getUid();
                            Log.d(TAG,userid);
                            reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String,String> hashMap= new HashMap<>();
                            hashMap.put("id",userid.toString());
                            hashMap.put("username",username.toString().trim());
                            hashMap.put("imageURL","default");
                            Log.d(TAG,"whe are ere");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        navController.navigate(R.id.action_registerActivity_to_homeActivity);
                                    }else{
                                        Log.d(TAG,"YES");
                                    }
                                    Log.d(TAG,"YES");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"NO");
                                }
                            });
                        }
                    }
                });
    }*/


    private void initViews(View view) {
        edtName=view.findViewById(R.id.edtRegisterFirstName);
        edtLastName=view.findViewById(R.id.edtRegisterLastName);
        edtPasswordConfirm=view.findViewById(R.id.edtRegisterConfirmPassword);
        edtEmail=view.findViewById(R.id.edtRegisterEmail);
        edtPassword=view.findViewById(R.id.edtRegisterPassword);
        registerBtn=view.findViewById(R.id.btnSingUp);
        goToSignIn=view.findViewById(R.id.txtGoToSignIn);
        btnBack=view.findViewById(R.id.btnBackFromSignUp);
        fAuth=FirebaseAuth.getInstance();
    }

}

