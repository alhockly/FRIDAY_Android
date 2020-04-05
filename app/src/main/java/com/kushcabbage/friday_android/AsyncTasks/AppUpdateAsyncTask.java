package com.kushcabbage.friday_android.AsyncTasks;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.kushcabbage.friday_android.IUpdateApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppUpdateAsyncTask extends AsyncTask<String,Void,Void> {

        String saveDir ="/sdcard/";
        String saveFileName="update.apk";
        private Context context;
        String downloadedVersionName;

        IUpdateApp iUpdateApp;

        public AppUpdateAsyncTask(Context aContext, IUpdateApp aUpdateApp){
            context = aContext;
            iUpdateApp = aUpdateApp;
        }

        @Override
        protected Void doInBackground(String... strings) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                if(strings.length==0){
                    System.out.println("Download link not provided to AsyncTask");
                    throw new NullPointerException();
                }
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                File outputFile = new File(saveDir+saveFileName);
                if(outputFile.exists()){
                    outputFile.delete();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    output.write(data, 0, count);
                }

                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();


                final PackageManager pm = context.getPackageManager();
                String fullPath = saveDir+saveFileName;
                PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
                System.out.println("VersionCode : " + info.versionCode + ", VersionName : " + info.versionName);
                downloadedVersionName = info.versionName;

            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                return null;
            }



            return null;
        }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(downloadedVersionName!= null) {
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                String installedVersion = pInfo.versionName;

                if (Float.parseFloat(installedVersion) < Float.parseFloat(downloadedVersionName)) {
                    iUpdateApp.installApp(new File(saveDir + saveFileName));
                }


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}


