package com.example.admin.actiongrasp.mMessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.admin.actiongrasp.ViewPager.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by MurasakiYoru on 2015/4/8.
 */
public class mMessengeListener extends WearableListenerService
{

    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    private GoogleApiClient mGoogleApiClient;

    private final String TAG="ActionGrasp";

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.wtf(TAG, "service start");

        if(mGoogleApiClient==null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                    {
                        @Override
                        public void onConnected(Bundle connectionHint)
                        {
                            Log.wtf(TAG, "onConnected: " + connectionHint);
                            // Now you can use the Data Layer API
                        }

                        @Override
                        public void onConnectionSuspended(int cause)
                        {
                            Log.wtf(TAG, "onConnectionSuspended: " + cause);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
                    {
                        @Override
                        public void onConnectionFailed(ConnectionResult result)
                        {
                            Log.wtf(TAG, "onConnectionFailed: " + result);
                        }
                    })
                            // Request access only to the Wearable API
                    .addApi(Wearable.API)
                    .build();
        }

        if(!mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.connect();
            Log.wtf(TAG, "Connecting to GoogleApiClient");
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();
        Log.wtf(TAG,"receive " +path);
        if(START_ACTIVITY_PATH.equals(path))
        {
            Log.wtf(TAG,"function start");

            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            return;
        }
    }

    @Override
    public void onDestroy()
    {

        Log.wtf(TAG, "Destroyed");
        if(null != mGoogleApiClient)
        {
            if(mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
                Log.wtf(TAG, "GoogleApiClient disconnected");
            }
        }
        super.onDestroy();
    }
}
