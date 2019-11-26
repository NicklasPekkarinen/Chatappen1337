package com.autorave.chatapp.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAACY9m_tQ:APA91bHLpfsemb596LrCvof95qgML0ck04fT1fI-u1KVu397cW6GUYPkLap_SmNPR_aChgwKqLxkQh079zuHzAj1KTxdjTLCFdj1KdvZTx1t3Yy6-62iUfZ7AjUAGWeG4Kfodi7Vp1c3"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
