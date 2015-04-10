package com.example.admin.actiongrasp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;


/**
 * Created by MurasakiYoru on 2015/4/10.
 */
public class WelcomeActivity extends Activity
{
    private ShimmerTextView wel_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomeactivity);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub_wel);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                wel_text=(ShimmerTextView)findViewById(R.id.wel_text);
                wel_text.setTextSize(34f);
                wel_text.setTextColor(Color.rgb(255,64,64));
                Shimmer sh = new Shimmer();
                sh.start(wel_text);
            }
        });


        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(WelcomeActivity.this, ActionGraspActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                WelcomeActivity.this.finish();
            }
        }, 1500);

    }
}
