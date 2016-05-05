package com.example.denijel.smartcargroup7;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
<<<<<<< HEAD
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
=======
import android.view.SurfaceHolder;
>>>>>>> 3d6983fa832f3854482e1ad7cb17fb29d9594b96
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.UUID;

/**
 * Created by denijel on 4/13/16.
 */
public class SendInput extends Activity{

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;
    OutputStream os;
    InputStream in;
    float angle, x, y;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private int BTSTATE = 0, SensorStatus=0;
    int MouseReq=0, read_values=0;
    private static final UUID MY_UUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static boolean active = false;
    private static final String TAG = "MainActivity";
    private MjpegView mv;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        active = true;
        //Try number 3

        new CommTerm().execute();

        final Button left1 = (Button) findViewById(R.id.leftBlinker);
        left1.bringToFront();

        String URL = "http://129.232.12.12:8080/?action=stream";
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        System.out.println("Ssh done");
        mv = new MjpegView(this);

        //setContentView(mv);
        LinearLayout camera = (LinearLayout) findViewById(R.id.middleSurface);
        camera.addView(mv);
        System.out.println("Added camera");
        System.out.println("Camera started");
        new DoRead().execute(URL);

        final String address = getIntent().getStringExtra("address").trim();
        btDevice=btAdapter.getRemoteDevice(address);
        BluetoothConnect.start();

        Button connect = (Button)findViewById(R.id.leftBlinker);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickList(v);
            }
        });

        final CustomSurfaceView csv = (CustomSurfaceView)findViewById(R.id.cSurfaceView);
        /*csv.setZOrderOnTop(true);
        SurfaceHolder csvHolder = csv.getHolder();
        csvHolder.setFormat(PixelFormat.TRANSPARENT);
        */
        //final CustomSurfaceView csv = new CustomSurfaceView(this);



<<<<<<< HEAD
=======

        //final CustomSurfaceView csv = new CustomSurfaceView(this);
        final CustomSurfaceView csv = (CustomSurfaceView)findViewById(R.id.cSurfaceView);
        csv.setZOrderOnTop(true);
        SurfaceHolder csvHolder = csv.getHolder();
        csvHolder.setFormat(PixelFormat.TRANSLUCENT);
>>>>>>> 3d6983fa832f3854482e1ad7cb17fb29d9594b96
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while(!csv.thread.isCancelled()){
                    if (angle > 1){
                        String msg = "a";
                        try{
                            os.write(msg.getBytes());
                        }catch(Exception es){}
                    }
                    else if (angle < 1){
                        String msg = "i";
                        try{
                            os.write(msg.getBytes());

                        }catch(Exception es){}
                    }
                    System.out.println(angle);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();

    }

    public void onClickList(View target){
        Intent listing = new Intent(this, com.example.denijel.smartcargroup7.BluetoothScan.class);
        startActivity(listing);


    }
    public void restartBtn(View target){
        System.out.println("Hello");
    }

    public float callMeAngle(float value){
        angle = value;
        return angle;

    }
    public float callMeX(float value){
        x = value;
        return x;

    }
    public float callMeY(float value){
        y = value;
        return y;

    }


    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if (res.getStatusLine().getStatusCode() == 401) {
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }

            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(true);
        }
    }


    /*public class MyTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            while(!csv.thread.isCancelled()){
                System.out.println(csv.angle);
            }
            return null;
        }
    }*/


    Thread BluetoothConnect=new Thread(){
        public void run()
        {
            //Looper.prepare();
            //Toast.makeText(getApplicationContext(), "Scanning and connecting to pc", Toast.LENGTH_LONG).show();
            int i=0,flag=0;
            while(i<1000)
            {
                try{
                    btSocket=btDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    btSocket.connect();
                    os = btSocket.getOutputStream();
                    in=btSocket.getInputStream();
                    /*if(mAcc!=null)
                        SensorStatus=SensorStatus|1;*/
                    flag=1;
                    BTSTATE=1;
                    //os.write(SensorStatus);
                    MouseReq=in.read();
                    break;
                }
                catch(IOException e){}
                i++;
            }
            if(flag==0)
            {
                //Toast.makeText(getApplicationContext(),"Unable to connect", Toast.LENGTH_SHORT).show();
                finish();
            }
            while(read_values!=255)
            {
                try{
                    read_values=in.read();
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
            }
            BTSTATE=0;
            try{os.write(255);
                os.close();
                btSocket.close();
            }
            catch(IOException e){}
            finish();

        }

    };





}