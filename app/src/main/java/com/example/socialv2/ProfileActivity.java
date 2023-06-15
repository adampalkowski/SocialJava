package com.example.socialv2;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.socialv2.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileActivity extends Fragment {
    private ImageButton btnBack;
    private String TAG="PROFILEACTIVITY";
    private ImageView profilePicture;
    private TextView changeProfilePicture,profileName;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    private StorageTask uploadTask;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        final NavController navController= Navigation.findNavController(view);

        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        fuser=FirebaseAuth.getInstance().getCurrentUser();

        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.d(TAG,user.toString()+"S");


              profileName.setText(user.getName());
               if(user.getPictureUrl().equals("default")){
                profilePicture.setImageResource(R.drawable.ic_profile);
              }else{
                Glide.with(getContext()).load(user.getPictureUrl()).into(profilePicture);
             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   FirebaseDatabase database= FirebaseDatabase.getInstance(adress);
                DatabaseReference reference=database.getReference();


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid=firebaseUser.getUid();


                reference=database.getReference("Users").child(userid).child("imageUrl");
                reference.setValue("");
                */
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_profileActivity_to_homeActivity);
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(Uri resultUri){
        final ProgressDialog  pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        imageUri=resultUri;
        if(imageUri!=null){
            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task <UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();

                        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("pictureUrl",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }else{
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }else{


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== IMAGE_REQUEST&&resultCode==RESULT_OK
        && data!=null &&data.getData()!=null){
            imageUri=data.getData();
            if(uploadTask!=null &&uploadTask.isInProgress()){

            }else{
                cropImage();

            }

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
        //CIRCULAR CROP IMAGE
    private void cropImage() {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1000,1000)
                .setCropShape(CropImageView.CropShape.OVAL)
        .start(getContext(),this);
    }

    private void initViews(View view) {
        changeProfilePicture=view.findViewById(R.id.profileChangeProfilePicture);
        profileName=view.findViewById(R.id.profileName);
        profilePicture=view.findViewById(R.id.profileImage);

        btnBack=view.findViewById(R.id.btnProfileBack);
    }
}
