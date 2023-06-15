package com.example.socialv2;

import static com.example.socialv2.LoginActivity.PREFS_NAME;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends Fragment {
    private static final String Encrypted_prefs = "encrypted_storage.txt";
    private ImageButton btnBack,btnResetPassword,btnUpdateEmail,btnDeleteAccount,btnDeleteLoginInformation;
    private TextView txtResetPassword,txtDeleteAccount,txtUpdateEmail,txtDeleteLoginInformation;
    private FirebaseAuth auth;
    private LayoutInflater inflater;
    private   AlertDialog.Builder reset_alert;
    private TextView btnLogOut,darkModeText;
    private Switch darkModeSwitch;
    private String TAG="SettingsActivityx";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
          initViews(view);
        final NavController navController= Navigation.findNavController(view);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_settingsActivity_to_homeActivity);
            }
        });
        txtResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetPassword(navController);

            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword(navController);
            }
        });

        txtDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(navController);
            }
        });
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(navController);
            }
        });

        //UPDATING EMAIL IN SETTINGS DONT KNOW IF IMPORTANT MIGHT BE USELESS
        txtUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmail(navController);
            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmail(navController);

            }
        });
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                  darkModeText.setText("On");
                } else {
                    darkModeText.setText("Off");
                }
            }
        });
        Log.d(TAG,"desdsddsdsgin info");
        btnDeleteLoginInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"deleteing login info");
                deleteLoginInformation();
            }
        });
        txtDeleteLoginInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"deleteing login info");
                deleteLoginInformation();
            }
        });
        txtDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLoginInformation();
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                navController.navigate(R.id.action_settingsActivity_to_startScreen);
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmail(navController);
            }
        });
    }

    private void deleteAccount(NavController navController){

        reset_alert.setTitle("Delete account permanently ?")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user=auth.getCurrentUser();
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                auth.signOut();
                                navController.navigate(R.id.action_settingsActivity_to_startScreen);
                            }
                        });
                    }
                }).setNegativeButton("Cancel",null)
                .create().show();
    }
    private void resetPassword(NavController navController){
        View view2 =inflater.inflate(R.layout.reset_pop_two,null);
        reset_alert.setTitle("Reset Forgot Password")
                .setMessage("enter your Email  to get Password reset link")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText email= view2.findViewById(R.id.resetEmailPop);
                        if(email.getText().toString().trim().isEmpty()) {
                            email.setError("require email");
                        }
                        auth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    private void updateEmail(NavController navController){

        View view2 =inflater.inflate(R.layout.reset_pop_two,null);
        reset_alert.setTitle("Update your Email")
                .setMessage("enter your new Email")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText email= view2.findViewById(R.id.resetEmailPop);
                        if(email.getText().toString().trim().isEmpty()) {
                            email.setError("require email");
                        }
                        FirebaseUser user= auth.getCurrentUser();
                        user.updateEmail(email.toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                }).setNegativeButton("Cancel",null).setView(view2)
                .create().show();
    }
    private void deleteLoginInformation(){

       try {

           String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
           SharedPreferences loginprefs = EncryptedSharedPreferences.create(
                   Encrypted_prefs,
                   masterKeyAlias,
                   getContext(),
                   EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                   EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
           loginprefs.edit().clear().commit();
           SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
           SharedPreferences.Editor editor= preferences.edit();
           editor.putString("remember","false");
           editor.apply();
       }catch (Exception e){

       }
    }
    private void initViews(View view) {
        darkModeSwitch=view.findViewById(R.id.darkModeSwitch);
        btnResetPassword=view.findViewById(R.id.ResetPasswordBtn);
        btnDeleteAccount=view.findViewById(R.id.DeleteAccountBtn);
        btnUpdateEmail=view.findViewById(R.id.UpdateEmailBtn);
        darkModeText=view.findViewById(R.id.darkModeText);

        btnDeleteLoginInformation=view.findViewById(R.id.deleteLoginInformationBtn);
        txtDeleteLoginInformation=view.findViewById(R.id.btnDeleteSheredPrefs);

        btnBack=view.findViewById(R.id.btnSettingsBack);

        txtResetPassword=view.findViewById(R.id.txtSettingsResetPassword);
        auth=FirebaseAuth.getInstance();
        inflater=this.getLayoutInflater();
        btnLogOut=view.findViewById(R.id.btnLogOut);
        reset_alert=new AlertDialog.Builder(getContext());
        txtDeleteAccount=view.findViewById(R.id.txtSettingsDeleteAccount);
        txtUpdateEmail=view.findViewById(R.id.txtSettingsUpdateEmail);
    }
}
