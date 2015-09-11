package com.remote_connection;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by vvvv on 23-08-2014.
 */
public class ConnectToAuthenticateAcc extends AsyncTask<String,String,Boolean> {

BufferedReader reader=null;
    @Override
    protected Boolean doInBackground(String... strings) {

        Boolean b1=false;
        String ret=null;
        int acc_no=Integer.parseInt(strings[0]);
        String address="http://192.168.0.103:8080/WSDemo1/resttest/invoke/authenticate1/"+acc_no;

        try {
            HttpClient httpClient=new DefaultHttpClient();
            HttpGet httpGet=new HttpGet();
            URI uri=new URI(address);
            httpGet.setURI(uri);

            HttpResponse response=httpClient.execute(httpGet);
            reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line=reader.readLine();
            ret=line;
            Log.d("Check888888",line);

            //ResponseHandler<String> responseHandler= new BasicResponseHandler();
            //ret=httpClient.execute(httpGet,responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(ret=="true"){
            Log.d("jgjgkjgjgjkgjgkjfjhhf",ret);
            b1=true;
        }
  return b1;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Toast.makeText(this,"Connecting...",Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    public void onPostExecute(Boolean aBoolean) {
        if(aBoolean){

        }

    }

}
