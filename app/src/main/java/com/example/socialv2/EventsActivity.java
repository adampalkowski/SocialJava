package com.example.socialv2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.socialv2.Adapters.MarkerInfoWindowAdapter;
import com.example.socialv2.Model.Place;
import com.example.socialv2.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.http.Url;


public class EventsActivity extends Fragment implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private String TAG = "EventsACtivity";
    private ImageButton btnBack;
    private BottomNavigationView bottomNavigationView;
    private GoogleMap mMap;
    private ImageButton activateLocationBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        initViews(view);

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );


// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });


        activateLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:

                        navController.navigate(R.id.action_eventsActivity_to_homeActivity);
                        break;
                    case R.id.friends:
                        navController.navigate(R.id.action_eventsActivity_to_friendsActivity);
                        break;
                    case R.id.search:
                        navController.navigate(R.id.action_eventsActivity_to_searchActivity);
                        break;
                    default:
                }
                return false;
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_eventsActivity_to_homeActivity);
            }
        });
    }


    private void initViews(View view) {
        Log.d(TAG, "sdsdssd");
        bottomNavigationView = view.findViewById(R.id.bottomNavigationViewEvents);
        bottomNavigationView.setItemIconTintList(null);
        btnBack = view.findViewById(R.id.btnBackFromEvents);
        activateLocationBtn = view.findViewById(R.id.btnActivateLocation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setIcon(R.drawable.ic_icon_event_blue);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(EventsActivity.this);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation");
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "ACCESS_COARSE_LOCATION");
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "ACCESS_FINE_LOCATION");
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d(TAG, location.toString());

                                    Double currentLatitude = location.getLatitude();
                                    Double currentLongitude = location.getLongitude();

                                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);

                                    Place place = new Place("someplace", currentLocation, currentLocation, 1.0f);


                                    MarkerOptions markerOptions = new MarkerOptions().title("place")
                                            .position(currentLocation)
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


            @Override
            public void onMapReady (@NonNull GoogleMap googleMap){
                Drawable markerIcon = getResources().getDrawable(R.drawable.ic_person_waving);
                BitmapDescriptor icon = getMarkerIconFromDrawable(markerIcon);


                String[] latlong = "-34.8799074,174.7565664".split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng location = new LatLng(latitude, longitude);
                Place place = new Place("someplace", location, location, 1.0f);
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // MarkerOptions markerOptions = new MarkerOptions().title(place.getName())
                //      .icon(icon)
                //       .position(place.getLatLng())
                //       .snippet("Thinking of finding some thing...");
                //  MarkerInfoWindowAdapter markerInfoWindowAdapter=new MarkerInfoWindowAdapter(getContext());
                //  mMap.setInfoWindowAdapter(markerInfoWindowAdapter);
                //   Marker mMarker = mMap.addMarker(markerOptions);

                mMap.setOnMyLocationButtonClickListener(this);
                mMap.setOnMyLocationClickListener(this);


                // mMarker.setTag(place);

                //  mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.getUiSettings().setZoomControlsEnabled(true);

            }

            private BitmapDescriptor getMarkerIconFromDrawable (Drawable drawable){
                Canvas canvas = new Canvas();
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                canvas.setBitmap(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                return BitmapDescriptorFactory.fromBitmap(bitmap);
            }


            @Override
            public boolean onMyLocationButtonClick () {
                return false;
            }

            @Override
            public void onMyLocationClick (@NonNull Location location){

            }
        }