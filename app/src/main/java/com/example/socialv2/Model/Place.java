package com.example.socialv2.Model;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private String name;
    private LatLng LatLng;
    private LatLng address;
    private Float rating;

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", LatLng=" + LatLng +
                ", address=" + address +
                ", rating=" + rating +
                '}';
    }

    public Place(String name, com.google.android.gms.maps.model.LatLng latLng, com.google.android.gms.maps.model.LatLng address, Float rating) {
        this.name = name;
        LatLng = latLng;
        this.address = address;
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatLng(com.google.android.gms.maps.model.LatLng latLng) {
        LatLng = latLng;
    }

    public void setAddress(com.google.android.gms.maps.model.LatLng address) {
        this.address = address;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public com.google.android.gms.maps.model.LatLng getLatLng() {
        return LatLng;
    }

    public com.google.android.gms.maps.model.LatLng getAddress() {
        return address;
    }

    public Float getRating() {
        return rating;
    }
}
