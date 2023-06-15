package com.example.socialv2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialv2.Model.Place;
import com.example.socialv2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private Context context;
    public MarkerInfoWindowAdapter(Context context) {
        this.context=context;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {

        Place place  =(Place) marker.getTag();

        View view=View.inflate(context,R.layout.marker_info_contents,null);
        TextView title =view.findViewById(R.id.text_view_title);
        title.setText(place.getName());


        TextView address =view.findViewById(R.id.text_view_address);
        address.setText(place.getLatLng().toString());

        TextView rating =view.findViewById(R.id.text_view_rating);
        rating.setText(place.getRating().toString());

        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

}



