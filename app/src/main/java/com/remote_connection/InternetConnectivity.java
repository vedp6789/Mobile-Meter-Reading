package com.remote_connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by comp on 24/09/2014.
 */
public class InternetConnectivity  {


    public Boolean checkInternetConnection(Context context){
        //boolean isConnected=false;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


       // boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;


        return activeNetwork != null;
    }
}
