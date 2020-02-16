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


public class AccuweatherAsyncTask extends AsyncTask<Void,String,Void> {

    IModifyUI modifyUI;
    String url;
    String apiKey;



    String URL_END = "?metric=true&apikey=";
    String FORECAST_URL = "/forecasts/v1/daily/5day/";

    String CURRENT_CONDITIONS_URL = "/currentconditions/v1/";
    String BASE_URL = "https://dataservice.accuweather.com";
    String iLocationKey;

    GsonWeatherForecastParser weatherForcastJsonObj = null;
    GsonCurrentWeatherParser currentWeatherJsonObj = null;

    //api key jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP
    //TODO store key in keystore and city code in sharedprefs, also create method to get key from city name

    public AccuweatherAsyncTask(String ApiKey, String location_key, IModifyUI modUI){
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
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            Log.d("Debug", "HTTP GET failed");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Debug", "HTTP GET failed");
            e.printStackTrace();
        }

        url = BASE_URL + CURRENT_CONDITIONS_URL + iLocationKey + URL_END + apiKey;
        Request currentConditionsRequest = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(currentConditionsRequest).execute();
            String jsonString = response.body().string();

            Type founderListType = new TypeToken<ArrayList<GsonCurrentWeatherParser>>(){}.getType();
            List<GsonCurrentWeatherParser> currentWeatherList = new Gson().fromJson(jsonString, founderListType);

            if (currentWeatherList == null) {
                throw new RequestsExceededException();
            }
            currentWeatherJsonObj = currentWeatherList.get(0);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (RequestsExceededException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(weatherForcastJsonObj.DailyForecasts != null && currentWeatherJsonObj != null){
            modifyUI.refreshWeatherDisplay(weatherForcastJsonObj,currentWeatherJsonObj);
        }
    }
}
