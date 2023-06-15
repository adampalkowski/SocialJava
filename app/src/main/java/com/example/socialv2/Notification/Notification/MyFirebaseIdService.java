package com.example.socialv2.Notification.Notification;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            updateToken(s);
        }
    }

    private void updateToken(String s) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token= new Token(s);
        assert firebaseUser != null;
        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
