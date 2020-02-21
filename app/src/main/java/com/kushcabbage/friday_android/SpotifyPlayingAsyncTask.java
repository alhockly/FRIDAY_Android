package com.kushcabbage.friday_android;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SpotifyPlayingAsyncTask extends AsyncTask<Void,String,Void> {

    IModifyUI modifyUI;
    String url;

    String BASE_URL="https://api.spotify.com";
    String NOWPLAYING_ENDPOINT = "/v1/me/player/currently-playing";



    public SpotifyPlayingAsyncTask(IModifyUI modUI, String authKey){
        modifyUI = modUI;

    }



    @Override
    protected Void doInBackground(Void... voids) {

        url = "";


        try {
            Response response = OkHttpCall();
            String jsonString = response.body().string();

            //weatherForcastJsonObj = new Gson().fromJson(jsonString, GsonWeatherForecastParser.class);


        } catch (IOException e) {
            e.printStackTrace();
        }








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
