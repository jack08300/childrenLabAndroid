package com.childrenlabandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.*;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class DeviceActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView bluetoothStatusText, bluetoothSwitchText, searchDeviceButton;
    private BluetoothAdapter mBluetoothAdapter;
    private int REUQEST_ENABLE = 1, REUQEST_CONNECT = 2;
    private LinearLayout deviceList;
    private Set<BluetoothDevice> bluetoothDevices;

    public String address;
    private static final UUID uuid2 = UUID.fromString("91c10edc-8616-4cbf-bc79-0bf54ed2fa17");
    private static final ParcelUuid uuid = ParcelUuid.fromString("91c10edc-8616-4cbf-bc79-0bf54ed2fa17");

    BluetoothManager bluetoothManager;
    BluetoothAdapter adapter;

    BluetoothLeScanner scanner;
    ScanSettings settings;
    BluetoothGatt gatt;
    boolean startedServiceDiscovery = false;

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

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();
        scanner = adapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();


        if(mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Your phone doesn't support bluetooth connection.", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

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

    public void addDeviceToList(final ScanResult result, boolean paired){
        final BluetoothDevice device = result.getDevice();
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
            TextView deviceUuidList = (TextView) view.findViewById(R.id.deviceUuidList);
            List<ParcelUuid> uuidList = result.getScanRecord().getServiceUuids();
            String uuidString = "";
            for(int i=0;i<uuidList.size();i++){
                uuidString += "\n" + uuidList.get(i).toString();
            }
            final TextView pairButton = (TextView) view.findViewById(R.id.pairButton);

            deviceUuidList.setText(uuidString);
            deviceName.setText(device.getName());
            deviceAddress.setText(device.getAddress());

            if(paired){
                pairButton.setText("PAIR");
            }

            pairButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView view = (TextView) v;
                    Log.d("Click on button", view.getText().toString());
                    try {
                        if(view.getText().toString().equals("PAIR")){
                            connectToDevice(device);

                        }else{
                            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                            method.invoke(device, (Object[]) null);
                            pairButton.setText("Pair");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            deviceList.addView(view);
        }

    }

    public void connectToDevice(BluetoothDevice device) {
        Log.d("connecting", "bluetooth");
        if (gatt == null) {
            gatt = device.connectGatt(this, false, gattCallback);
        }else{
            gatt.discoverServices();
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("onConnectionStateChang ",  status + ", state: " + newState);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d("Bluetooth Status", "STATE_CONNECTED");
                    if (!startedServiceDiscovery) {
                        gatt.discoverServices();
                        startedServiceDiscovery = true;
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //callback.didDisconnectFromDevice(this, gatt.getDevice());
                    gatt = null;
                    searchDevice();
                    break;
                default:
                    Log.d("STATE_OTHER", String.valueOf(status));
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status){
            Log.d("characterstatic read", "readinginging");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status){
            Log.d("onDescriptorRead read", "readinginging");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattCharacteristic ch = gatt.getService(UUID.fromString("91c10edc-8616-4cbf-bc79-0bf54ed2fa17"))
                        .getCharacteristic(UUID.fromString("09a44002-cd70-4b7a-b46f-cc4cdbab1bb4"));

/*                if(!gatt.readCharacteristic(ch)){
                    Log.e("Read characteristic", "can't read 09a44002-cd70-4b7a-b46f-cc4cdbab1bb4");
                }*/
                gatt.setCharacteristicNotification(ch, true);
                //BluetoothGattCharacteristic bch = service.getCharacteristic((UUID.fromString("91c10edc-8616-4cbf-bc79-0bf54ed2fa17")));
                //BluetoothGattDescriptor descriptor = ch.getDescriptor(UUID.fromString("09a44002-cd70-4b7a-b46f-cc4cdbab1bb4"));
                //descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                //gatt.writeDescriptor(descriptor);

            } else {
                Log.d("onServicesDiscovered", "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            Log.d("onCharacteristicChanged", "data: " + data.toString());
        }
    };

    private List<ScanFilter> scanFilters() {

        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(uuid).build();
        List<ScanFilter> list = new ArrayList<>(1);
        list.add(filter);
        return list;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device != null) {
                addDeviceToList(result, true);
                scanner.stopScan(scanCallback);
            }
        }
    };

    public void searchDevice(){
        deviceList.removeAllViews();
        scanner.startScan(scanFilters(), settings, scanCallback);
        Log.d("blueTooth","Scanner started");

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
    }
}
