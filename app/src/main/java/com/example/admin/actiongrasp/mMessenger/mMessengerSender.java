package com.example.admin.actiongrasp.mMessenger;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by MurasakiYoru on 2015/4/14.
 */
public class mMessengerSender
{
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";

    public mMessengerSender(Context c)
    {
        this.context=c ;
        setGoogleApiClient();
    }
    private void setGoogleApiClient()
    {
        if(mGoogleApiClient==null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                    {
                        @Override
                        public void onConnected(Bundle connectionHint)
                        {
                            Log.d("actiongrasp", "onConnected: " + connectionHint);
                            // Now you can use the Data Layer API
                        }

                        @Override
                        public void onConnectionSuspended(int cause)
                        {
                            Log.d("actiongrasp", "onConnectionSuspended: " + cause);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
                    {
                        @Override
                        public void onConnectionFailed(ConnectionResult result)
                        {
                            Log.d("actiongrasp", "onConnectionFailed: " + result);
                        }
                    })
                            // Request access only to the Wearable API
                    .addApi(Wearable.API)
                    .build();
        }
        //connect
        if(!mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.connect();
            Log.v("actiongrasp", "Connecting to GoogleApiClient");
        }
    }
    public void sendMessage()
    {
        if(mGoogleApiClient.isConnected())
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    for(Node node : nodes.getNodes())
                    {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),START_ACTIVITY_PATH, new byte[0]).await();
                        if(!result.getStatus().isSuccess())
                        {
                            Log.e("test", "send error");
                        }
                        else
                        {
                            Log.i("test", "send success!! sent to: " + node.getDisplayName());
                        }
                    }
                }
            }).start();

        }
        else
        {
            Log.e("test", "not connected");
        }
    }

//    @Override
//    public void onDestroy()
//    {
//        Log.v("actiongrasp", "Destroyed");
//        if(null != mGoogleApiClient)
//        {
//            if(mGoogleApiClient.isConnected())
//            {
//                mGoogleApiClient.disconnect();
//                Log.v("actiongrasp", "GoogleApiClient disconnected");
//            }
//        }
//        super.onDestroy();
//    }
}
