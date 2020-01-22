package com.example.friday_android;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SpotifyAuthAsyncTask extends AsyncTask<Void,String,Void> {


    IKeyPass iKeyPass;
    String url;

    String BASE_URL="https://api.spotify.com";


    String AUTH_ENDPOINT = "/authorize";

    String clientID = "2c0d0c49b20c4a2cbe346f42bb6dab74";

    String scope = "user-read-currently-playing";



    public SpotifyAuthAsyncTask(IKeyPass ikeypass){
        iKeyPass = ikeypass;
    }



    @Override
    protected Void doInBackground(Void... voids) {

        url = BASE_URL + AUTH_ENDPOINT +"?client_id=" + clientID + "&scope=" + scope + "&response_type=code" + "&redirect_uri=http://localhost/";


        try {
            Response response = OkHttpCall();
            String jsonString = response.body().string();

            //weatherForcastJsonObj = new Gson().fromJson(jsonString, GsonWeatherForecastParser.class);

            iKeyPass.SetKey(Util.SPOTIFY_AUTHKEY_NAME,"key");
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