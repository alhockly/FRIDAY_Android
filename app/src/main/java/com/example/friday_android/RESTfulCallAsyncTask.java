package com.example.friday_android;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RESTfulCallAsyncTask extends AsyncTask<Void,String,Void> {

    IModifyUI modifyUI;
    String url;





    public RESTfulCallAsyncTask(IModifyUI modUI){
        modifyUI = modUI;

    }



    @Override
    protected Void doInBackground(Void... voids) {




       return null;

    }


    public Response OkHttpCall() throws IOException {
        Response response=null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();


            response = client.newCall(request).execute();
            String jsonString = response.body().string();




        return response;
    }


}
