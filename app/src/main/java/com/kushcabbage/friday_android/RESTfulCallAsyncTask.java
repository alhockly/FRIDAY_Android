package com.kushcabbage.friday_android;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RESTfulCallAsyncTask extends AsyncTask<Void, String, Void> {

    IModifyUI modifyUI;
    String url;


    public RESTfulCallAsyncTask(IModifyUI modUI) {
        modifyUI = modUI;

    }


    @Override
    protected Void doInBackground(Void... voids) {


        return null;

    }


    public Response OkHttpCall() throws IOException {
        Response response = null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();


        response = client.newCall(request).execute();
        String jsonString = response.body().string();


        return response;
    }


}
