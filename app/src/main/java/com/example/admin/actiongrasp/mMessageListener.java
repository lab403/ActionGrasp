package com.example.admin.actiongrasp;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by MurasakiYoru on 2015/4/8.
 */
public class mMessageListener extends WearableListenerService
{

    public static final String START_ACTIVITY_PATH = "/start/MainActivity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();
        if(START_ACTIVITY_PATH.equals(path))
        {
            Intent intent = new Intent(this,InitialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            return;
        }

    }
}
