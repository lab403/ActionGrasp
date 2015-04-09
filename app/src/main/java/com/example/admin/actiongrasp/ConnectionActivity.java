package com.example.admin.actiongrasp;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.actiongrasp.service.mMessageListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ConnectionActivity extends ActionBarActivity {

    private Button butAut;
    private Button butManual;

    private Button butLink;
    private EditText serverIP;
    private static String SERVER_IP;
    private static int SERVER_PORT;
    private static DataOutputStream dout = null;


    // thread1 - UI 主畫面
    private Handler mUIHandler=new Handler();

    // thread2 - 廣播本機IP
    private Handler mThreadHandler;
    private HandlerThread mThread;

    // thread3 - 接收serverIP
    private Handler mThreadReciverHandler;
    private HandlerThread mThreadReciver;

    private boolean tThreadStop=false;
    // MRCode常數區段
    public final String MRCODE_TRUN_OFF = "MRCode_CC_02";
    public final String MRCODE_RESET = "MRCode_CC_01";
    public final String MRCODE_SLEEP = "MRCode_CC_00";
    public final String MRCODE_CONNECT = "TESTTEST123123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

         serverIP = (EditText) findViewById(R.id.editTextIP);
        butAut=(Button)findViewById(R.id.buttonAut);
        butAut.setOnClickListener(Aut);
        butManual=(Button)findViewById(R.id.buttonMan);
        butManual.setOnClickListener(Manual);
        //-----------------------------------------------------------------------//
        //                                                                       //
        //after anything, we need change this to always open                     //
        startService(new Intent(this,mMessageListener.class));                   //
        //                                                                       //
        //-----------------------------------------------------------------------//
    }

   private Button.OnClickListener Aut =new Button.OnClickListener(){

       @Override
       public void onClick(View v) {
           mAutoConnection();
          // Connect_Act(AutomaticActivity.class);
       }
   };
   private Button.OnClickListener Manual =new Button.OnClickListener(){

       @Override
       public void onClick(View v) {
           mCheckIP();
          // Connect_Act(InputActivity.class);
       }
   };
//   private void Connect_Act(Class target)
//   {
//       Intent intent=new Intent();
//       // Bundle bundle=new Bundle();
//       intent.setClass(ConnectionActivity.this,target);
//       startActivityForResult(intent,0);
//   }

    private void mCheckIP(){
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
        if(serverIP!=null)      SERVER_IP= serverIP.getText().toString();
        SERVER_PORT = 3579;

        // 如thread存在則移除它
        if(mThread!=null) {
            if (!mThread.isInterrupted()) {
                try {
                    mThread.interrupt();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }


        // 建立與SERVER連線
        mThread=new HandlerThread("writer");
        mThread.start();
        mThreadHandler=new Handler(mThread.getLooper());
        mThreadHandler.post(tSocketClient);
    }


    // 方法:自動搜尋
    private void mAutoConnection(){
        // 如thread存在則移除它
        if(mThread!=null) {
            if (!mThread.isInterrupted()) {
                try {
                    mThread.interrupt();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }

        if(mThreadHandler!=null){
            mThreadHandler.removeCallbacks(tSendBrocast);
        }

        // 宣告handler
        mThread = new HandlerThread("autoConnect");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(tSendBrocast);
    }

    // 執行緒:廣播本機IP
    private Runnable tSendBrocast=new Runnable() {
        @Override
        public void run() {
            String myIp;
            try {
                // 調用自訂之外部方法取得本地IP
                myIp = utils.getIPAddress(getApplication());
                byte[] msg = new String(myIp).getBytes();
                InetAddress addr = InetAddress.getByName("255.255.255.255");
                DatagramSocket client = new DatagramSocket();
                DatagramPacket sendPack =
                        new DatagramPacket(msg, msg.length, addr, 8899);
                client.send(sendPack);

                Toast.makeText(getBaseContext(), "自動搜尋", Toast.LENGTH_SHORT).show();

                // 如thread存在則移除它
                if(mThreadReciver!=null) {
                    if (!mThreadReciver.isInterrupted()) {
                        try {
                            mThreadReciver.interrupt();
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "tSendBrocast err="+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                // 啟動另一個thread接收SERVER IP
                mThreadReciver = new HandlerThread("receiver");
                mThreadReciver.start();
                mThreadReciverHandler = new Handler(mThreadReciver.getLooper());
                mThreadReciverHandler.post(tReciveFromPc);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };
    // 執行緒:把IP填入EDITTEXT並啟動連線
    private Runnable tAutoConnectServer=new Runnable() {
        @Override
        public void run() {
            // 把IP填入EDITTEXT
          serverIP.setText(SERVER_IP);

            Toast.makeText(getBaseContext(), "IP="+SERVER_IP, Toast.LENGTH_LONG).show();

            // 呼叫連線方法
            mConnectServer();
        }
    };
    // 執行緒:接收serverIP
    private Runnable tReciveFromPc=new Runnable() {
        @Override
        public void run() {
            String in;
            ServerSocket ss=null;
            Socket cs=null;
            DataInputStream din=null;
            try {
                //
                ss = new ServerSocket(3578);
                cs = ss.accept();
                din = new DataInputStream(cs.getInputStream());// 得到輸出串流
                in = din.readUTF();// 向伺服器發送訊息

                if (in.isEmpty()) {
                    Toast.makeText(getApplication(), "Empty", Toast.LENGTH_LONG).show();
                } else {
                    SERVER_IP = in;
                    Toast.makeText(getApplication(),"SERVER_IP="+ SERVER_IP, Toast.LENGTH_LONG).show();
                    // 把IP填入EDITTEXT並啟動連線
                    mUIHandler.post(tAutoConnectServer);
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            }finally {
                try {
                    if (cs!=null) cs.close();
                    if (ss!=null) ss.close();
                    if (din!=null) din.close();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    // 執行緒:作為客戶端連線
    private Runnable tSocketClient=new Runnable() {
        @Override
        public void run() {
            String in;
            Socket cs = null;
            DataInputStream din=null;
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

                din.close();
                dout.close();
                cs.close();
            } catch (Exception e) {
                Toast.makeText(getApplication(), "tSocketClient err="+e.toString(), Toast.LENGTH_LONG).show(); // 列印異常資訊
            } finally {// 用finally語句塊確保動作執行
                try {
                    if (dout != null) dout.close();// 關閉輸入串流
                    if (cs != null) cs.close();// 關閉Socket連接
                    if (din!=null) din.close();
                } catch (Exception e) {
                    Toast.makeText(getApplication(), e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }
    };

}
