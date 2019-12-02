package com.example.friday_android;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AccuweatherAsyncTask extends AsyncTask<Void,String,Void> {


    IModifyUI modifyUI;
    String url;
    GsonWeatherParser parse;

    //TODO London city key 328328
    //api key jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP
    //TODO store key in keystore and city code in sharedprefs, also create method to get key from city name

    public AccuweatherAsyncTask(String url, IModifyUI modUI){
        modifyUI = modUI;
        this.url = url;
        //Jsonparser = parser;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();


            GsonWeatherParser js = new Gson().fromJson(jsonString, GsonWeatherParser.class);
            Log.d("Debug", jsonString);
            if (js.DailyForecasts == null) {
                throw new RequestsExceededException();
            }
            modifyUI.refreshWeatherDisplay(js);


        } catch (RequestsExceededException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            Log.d("Debug", "HTTP GET failed");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Debug", "HTTP GET failed");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
