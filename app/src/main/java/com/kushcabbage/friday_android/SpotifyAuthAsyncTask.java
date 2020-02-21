package com.kushcabbage.friday_android;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SpotifyAuthAsyncTask extends AsyncTask<Void,String,Void> {

    String url;

    String BASE_URL="https://accounts.spotify.com/";

    String AUTH_ENDPOINT = "authorize";

    String clientID = "2c0d0c49b20c4a2cbe346f42bb6dab74";
    String clientSecret = "811e8611fafc4683b415caae2814d98b";
    String scope = "user-read-currently-playing";


    @Override
    protected Void doInBackground(Void... voids) {

        url = BASE_URL + AUTH_ENDPOINT +"?client_id=" + clientID + "&scope=" + scope + "&response_type=code" + "&redirect_uri=http://localhost/";


        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();


            Util.apiKeyMap.put(Util.SPOTIFY_AUTHKEY_NAME,"key");

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
