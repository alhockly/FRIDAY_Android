package com.example.friday_android;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SongKickAyncTask extends AsyncTask<Void,Void,Void> {

    String username = "alex-hockly";
    String apiKey = "vFJgueGeeIk9C2rV";

   String url =  "https://api.songkick.com/api/3.0/users/"+username+"/calendar.json?reason=tracked_artist&apikey="+apiKey;


    IModifyUI modifyUI;

    public SongKickAyncTask(IModifyUI modui){
        modifyUI = modui;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();


            GsonSongKickParser js = new Gson().fromJson(jsonString, GsonSongKickParser.class);

            for(GsonSongKickParser.CalenderEntry cal : js.resultsPage.results.calendarEntry){

                if (cal.event.start.datetime != null) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    try {
                        cal.event.start.dateobj = format.parse(cal.event.start.datetime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                cal.event.featuredArtists=" + ";
                int ii=0;
               for (GsonSongKickParser.Performance p: cal.event.performance){
                   if (cal.event.performance.size() == 1){cal.event.featuredArtists="";break;}  //bail if no features
                   if (ii==0){
                       ii++;
                       continue;
                   }

                   if (ii>1){
                       cal.event.featuredArtists+= ", "+p.displayName;      //dont show comma for first feature
                   } else{
                       cal.event.featuredArtists+= p.displayName;
                   }
                   if (ii>2){break;}        //max num of features
                   ii++;
               }

            }

            Log.d("Debug", jsonString);
            if (js.resultsPage == null) {
                throw new RequestsExceededException();
            }

            modifyUI.refreshSongKickDisplay(js);


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
}
