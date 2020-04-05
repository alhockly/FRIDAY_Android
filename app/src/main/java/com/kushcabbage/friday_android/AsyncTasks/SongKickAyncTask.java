package com.kushcabbage.friday_android.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kushcabbage.friday_android.IModifyUI;
import com.kushcabbage.friday_android.exceptions.RequestsExceededException;
import com.kushcabbage.friday_android.gsonParsers.GsonSongKickParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SongKickAyncTask extends AsyncTask<Void,Void,Void> {

    String username = "alex-hockly";
    String apiKey = "vFJgueGeeIk9C2rV";

   String url =  "https://api.songkick.com/api/3.0/users/"+username+"/calendar.json?reason=tracked_artist&apikey="+apiKey;

    GsonSongKickParser js;

    IModifyUI modifyUI;

    Date currentDate;

    public SongKickAyncTask(IModifyUI modui){
        modifyUI = modui;
        currentDate = new Date();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();


            js = new Gson().fromJson(jsonString, GsonSongKickParser.class);

            for(GsonSongKickParser.CalenderEntry cal : js.getevents()){

                if (cal.getEvent().start.datetime != null) {
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

            for (Iterator<GsonSongKickParser.CalenderEntry> iterator = js.getevents().iterator(); iterator.hasNext(); ) {
                 GsonSongKickParser.CalenderEntry cal = iterator.next();
                if (cal.event.start.dateobj != null) {
                    if (cal.event.start.dateobj.before(currentDate)) {
                        iterator.remove();
                    }
                }
            }

            Log.d("Debug", jsonString);
            if (js.resultsPage == null) {
                throw new RequestsExceededException();
            }


        } catch (RequestsExceededException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            Log.d("Debug", "json parse failed");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Debug", "HTTP GET failed - probably offline");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if( js!=null){
            modifyUI.refreshSongKickDisplay(js);
        }
    }
}
