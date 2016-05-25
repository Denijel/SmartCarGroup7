package com.example.denijel.smartcargroup7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    GestureDetector detector;
    String lastClass = "";
//    BTList btList = new BTList();
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
    private static final int REQUEST_ENABLE_BT = 1;
    ArrayList<BluetoothDevice> pairedDevices;
    ListView listViewPairedDevices;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detector = new GestureDetector(this, this);

        final Button left = (Button) findViewById(R.id.leftBlinker);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickList(v);
            }
        });




        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickList(View target){
        Intent listing = new Intent(this, com.example.denijel.smartcargroup7.BluetoothScan.class);
        startActivity(listing);




    }

    public void onResume(){
        super.onResume();
        /*final String address = getIntent().getStringExtra("address").trim();
        btDevice=btAdapter.getRemoteDevice(address);*/
        //BluetoothConnect.start();




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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  /*depth*/1) {
            if(resultCode == RESULT_OK){
                lastClass = data.getStringExtra("the current class");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX()<e2.getX() && e2.getX()-e1.getX() > Math.abs(e2.getY()-e1.getY())){

            if(!this.getClass().equals(Start.class)) {
                Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("the current class", this.getClass().getSimpleName());
                setResult(RESULT_OK, intent);
                finish();
            }else {
                Toast.makeText(getApplicationContext(), "Can't back further", Toast.LENGTH_SHORT).show();
            }
        }
        if(e1.getX()>e2.getX() && e1.getX()-e2.getX() > Math.abs(e2.getY()-e1.getY())){
            Toast.makeText(getApplicationContext(),"Undo",Toast.LENGTH_SHORT).show();

            switch (lastClass){
                case "Start":
                    startActivityForResult(new Intent(this, Start.class), 1);
                    break;
                case "MainActivity":
                    startActivityForResult(new Intent(this, MainActivity.class), 1);
                    break;
                case "Options":
                    startActivityForResult(new Intent(this, Options.class), 1);
                    break;
//                case "Help":
//                  startActivityForResult(new Intent('Help.this, Help.class), 1);
//                  break;
                default:
                    break;
            }
        }
        if(e1.getY()>e2.getY() && e1.getY()-e2.getY() > Math.abs(e2.getX()-e1.getX())){
            //swipe up action possibility
        }
        if(e1.getY()<e2.getY() && e2.getY()-e1.getY() > Math.abs(e2.getX()-e1.getX())){
            //swipe down action possibility
        }

        return true;
    }

}
