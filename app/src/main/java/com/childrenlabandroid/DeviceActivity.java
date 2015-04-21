package com.childrenlabandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class DeviceActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView bluetoothStatusText, bluetoothSwitchText, searchDeviceButton;
    private BluetoothAdapter mBluetoothAdapter;
    private int REUQEST_ENABLE = 1, REUQEST_CONNECT = 2;
    private LinearLayout deviceList;
    private Set<BluetoothDevice> bluetoothDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        bluetoothDevices = new HashSet<>();
        bluetoothStatusText = (TextView) findViewById(R.id.bluetoothStatusText);
        bluetoothSwitchText = (TextView) findViewById(R.id.bluetoothSwitchText);
        searchDeviceButton = (TextView) findViewById(R.id.searchDeviceButton);
        deviceList = (LinearLayout) findViewById(R.id.deviceList);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        updateStatus();
        searchDeviceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.searchDeviceButton:
                Log.d("click on device search", "yes");
                if(!updateStatus()){
                    switchBluetoothOn();
                }else{
                    searchDevice();
                }
                break;
            case R.id.pairButton:
                Log.d("pairing device", "yes");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Log.d("requestCode", String.valueOf(requestCode));
        Log.d("resultCode", String.valueOf(resultCode));
        Log.d("result_ok", String.valueOf(RESULT_OK));

        if (requestCode == REUQEST_ENABLE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                bluetoothSwitchText.setText("ON");
                searchDevice();
            }
        }
    }

    public boolean updateStatus(){
        if(!mBluetoothAdapter.isEnabled()){
            bluetoothSwitchText.setText("OFF");
            return false;
        }else{
            bluetoothSwitchText.setText("ON");
            return true;
        }
    }

    public void switchBluetoothOn(){
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REUQEST_ENABLE);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("Received", intent.getAction());
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                addDeviceToList(device, false);
            }
        }
    };

    public void addDeviceToList(final BluetoothDevice device, boolean paired){
        Log.d("Bluetooth", device.getName() + "\n" + device.getAddress());

        boolean alreadyInList = false;
        for(BluetoothDevice d : bluetoothDevices){
            if(d.getAddress().equals(device.getAddress())){
                alreadyInList = true;
                break;
            }
        }
        if(!alreadyInList){
            bluetoothDevices.add(device);
            View view = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.each_device, null);
            TextView deviceName = (TextView) view.findViewById(R.id.deviceName);
            TextView deviceAddress = (TextView) view.findViewById(R.id.deviceAddress);
            TextView pairButton = (TextView) view.findViewById(R.id.pairButton);

            deviceName.setText(device.getName());
            deviceAddress.setText(device.getAddress());

            if(paired){
                pairButton.setText("Paired");
            }

            pairButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView view = (TextView) v;
                    Log.d("Click on button", view.getText().toString());
                    try {
                        if(view.getText().toString().equals("PAIR")){
                            Method method = device.getClass().getMethod("createBond", (Class[]) null);
                            method.invoke(device, (Object[]) null);
                        }else{
                            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                            method.invoke(device, (Object[]) null);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            deviceList.addView(view);
        }

    }

    public void searchDevice(){
        deviceList.removeAllViews();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                addDeviceToList(device, true);
            }
        }

            bluetoothStatusText.setText("Searching Device...");
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device, menu);
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
