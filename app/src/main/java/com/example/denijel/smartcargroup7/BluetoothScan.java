package com.example.denijel.smartcargroup7;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by denijel on 3/22/16.
 */
public class BluetoothScan extends Activity implements GestureDetector.OnGestureListener {
    protected final int REQUEST_ENABLE_BT=1;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private ArrayAdapter<String> PairedDevices;
    BluetoothAdapter ba=BluetoothAdapter.getDefaultAdapter();
    ListView PairedDevicesFound;
    ListView ScannedDevicesFound;
    GestureDetector detector;
    String lastClass = "";
    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if(state==BluetoothAdapter.STATE_OFF)
                    ba.enable();
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    PairedDevices.add(device.getName() + "\n" + device.getAddress());
                    PairedDevices.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Button scanButton = (Button) findViewById(R.id.button_scan);
                scanButton.setText("Scan for more devices");
                if (PairedDevices.getCount() == 0) {
                    PairedDevices.add("No Devices Available");
                    PairedDevices.notifyDataSetChanged();
                }
                // Scans for more devices. If no devices are found the Activity title is changed to No Devices Available
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoothscan);
        IntentFilter BluetoothState = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, BluetoothState);
        Intent i = new Intent();
        setResult(100,i);
        PairedDevicesFound = (ListView)findViewById(R.id.paired_devices);
        PairedDevicesFound.setOnItemClickListener(mDeviceClickListener);
        PairedDevices=new ArrayAdapter<String>(BluetoothScan.this ,R.layout.forlist);
        PairedDevicesFound.setAdapter(PairedDevices);

        detector = new GestureDetector(this, this);

        Set<BluetoothDevice> pairedDevices = ba.getBondedDevices();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                PairedDevices.add(device.getName() + "\n" + device.getAddress());
                PairedDevices.notifyDataSetChanged();
            }
        }
        final Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PairedDevices.clear();
                if(ba.isDiscovering()==true)
                    ba.cancelDiscovery();
                Set<BluetoothDevice> pairedDevices = ba.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        PairedDevices.add(device.getName() + "\n" + device.getAddress());
                        PairedDevices.notifyDataSetChanged();
                    }
                }
                Button scanButton = (Button) findViewById(R.id.button_scan);
                scanButton.setText("Scanning......");
                // Turn on sub-title for new devices
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                }
                while(!ba.startDiscovery());
            }
        });

        // Binding resources Array to ListAdapter

    }
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            v.setBackgroundColor(Color.parseColor("#FFA800"));
            if(ba.isDiscovering()==true)
                ba.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            if(info.equalsIgnoreCase("No Devices Available")==false)
            {
                Intent intent = new Intent(getApplicationContext(), SendInput.class);
                String address = info.substring(info.length() - 17);
                intent.putExtra("address",address);
                startActivityForResult(intent,100);
            }
        }
    };
    // Initialize the button to perform device discovery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT)
        {
            if(resultCode==Activity.RESULT_CANCELED){
                if(!ba.isEnabled()){
                    finish();
                }
            }
        }
        if (requestCode ==  /*depth*/1) {
            if(resultCode == RESULT_OK){
                lastClass = data.getStringExtra("the current class");
            }
        }
        if(requestCode==100)
            finish();
        return;
    }
    @Override
    protected void onPause()
    {
        if(ba.isDiscovering()==true)
            ba.cancelDiscovery();
        (BluetoothScan.this).unregisterReceiver(mReceiver);
        super.onPause();
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
                case "Help":
                    startActivityForResult(new Intent(this, Help.class), 1);
                    break;
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
