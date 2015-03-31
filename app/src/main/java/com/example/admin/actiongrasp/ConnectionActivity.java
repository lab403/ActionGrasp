package com.example.admin.actiongrasp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class ConnectionActivity extends ActionBarActivity {

    private Button butAut;
    private Button butManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        butAut=(Button)findViewById(R.id.buttonAut);
        butAut.setOnClickListener(Aut);
        butManual=(Button)findViewById(R.id.buttonMan);
        butManual.setOnClickListener(Manual);
    }

   private Button.OnClickListener Aut =new Button.OnClickListener(){

       @Override
       public void onClick(View v) {


           Connect_Act(AutomaticActivity.class);
       }
   };
   private Button.OnClickListener Manual =new Button.OnClickListener(){

       @Override
       public void onClick(View v) {


           Connect_Act(InputActivity.class);
       }
   };
   private void Connect_Act(Class target)
   {
       Intent intent=new Intent();
       // Bundle bundle=new Bundle();
       intent.setClass(ConnectionActivity.this,target);
       startActivityForResult(intent,0);
   }

}
