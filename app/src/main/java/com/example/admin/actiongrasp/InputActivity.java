package com.example.admin.actiongrasp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Admin on 2015/3/25.
 */
public class InputActivity extends ActionBarActivity {

    private EditText editIP;
    private Button butLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        editIP=(EditText)findViewById(R.id.editIP);
        butLink=(Button)findViewById(R.id.buttonLink);
        butLink.setOnClickListener(Link);

    }

    private Button.OnClickListener Link=new Button.OnClickListener(){

        @Override
        public void onClick(View v) {


        }


    };


}
