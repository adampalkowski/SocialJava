package com.example.socialv2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.socialv2.Adapters.CreateAddedUsersAdapter;
import com.example.socialv2.Adapters.CreateListAdapter;
import com.example.socialv2.Adapters.MarkerInfoWindowAdapter;
import com.example.socialv2.Model.Place;
import com.example.socialv2.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateActivity extends Fragment implements CalendarDialog.getDate,TimerDialog.getTime,AddFriendsDialog.goToHomePage //, OnMapReadyCallback {
{
        private ImageButton btnBack;
        private TextView createDate,createTime,timeLength;
        private TextView editMaxDesc,editMinDesc;
        private RelativeLayout btnCreate;
        private ConstraintLayout MinMaxContainer;
        private EditText edtGetStatus;
        private ImageView infoMinCount;
        private ImageView arrow;
        private CardView requirementsCardView;
        private TimerDialog bottomSheetTimer;
        private String TAG="CreateActivity";
        private String timeStartHour,timeStartMinute,lengthMinute,lengthHour,dateS;

        private       CalendarDialog bottomSheetCalendar;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  //  private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private String adress="https://socialv2-340711-default-rtdb.europe-west1.firebasedatabase.app/";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"sds");
        return inflater.inflate(R.layout.fragment_create_activity,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"sd2s");

        initViews(view);
        final NavController navController = Navigation.findNavController(getView());
     //   SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
      //          .findFragmentById(R.id.minimap);
      //  mapFragment.getMapAsync(CreateActivity.this);
        timeStartMinute=null;
        timeStartHour=null;
        lengthMinute="0";
        lengthHour="0";
        dateS=null;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_createActivity_to_homeActivity);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showBottomSheetDialogFragment();



            }
        });

        timeLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomSheetDialogTimeLength();
            }
        });

        final Calendar myCalendar= Calendar.getInstance();

        edtGetStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel(myCalendar);
            }
        };
        infoMinCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   editMinDesc.setVisibility(View.VISIBLE);
                  infoMinCount.setVisibility(View.GONE);

            }
        });

        requirementsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (MinMaxContainer.getVisibility() == View.VISIBLE) {

                    // The transition of the hiddenView is carried out
                    //  by the TransitionManager class.
                    // Here we use an object of the AutoTransition
                    // Class to create a default transition.
                    TransitionManager.beginDelayedTransition(requirementsCardView,
                            new AutoTransition());
                    MinMaxContainer.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_rdown);
                    requirementsCardView.setCardElevation(0);
                    editMinDesc.setVisibility(View.INVISIBLE);
                    infoMinCount.setVisibility(View.VISIBLE);
                }else {
                    requirementsCardView.setCardElevation(5);
                    TransitionManager.beginDelayedTransition(requirementsCardView,
                            new AutoTransition());
                    MinMaxContainer.setVisibility(View.VISIBLE);

                    arrow.setImageResource(R.drawable.ic_up);
                }

            }
        });

        createDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomSheetDialogCalendar();


            }
        });

        createTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialogTimer();
            }
        });
    }

    private void hideKeyboard(View v) {

            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);


    }

    private void updateLabel(Calendar myCalendar){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.GERMAN);
        createDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void showBottomSheetDialogFragment() {
        if(edtGetStatus.getText().toString().trim()!=""&&edtGetStatus.getText().toString().trim()!=null){
            if(timeStartHour!=null){
                AddFriendsDialog bottomSheetFragment = new AddFriendsDialog(edtGetStatus.getText().toString(),dateS,timeStartMinute,timeStartHour,lengthHour,lengthMinute,"latlng",CreateActivity.this);
                bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
            }else{
                //todo SHOW ALERT
            }

        }else{
            edtGetStatus.setText("Set activity description");
        }
    }
    public void showBottomSheetDialogCalendar() {
        bottomSheetCalendar.show(getFragmentManager(), bottomSheetCalendar.getTag());
    }
    public void hideBottomSheetDialogCalendar() {
        bottomSheetCalendar.dismiss();
    }

    public void showBottomSheetDialogTimer() {
        bottomSheetTimer = new TimerDialog(CreateActivity.this,1);
        bottomSheetTimer.show(getFragmentManager(), bottomSheetTimer.getTag());
    }

    public void showBottomSheetDialogTimeLength() {
        bottomSheetTimer = new TimerDialog(CreateActivity.this,2);
        bottomSheetTimer.show(getFragmentManager(), bottomSheetTimer.getTag());
    }
    public void closeTimer() {
        bottomSheetTimer.dismiss();
    }

    private void initViews(View view) {
        btnCreate=view.findViewById(R.id.invitefriendslayout);
        arrow=view.findViewById(R.id.arrowRequirements);
        timeLength=view.findViewById(R.id.timeLength);
        createDate=view.findViewById(R.id.createDate);
        editMinDesc=view.findViewById(R.id.editMinDesc);
        MinMaxContainer=view.findViewById(R.id.MinMaxContainer);
        btnBack=view.findViewById(R.id.btnBackFromCreate);
        createTime=view.findViewById(R.id.timer);
        infoMinCount=view.findViewById(R.id.infoMinCount);
        requirementsCardView=view.findViewById(R.id.requirementsCardView);
        edtGetStatus=view.findViewById(R.id.editTextCreate);
        bottomSheetCalendar = new CalendarDialog(CreateActivity.this);
    }

    @Override
    public void onDateClicked(String date) {
        createDate.setText(date);
        hideBottomSheetDialogCalendar();
        dateS=date;

    }

    @Override
    public void onTimeClicked(String hour, String minute,int state) {
        if(state==1){
            timeStartHour=hour;
            timeStartMinute=minute;
            if(Integer.parseInt(hour)<10){
                if(Integer.parseInt(minute)<10){
                    createTime.setText("0"+hour+":"+"0"+minute);
                }else{
                    createTime.setText("0"+hour+":"+minute);
                }
            }else{
                if(Integer.parseInt(minute)<10){
                    createTime.setText(hour+":"+"0"+minute);
                }else{
                    createTime.setText(hour+":"+minute);
                }
            }

        }else{
            lengthMinute=minute;
            lengthHour=hour;
            if(Integer.parseInt(minute)==0){


                if(Integer.parseInt(hour)==1){
                    timeLength.setText(hour+" hour");
                }else{

                    timeLength.setText(hour+" hours");
                }
            }else {
                if(Integer.parseInt(hour) == 1) {
                    timeLength.setText(hour + " hour " + minute + " minutes");
                }else if(Integer.parseInt(hour) == 0){
                    timeLength.setText( minute + " minutes");
                }

                else {

                    timeLength.setText(hour + " hours " + minute + " minutes");
                }
            }



        }
        closeTimer();
    }

    //@Override
   // public void onMapReady(@NonNull GoogleMap googleMap) {
       // fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
       // mMap = googleMap;
     //   mMap.getUiSettings().setMapToolbarEnabled(false);
     //   getCurrentLocation();
   // }
 /*   @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Double currentLatitude = location.getLatitude();
                                    Double currentLongitude = location.getLongitude();

                                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);

                                    Place place = new Place("someplace", currentLocation, currentLocation, 1.0f);


                                    MarkerOptions markerOptions = new MarkerOptions().title("place")
                                            .position(currentLocation)
                                            .icon(getMarkerIconFromDrawable(getContext(),R.drawable.ic_person_waving))
                                            .snippet("Thinking of finding some thing...");
                                    MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getContext());
                                    mMap.setInfoWindowAdapter(markerInfoWindowAdapter);
                                    Marker mMarker = mMap.addMarker(markerOptions);
                                    mMarker.setTag(place);


                                    //  mMap.setOnMyLocationButtonClickListener(getActivity());
                                    //mMap.setOnMyLocationClickListener(getActivity());


                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                                    mMap.getUiSettings().setZoomControlsEnabled(true);
                                }

                            }
                        }); //override methods
            } else

            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }


    }
    */

    private BitmapDescriptor getMarkerIconFromDrawable(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onFinish(int x) {
        if(x==1){
             final NavController navController = Navigation.findNavController(getView());
             navController.navigate(R.id.action_createActivity_to_homeActivity);
        }
    }
}
