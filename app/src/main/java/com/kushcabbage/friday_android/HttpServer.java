package com.kushcabbage.friday_android;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;

/*api definition
*
* api runs on port 8080
*
* e.g ipAdress:8080/api/
*
*  calls to manipulate the view call at /view/
*
*  calls to access data at /data/ using GET method to read and POST to request a write
*
* e.g
*   baseUrl/api/view/display/off
*   baseUrl/api/data/uptime
*   /api/data/refresh/currentTemp
*
*
* */

public class HttpServer extends NanoHTTPD {
    private String TAG = "HTTPSERVER";
    private IApiMVC.ViewOps viewInterface;
    private IApiMVC.DataControllerOps dataInterface;


    public HttpServer(IApiMVC.ViewOps aViewInterface, IApiMVC.DataControllerOps aDataTaskInterface) throws IOException {
        super(8080);
        viewInterface = aViewInterface;
        dataInterface = aDataTaskInterface;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String htmlOpen = "<html><body>\n";
        String htmlClose = "</body></html>\n";
        String htmlbody ="";
        String[] sessionUri = session.getUri().toLowerCase().split("/");
        if(!sessionUri[1].equals("api")){
            return newFixedLengthResponse( htmlOpen + "hello" + htmlClose);
        } else {
            if(sessionUri[2].equals("view")){
                return viewApiHandler(getSliceOfArray(sessionUri,3,sessionUri.length));
            }
            if(sessionUri[2].equals("data")){
                return dataApiHandler(getSliceOfArray(sessionUri,3,sessionUri.length));

            }
        }
        return newFixedLengthResponse("");
    }


    public Response viewApiHandler(@NotNull String[] aUri){
        if(aUri[0].equals("display")){
            if(aUri[1].equals("off")){
                viewInterface.displayOnOff(false);
            } else {
                viewInterface.displayOnOff(true);
            }
        }

        return newFixedLengthResponse("");
    }

    public Response dataApiHandler(String[] aUri){
        if(aUri[0].equals("refresh")){
            if(aUri[1].equals("currenttemp")){
                dataInterface.refreshCurrentTemp();
                return newFixedLengthResponse("Current temp refreshed");
            }

        }
        return newFixedLengthResponse("");
    }



    public static String[] getSliceOfArray(String[] arr, int start, int end) {

        // Get the slice of the Array
        String[] slice = new String[end - start];

        // Copy elements of arr to slice
        for (int i = 0; i < slice.length; i++) {
            slice[i] = arr[start + i];
        }

        // return the slice
        return slice;
    }
}
