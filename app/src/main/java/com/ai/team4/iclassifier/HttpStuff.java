package com.ai.team4.iclassifier;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpStuff extends AsyncTask<String, Integer, String> {
    final String baseURL ="https://gateway.watsonplatform.net/natural-language-classifier/api/v1/classifiers/";

    //add data specific to your instance here
    private final String classifierID = "e997b3x362-nlc-6";
    private final String username="4bde92b0-827c-4b20-8824-56e096640a8e";
    private final String password = "2howvcrl0JxP";

    MainActivity mainActivity = null;

    public HttpStuff(MainActivity act)
    {
        mainActivity = act;
    }
    @Override
    protected String doInBackground(String... question) {
        String retVal = "none";
        //build a request to talk to Watson
        OkHttpClient client = new OkHttpClient();
        String creds = Credentials.basic(username, password);
        Request request = new Request.Builder()
                .url(baseURL + classifierID + "/classify?text=" + question[0])
                .header("Authorization",creds)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            retVal = response.body().string();
        }
        catch (IOException ex)
        {
            retVal="Sorry :"+ ex.getMessage();
        }
        return retVal;
    }


    protected void onProgressUpdate(Integer... progress) {
    //TODO
    }

    @Override
    protected void onPostExecute(String retVal) {
        mainActivity.updateResult(retVal);
    }

    protected void onPreExecute()
    {
        mainActivity.updateRequest("Asking Watson to classify...");
    }
}

