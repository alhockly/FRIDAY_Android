package com.kushcabbage.friday_android.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kushcabbage.friday_android.IModifyUI;
import com.kushcabbage.friday_android.exceptions.RequestsExceededException;
import com.kushcabbage.friday_android.gsonParsers.GsonCurrentWeatherParser;
import com.kushcabbage.friday_android.gsonParsers.GsonWeatherForecastParser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AccuweatherForecastWeatherAsyncTask extends AsyncTask<Void,String,Void> {

    IModifyUI modifyUI;
    String url;
    String apiKey;


    String URL_END = "?metric=true&apikey=";
    String FORECAST_URL = "/forecasts/v1/daily/5day/";

    String BASE_URL = "https://dataservice.accuweather.com";
    String iLocationKey;

    GsonWeatherForecastParser weatherForcastJsonObj = null;


    //TODO store key in keystore and city code in sharedprefs, also create method to get key from ip address

    public AccuweatherForecastWeatherAsyncTask(String ApiKey, String location_key, IModifyUI modUI){
        modifyUI = modUI;
        iLocationKey = location_key;
        apiKey = ApiKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        url = BASE_URL+ FORECAST_URL + iLocationKey + URL_END + apiKey;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();

            weatherForcastJsonObj = new Gson().fromJson(jsonString, GsonWeatherForecastParser.class);

            if (weatherForcastJsonObj.DailyForecasts == null) {
                throw new RequestsExceededException();
            }


        } catch (RequestsExceededException e) {
            e.printToScreen(modifyUI);
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
        if(weatherForcastJsonObj != null){
            modifyUI.refreshForecastDisplay(weatherForcastJsonObj);
        }
    }
}
