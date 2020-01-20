package com.example.friday_android;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AccuweatherAsyncTask extends AsyncTask<Void,String,Void> {

    IModifyUI modifyUI;
    String url;

    String accuWeatherKey = "jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP";

    String URL_END = "?metric=true&apikey=";
    String FORECAST_URL = "/forecasts/v1/daily/5day/";

    String CURRENT_CONDITIONS_URL = "/currentconditions/v1/";
    String BASE_URL = "http://dataservice.accuweather.com";
    String iLocationKey;


    //TODO London city key 328328
    //api key jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP
    //TODO store key in keystore and city code in sharedprefs, also create method to get key from city name

    public AccuweatherAsyncTask(String location_key, IModifyUI modUI){
        modifyUI = modUI;
        iLocationKey = location_key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        url = BASE_URL+ FORECAST_URL + iLocationKey + URL_END + accuWeatherKey;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        GsonWeatherForecastParser weatherForcastJsonObj = null;
        GsonCurrentWeatherParser currentWeatherJsonObj = null;

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

        url = BASE_URL + CURRENT_CONDITIONS_URL + iLocationKey + URL_END + accuWeatherKey;
        Request currentConditionsRequest = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(currentConditionsRequest).execute();
            String jsonString = response.body().string();
            currentWeatherJsonObj = new Gson().fromJson(jsonString, GsonCurrentWeatherParser.class);
            Log.d("Debug", jsonString);
            if (currentWeatherJsonObj.Temperature == null) {
                throw new RequestsExceededException();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RequestsExceededException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }


        modifyUI.refreshWeatherDisplay(weatherForcastJsonObj,currentWeatherJsonObj);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
