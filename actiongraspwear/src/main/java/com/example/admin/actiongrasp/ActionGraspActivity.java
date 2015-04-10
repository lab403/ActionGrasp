package com.example.admin.actiongrasp;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;



public class ActionGraspActivity extends Activity{

    private TextView mTextView;
    private Button btn;

    private GoogleApiClient mGoogleApiClient;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_grasp);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                mTextView = (TextView) stub.findViewById(R.id.text);
                btn = (Button)stub.findViewById(R.id.btn);
                btn.setOnClickListener(btn_action);
            }
        });

        setgoogleapi();
    }



    private void setgoogleapi()
    {
        if(mGoogleApiClient==null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                    {
                        @Override
                        public void onConnected(Bundle connectionHint)
                        {
                            Log.d("actiongrasp", "onConnected: " + connectionHint);
                            mTextView.setText("onConnected: " + connectionHint);
                            // Now you can use the Data Layer API
                        }

                        @Override
                        public void onConnectionSuspended(int cause)
                        {
                            Log.d("actiongrasp", "onConnectionSuspended: " + cause);
                            mTextView.setText("onConnectionSuspended: " + cause);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
                    {
                        @Override
                        public void onConnectionFailed(ConnectionResult result)
                        {
                            Log.d("actiongrasp", "onConnectionFailed: " + result);
                            mTextView.setText("onConnectionFailed: " + result);
                        }
                    })
                            // Request access only to the Wearable API
                    .addApi(Wearable.API)
                    .build();
        }

        if(!mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.connect();
            Log.v("actiongrasp", "Connecting to GoogleApiClient");
        }
    }


    private View.OnClickListener btn_action = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            sendStartActivityMessage();
        }
    };

    private void sendStartActivityMessage()
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
                            Log.e("test", "error");
                        }
                        else
                        {
                            Log.i("test", "success!! sent to: " + node.getDisplayName());
                        }
                    }
                    /*
                    NodeApi.GetLocalNodeResult nodes = Wearable.NodeApi.getLocalNode(mGoogleApiClient).await();
                    Node node = nodes.getNode();

                    Log.v("actiongrasp", "Activity Node is : "+node.getId()+ " - " + node.getDisplayName());

                    MessageApi.SendMessageResult result =
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), START_ACTIVITY_PATH, new byte[0]).await();

                    if (result.getStatus().isSuccess())
                    {
                        Log.v("actiongrasp", "Activity Message: {" + START_ACTIVITY_PATH + "} sent to: " + node.getDisplayName());
                    }
                    else
                    {
                        // Log an error
                        Log.v("actiongrasp", "ERROR: failed to send Activity Message");
                    }
                    */

                }
            }).start();

        }
        else
        {
            Log.e("test", "not connected");
        }
    }

    private void app_finish()
    {
        this.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        app_finish();
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        Log.v("actiongrasp", "Destroyed");
        if(null != mGoogleApiClient)
        {
            if(mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
                Log.v("actiongrasp", "GoogleApiClient disconnected");
            }
        }
        super.onDestroy();
    }

}
