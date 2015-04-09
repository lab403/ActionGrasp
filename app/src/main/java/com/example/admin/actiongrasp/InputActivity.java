package com.example.admin.actiongrasp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Admin on 2015/3/25.
 */
public class InputActivity extends ActionBarActivity {

    private Button butLink;
    private EditText serverIP;
    private static String SERVER_IP;
    private static int SERVER_PORT;
    private static DataOutputStream dout = null;



    // thread2 - 廣播本機IP
    private Handler mThreadHandler;
    private HandlerThread mThread;



    // MRCode常數區段
    public final String MRCODE_TRUN_OFF = "MRCode_CC_02";
    public final String MRCODE_RESET = "MRCode_CC_01";
    public final String MRCODE_SLEEP = "MRCode_CC_00";
    public final String MRCODE_CONNECT = "TESTTEST123123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        butLink=(Button)findViewById(R.id.butLink);
        butLink.setOnClickListener(mCheckIP);

        serverIP=(EditText)findViewById(R.id.editTextIP);
}

    // 方法:判斷ip
    private Button.OnClickListener mCheckIP =new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            int Error = 0;
            String IP4[] = serverIP.getText().toString().split("\\.");
            if (serverIP.getText().toString().isEmpty()) {
                //請輸入ip
                Error = 1;
            } else if (IP4.length != 4) {
                //錯誤的ip格式
                Error = 2;
            /*
                暫時去除檢查PORT功能,因為目前port都固定
             */
                //}else if(Integer.parseInt(serverPORT.getText().toString()) <= 0 ||
                //        Integer.parseInt(serverPORT.getText().toString()) > 65535){
                //    //錯誤的port
                //    Error = 3;
            } else
                for (String IP : IP4)
                    if (Integer.parseInt(IP) < 0 || Integer.parseInt(IP) > 255)
                        Error = 2;
            String MES = null;
            switch (Error) {
                case 0:
                    mConnectServer();
                    MES="連線中...";
                    break;
                case 1:
                    MES = "請輸入IP";
                    break;
                case 2:
                    MES = "不正確的IP格式";
                    break;
                case 3:
                    MES = "port錯誤";
                    break;
            }
            Toast.makeText(getApplication(), MES, Toast.LENGTH_LONG).show(); // 列印異常資訊
        }
        // 方法:建立連線
        private void mConnectServer(){
            SERVER_IP= serverIP.getText().toString();
            SERVER_PORT = 3579;

            // 如thread存在則移除它
            if(mThread!=null) {
                if (!mThread.isInterrupted()) {
                    try {
                        mThread.interrupt();
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "line:108=".toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            // 建立與SERVER連線
            mThread=new HandlerThread("writer");
            mThread.start();
            mThreadHandler=new Handler(mThread.getLooper());
            mThreadHandler.post(tSocketClient);
        }

        // 執行緒:作為客戶端連線
        private Runnable tSocketClient=new Runnable() {
            @Override
            public void run() {
                String in;
                Socket cs = null;
                DataInputStream din;
                try {
                    cs = new Socket(SERVER_IP, SERVER_PORT);// 連接伺服器(IP依您電腦位址來修改)
                    dout = new DataOutputStream(cs.getOutputStream());// 得到輸出串流
                    dout.writeUTF(MRCODE_CONNECT);// 向伺服器發送訊息

                    din = new DataInputStream(cs.getInputStream());// 得到輸出串流
                    in =din.readUTF();// 向伺服器發送訊息

                    if(in.equalsIgnoreCase("Connected")) {
                        Toast.makeText(getApplication(),"Connected",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplication(),in,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show(); // 列印異常資訊
                } finally {// 用finally語句塊確保動作執行
                    try {
                        if (dout != null) dout.close();// 關閉輸入串流
                        if (cs != null) cs.close();// 關閉Socket連接
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    };
}
