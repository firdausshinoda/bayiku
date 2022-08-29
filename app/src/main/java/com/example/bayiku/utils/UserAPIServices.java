package com.example.bayiku.utils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPIServices {

    @POST("get-nama-bayi")
    Call<ResponseBody> search_nama(@Body RequestBody file);

    @POST("save-bobot")
    Call<ResponseBody> saveBobot(@Body RequestBody file);

}