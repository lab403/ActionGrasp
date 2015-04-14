package com.example.admin.actiongrasp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.actiongrasp.mMessenger.mMessengerSender;
import com.example.admin.actiongrasp.mMessenger.mMessengeListener;


public class ActionGraspActivity extends Activity{

    private TextView mTextView;
    private Button btn;
    private mMessengerSender mMSender;

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

        this.mMSender = new mMessengerSender(ActionGraspActivity.this);

        startService(new Intent(this,mMessengeListener.class));
    }

    private View.OnClickListener btn_action = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            mMSender.sendMessage();
        }
    };



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


}
