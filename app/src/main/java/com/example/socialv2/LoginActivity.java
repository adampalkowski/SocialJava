package com.example.socialv2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;
import com.example.socialv2.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends Fragment {
    private static final String TAG = "StartActivity";
    private CheckBox rememberMe;
    private Button btnSignIn;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private EditText edtEmail,edtPassword;
    private TextView goToSingUp,singInWithGoogle;
    private FirebaseAuth fAuth;
    private ImageView existingProfileImage;
    private TextView txtForgotPassword,existingaccountEmail;
    private LayoutInflater inflater;
    private FirebaseAuth mAuth;
    private ConstraintLayout layoutSinginWithExistingAccount;
    public static final String PREFS_NAME = "checkbox";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_PICTURE = "picture";
    private static final String Encrypted_prefs = "encrypted_storage.txt";
    private   AlertDialog.Builder reset_alert;
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        Log.d(TAG,"start page");
        final NavController navController= Navigation.findNavController(view);
        if(fAuth.getCurrentUser()!=null){
            navController.navigate(R.id.action_loginScreen_to_homeActivity);
            return;
       }
       // SharedPreferences preferences= getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
       // preferences.edit().clear().commit() ;
      //  SharedPreferences loginprefs=getActivity().getSharedPreferences(Encrypted_prefs,Context.MODE_PRIVATE);
      //  loginprefs.edit().clear().commit();
        SharedPreferences preferences= getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String checkbox=preferences.getString("remember","");
        if(checkbox.equals("true")){
           try{
               String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
               SharedPreferences loginprefs = EncryptedSharedPreferences.create(
                       Encrypted_prefs,
                       masterKeyAlias,
                       getContext(),
                       EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                       EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );
                       String rlogin=loginprefs.getString(PREF_USERNAME,"");
                       String picture=loginprefs.getString(PREF_PICTURE,"");
                       existingaccountEmail.setText(rlogin);
                       Log.d(TAG,picture+"sdsd");
                       if(picture!=""&&picture!=null){
                           Log.d(TAG,picture);
                           Glide.with(getContext()).load(picture).into(existingProfileImage);
                       }else if(picture=="default"){
                           existingProfileImage.setImageResource(R.drawable.ic_profile);
                       }

           }catch (Exception e){

           }




            layoutSinginWithExistingAccount.setVisibility(View.VISIBLE);

        }else if(checkbox.equals("false")){
            layoutSinginWithExistingAccount.setVisibility(View.GONE);
        }


        layoutSinginWithExistingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                    SharedPreferences loginprefs = EncryptedSharedPreferences.create(
                            Encrypted_prefs,
                            masterKeyAlias,
                            getContext(),
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                    String rlogin = loginprefs.getString(PREF_USERNAME, "");
                    String rpassword = loginprefs.getString(PREF_PASSWORD, "");
                    logIn(rlogin, rpassword, view);
                }catch (Exception e ){

                }

            }
        });

            //todo LOGIN WITH GOOGLE AND FACEBOOK

        goToSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginScreen_to_registerActivity);
                Log.d(TAG,"clicked sign up");
            }
        });

        // NOTE LOGIN AND PASSWORD STORED IN SHARED PREFS ARE PLAIN TEXT THEY PROBABLY SHOULD BE ENCRYPTED




        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                }else if (!compoundButton.isChecked()){
                    SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                }


            }
        });




        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"login clicked");
                String email =edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    edtEmail.setError("email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    edtPassword.setError("password is required");
                    return;
                }

                logIn(email,password,view);

                Log.d(TAG,"logging");
               if(rememberMe.isChecked()){

                   Log.d(TAG,"checked");   //encrypt the shared prefs password
                try{
                 //  String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                    //SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                          //  Encrypted_prefs,
                          //  masterKeyAlias,
                          //  getContext(),
                         //   EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        //   EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                  //  );
                  //  sharedPreferences.edit().clear().commit();




                  String  masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                     SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                            Encrypted_prefs,
                            masterKeyAlias,
                            getContext(),
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString(PREF_USERNAME, email);
                    editor.putString(PREF_PASSWORD, password);


                    editor.apply();
                    Log.d(TAG,"SUCCESSFUL ENCRYPT");
                }catch (Exception e){
                }
               }

            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view2 =inflater.inflate(R.layout.reset_pop,null);
                reset_alert.setTitle("Reset Forgot Password")
                        .setMessage("enter your Email  to get Password reset link")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText email= view2.findViewById(R.id.resetEmailPop);
                                if(email.getText().toString().trim().isEmpty()) {
                                    email.setError("require email");
                                }
                                fAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //todo show some error that notifies user that there is no such emial
                                    }
                                });
                            }

                        }).setNegativeButton("Cancel",null).setView(view2)
                        .create().show();
            }
        });



    }

    private void initViews(View view) {
        mAuth = FirebaseAuth.getInstance();
        goToSingUp=view.findViewById(R.id.txtGoToSignUp);
        rememberMe=view.findViewById(R.id.rememberMe);
        layoutSinginWithExistingAccount=view.findViewById(R.id.layoutSignInWithExistinAccount);
        btnSignIn=view.findViewById(R.id.btnConfirmSignIn);
        edtEmail=view.findViewById(R.id.edtLoginEmail);
        singInWithGoogle=view.findViewById(R.id.signInWithGoogle);
        edtPassword=view.findViewById(R.id.edtLoginPassword);
        existingaccountEmail=view.findViewById(R.id.existingAccountEmail);
        existingProfileImage=view.findViewById(R.id.existingProfileImage);
        fAuth=FirebaseAuth.getInstance();
        inflater=this.getLayoutInflater();
        reset_alert=new AlertDialog.Builder(getContext());
        txtForgotPassword=view.findViewById(R.id.txtLoginForgotPassword);

    }

    private void logIn(String email,String password,View view){
        final NavController navController= Navigation.findNavController(view);
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    String userId=fAuth.getUid();
                    //get the database reference to access the current users data
                    DatabaseReference reference = FirebaseDatabase.getInstance(adress)
                            .getReference("Users").child(userId);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user= snapshot.getValue(User.class) ;

                            //NOTE IT DOES SET THE ENCRYPTED PREFERENCE EVERY TIME YOU
                            String picture =user.getPictureUrl().toString();
                           try {
                               String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                               SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                                       Encrypted_prefs,
                                       masterKeyAlias,
                                       getContext(),
                                       EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                       EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                               );
                               SharedPreferences.Editor editor = sharedPreferences.edit();
                               editor.putString(PREF_PICTURE, picture);
                               editor.apply();

                           }
                            catch (Exception e){

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    navController.navigate(R.id.action_loginScreen_to_homeActivity);

                }else{
                    edtEmail.setError("WRONG EMAIL OR PASSWORD");
                }
            }
        });
    }
}
