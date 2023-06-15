package com.example.socialv2.Model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private int age;
    private String id;
    private String name;
    private  String email;
    private String pictureUrl;
    private String status;
    private String search;
    private UserActivity usersActivity;

    public User(String id, String name, int age, String email, String pictureUrl, String search, UserActivity usersActivity){
        this.id=id;
        this.age=age;
        this. name=name;
        this. email=email;
        this. pictureUrl=pictureUrl;
        this. search=search;
        this.usersActivity=usersActivity;

    }

    public User() {
    }

    protected User(Parcel in) {
        age = in.readInt();
        id = in.readString();
        name = in.readString();
        email = in.readString();
        pictureUrl = in.readString();
        status = in.readString();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public UserActivity getUsersActivity() {
        return usersActivity;
    }

    public void setUsersActivity(UserActivity usersActivity) {
        this.usersActivity = usersActivity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAge() {
            return age;
        }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(age);
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(pictureUrl);
        parcel.writeString(status);
    }
}
