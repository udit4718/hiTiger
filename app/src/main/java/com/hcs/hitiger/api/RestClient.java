package com.hcs.hitiger.api;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcs.hitiger.HitigerApplication;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class RestClient {
    private static final String BASE_URL = "http://54.213.224.190:8080/api/mobile";
    private ApiService apiService;

    public RestClient() {
        GsonBuilder gsonBuilder = HitigerApplication.getInstance().getGsonBuilder();
        Gson gson = gsonBuilder
                .create();
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        client.setRetryOnConnectionFailure(false);
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(20, TimeUnit.SECONDS);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(client))
                .setConverter(new GsonConverter(gson))
                .build();
        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }
}
