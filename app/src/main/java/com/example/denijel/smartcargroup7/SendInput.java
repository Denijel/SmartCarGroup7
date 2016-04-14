package com.example.denijel.smartcargroup7;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by denijel on 4/13/16.
 */
public class SendInput extends Activity {

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;
    OutputStream os;
    InputStream in;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private int BTSTATE = 0, SensorStatus=0;
    int MouseReq=0, read_values=0;
    private static final UUID MY_UUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");




    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Try number 3

        final String address = getIntent().getStringExtra("address").trim();
        btDevice=btAdapter.getRemoteDevice(address);
        BluetoothConnect.start();



        Button right = (Button)findViewById(R.id.rightBlinker);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "d";
                System.out.println("BLABLAABLABLA");
                try {
                    os.write(msg.getBytes());
                    System.out.println("HAHAHHAHHHAHA");
                } catch (Exception es) {
                }
            }
        });

        final CustomSurfaceView csv = new CustomSurfaceView(this);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                while(!csv.thread.isCancelled()){
                    System.out.println(csv.angle);
                }
                return null;

            }
        };

        task.execute();

    }


    Thread BluetoothConnect=new Thread(){
        public void run()
        {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "Scanning and connecting to pc", Toast.LENGTH_LONG).show();
            int i=0,flag=0;
            while(i<1000)
            {
                try{
                    btSocket=btDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    btSocket.connect();
                    os = btSocket.getOutputStream();
                    in=btSocket.getInputStream();
                    if(mAcc!=null)
                        SensorStatus=SensorStatus|1;
                    flag=1;
                    BTSTATE=1;
                    os.write(SensorStatus);
                    MouseReq=in.read();
                    break;
                }
                catch(IOException e){}
                i++;
            }
            if(flag==0)
            {
                Toast.makeText(getApplicationContext(),"Unable to connect", Toast.LENGTH_SHORT).show();
                finish();
            }
            while(read_values!=255)
            {
                try{
                    read_values=in.read();
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
