package com.kushcabbage.friday_android.AsyncTasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kushcabbage.friday_android.IModifyUI;
import com.kushcabbage.friday_android.exceptions.RequestsExceededException;
import com.kushcabbage.friday_android.gsonParsers.GsonCurrentWeatherParser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AccuweatherCurrentWeatherAsyncTask extends AsyncTask<Void, String, Void> {

    IModifyUI modifyUI;
    String url;
    String apiKey;

    Exception exception = null;

    String URL_END = "?metric=true&apikey=";

    String CURRENT_CONDITIONS_URL = "/currentconditions/v1/";
    String BASE_URL = "https://dataservice.accuweather.com";
    String iLocationKey;

    GsonCurrentWeatherParser currentWeatherJsonObj = null;

    //TODO store key in keystore and city code in sharedprefs, also create method to get key from ip address

    public AccuweatherCurrentWeatherAsyncTask(String ApiKey, String location_key, IModifyUI modUI) {
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


        url = BASE_URL + CURRENT_CONDITIONS_URL + iLocationKey + URL_END + apiKey;
        OkHttpClient client = new OkHttpClient();
        Request currentConditionsRequest = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(currentConditionsRequest).execute();
            String jsonString = response.body().string();

            Type currentWeatherArrayType = new TypeToken<ArrayList<GsonCurrentWeatherParser>>() {
            }.getType();
            List<GsonCurrentWeatherParser> currentWeatherList = new Gson().fromJson(jsonString, currentWeatherArrayType);

            if (currentWeatherList == null) {
                throw new RequestsExceededException();
            }
            currentWeatherJsonObj = currentWeatherList.get(0);


        } catch (IOException e) {
            exception = e;
            e.printStackTrace();
        } catch (RequestsExceededException e) {
            exception = e;
            return null;
        } catch (JsonSyntaxException e) {
            exception = e;
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (exception != null) {
            if (exception instanceof RequestsExceededException) {
                ((RequestsExceededException) exception).printToScreen(modifyUI);
            }
        } else {
            if (currentWeatherJsonObj != null) {
                modifyUI.refreshCurrentWeatherDisplay(currentWeatherJsonObj);
            }
        }
    }
}
