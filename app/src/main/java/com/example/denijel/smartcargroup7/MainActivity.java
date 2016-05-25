package com.example.denijel.smartcargroup7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Denijel and Olle.
 */

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    GestureDetector detector;
    String lastClass = "";
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
    static boolean active = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("started");
        new CommTerm().execute(); // executes commterm which is a sshcommands to the raspberry pi which starts the camera
        detector = new GestureDetector(this, this);

        setContentView(R.layout.splash);

        final ImageView iv = (ImageView) findViewById(R.id.logo);
        final TextView tv = (TextView) findViewById(R.id.wlcm);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fall);
        final Animation aan = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.VISIBLE);
                tv.startAnimation(aan);
                aan.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
            // Runs the animation
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tv.setVisibility(View.VISIBLE);
                        iv.startAnimation(an2);
                        tv.startAnimation(an2);
                        finish();
                        Intent i = new Intent(MainActivity.this, Start.class);
                        startActivity(i);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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

        if (id == R.id.action_settings) {
            return true;
        }
        //Checks if id is correct.

        return super.onOptionsItemSelected(item);
    }

    public void onClickList(View target){
        Intent listing = new Intent(this, com.example.denijel.smartcargroup7.BluetoothScan.class);
        startActivity(listing);
    }
    public void RestartCam(View target){
    }

    public void onResume(){
        super.onResume();

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
                //Function for initilazing bluetooth to PC.
            if(flag==0)
            {
                Toast.makeText(getApplicationContext(),"Unable to connect", Toast.LENGTH_SHORT).show();
                finish();
                //If a connection was not established (if flag still has the value 0)
                //the message Unable to connect will appear and the connection failed.
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
                //If BTSTATE=0 (No bluetooth is connected) it disconnects from the socket.

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
