package com.example.dekkotask;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Loadmore {

    public static void load(String photoName) {
        MainActivity.mLoading = true;

        //Api call
        DownloadTask task = new DownloadTask();
        task.execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&\n" +
                " api_key=178069b03af62f5735258c0a10a14d6e&format=json&nojsoncallback=1&text="+photoName);
    }

    public static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection connection;

            try {

                url = new URL(urls[0]);
                connection = (HttpsURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {

                    result += (char) data;
                    data = reader.read();

                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObject = new JSONObject(s);

                String photosInfo = jsonObject.getString("photos");

                JSONObject jsonTotalPhotos = new JSONObject(photosInfo);
                int mtotalPhotos = jsonTotalPhotos.getInt("total");
                String photoUrlInfo = jsonTotalPhotos.getString("photo");

                if(jsonTotalPhotos.getInt("total") == mtotalPhotos){
                    MainActivity.hasMore = false;
                }

                JSONArray jsonArray = new JSONArray(photoUrlInfo);
                ArrayList<String> resultPhotosUrl = new ArrayList<String>();

                for(int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String eachUrl = "https://farm"+jsonPart.getString("farm")+".static.flickr.com/"
                            +jsonPart.getString("server")+"/"+jsonPart.getString("id")+"_"
                            +jsonPart.getString("secret")+".jpg";

                    resultPhotosUrl.add(eachUrl);
                }

                MainActivity.data.addAll(resultPhotosUrl);
                MainActivity.adapter.notifyDataSetChanged();
                MainActivity.mLoading = false;


            } catch (Exception e) {
                e.printStackTrace();
//                Log.i("failed","Try Later");
            }
        }
    }

}
