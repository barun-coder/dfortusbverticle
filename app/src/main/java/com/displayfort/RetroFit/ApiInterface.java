package com.displayfort.RetroFit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by prashantm on 10/27/2016.
 */
public interface ApiInterface {
    @Headers({"Content-Type: application/json"})
    @POST
    Call<JsonElement> postData(@Url String remainingURL, @Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @PUT
    Call<JsonElement> putData(@Url String remainingURL, @Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<JsonElement> postData(@Url String remainingURL, @Body JsonObject jsonObject, @Header("Authorization") String session_key);

    @Headers({"Content-Type: application/json"})
    @PUT
    Call<JsonElement> putData(@Url String remainingURL, @Body JsonObject jsonObject, @Header("Authorization") String session_key);

    @GET
    Call<JsonElement> getData(@Url String remainingURL, @Header("Authorization") String session_key);

    @DELETE
    Call<JsonElement> deleteData(@Url String remainingURL, @Header("Authorization") String session_key);

    @Multipart
    @POST
    Call<JsonElement> uploadImage(@Url String remainingURL, @Part MultipartBody.Part file, @Body ProgressRequestBody body, @Header("Authorization") String session_key);

    @Multipart
    @POST
    Call<JsonElement> uploadImage(@Url String remainingURL, @Part MultipartBody.Part file, @Header("Authorization") String session_key);


    @GET
    Call<JsonElement> getBanner(@Url String remainingURL, @Header("Authorization") String authHeader);
}


