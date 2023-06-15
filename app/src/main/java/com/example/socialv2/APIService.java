package com.example.socialv2;

import com.example.socialv2.Notification.Notification.MyResponse;
import com.example.socialv2.Notification.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({

    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
