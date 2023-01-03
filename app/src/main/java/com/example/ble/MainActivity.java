package com.example.ble;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ble.ui.home.HomeFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {



    private static final int REQUEST_ENABLE_BT = 0;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    BluetoothGatt gatt;
    boolean mScanning;

    ArrayList<String> list = new ArrayList<String>();
    public static ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    ArrayAdapter<String> adapter;

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler = new Handler();

    private static final long SCAN_PERIOD = 10000;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public UUID convertFromInteger(int i){
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager.class);
        }
        bluetoothAdapter = bluetoothManager.getAdapter();



        if (bluetoothAdapter == null) {
            finish();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);
//        BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
//                list.add(bluetoothDevice);
//            }
//        };


        //while(list.size()==0){}
        //bluetoothAdapter.startLeScan(scanCallback);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(newState== STATE_CONNECTED)
                    gatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                byte[] value = new byte[1];
                int data = 0x30;
                value[0] = (byte) (21 & 0x31);
                String cist = "f17129f5-64b9-4f24-bcfd-c53a8bdd3893";

                int i=0;
                int j=0;

                List<BluetoothGattService> services = gatt.getServices();
                BluetoothGattCharacteristic sound;

                sound = services.get(2).getCharacteristics().get(1);
                sound.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                sound.setValue(data, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.i("TAG", String.valueOf(gatt.writeCharacteristic(sound)
                ));


//                for(BluetoothGattService service: services){
//                    j=0;
//                    for(BluetoothGattCharacteristic characteristic: service.getCharacteristics()){
//
//                        if(characteristic.getUuid().toString()==cist)
//                            Log.i("maa ka bhosda", characteristic.getService().getUuid().toString());
//                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//                        characteristic.setValue(data, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//
//                        gatt.writeCharacteristic(characteristic);
//                        //Log.i("onServicesDiscovered", String.valueOf(gatt.writeCharacteristic(characteristic)));
//                        Log.i("onServicesDiscovered", characteristic.getUuid().toString()+"i = "+i+" j= "+j);
//                        j++;
//                    }
//                    i++;
//                }




                //BluetoothGattCharacteristic characteristic =
                 //       gatt.getService(UUID.fromString("b68ed28b-7057-4cfb-9315-fda5efe62a39")).getCharacteristic(UUID.fromString("f17129f5-64b9-4f24-bcfd-c53a8bdd3893"));
                //gatt.setCharacteristicNotification(characteristic, true);


                //Log.i("TAG", "hogaya");

//                BluetoothGattDescriptor descriptor =
//                        characteristic.getDescriptor(UUID.fromString("0x2902"));
//
//                descriptor.setValue(
//                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);

//                int it = 0x31;
//                BigInteger bigInteger = BigInteger.valueOf(it);
//                byte[] bytearr = bigInteger.toByteArray();
//
//                BluetoothGattCharacteristic characteristic =
//                        gatt.getService(UUID.fromString("b68ed28b-7057-4cfb-9315-fda5efe62a39"))
//                                .getCharacteristic(UUID.fromString("f17129f5-64b9-4f24-bcfd-c53a8bdd3893"));
//                characteristic.setValue(bytearr);
//                gatt.writeCharacteristic(characteristic);
            }
        };

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                gatt = devices.get(i).connectGatt(getApplicationContext(), true, gattCallback);
//                ArrayList<BluetoothGattService> services = new ArrayList<>();
//                services = (ArrayList<BluetoothGattService>) gatt.getServices();
//
//                for( BluetoothGattService bgs: services){
//                    Log.i("UUID: ", bgs.getUuid().toString());
//                }
//            }
//        });

        //listView.setAdapter(adapter);
        //listView.setAdapter(adapter);


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
            Log.i("MainActivity", "scanning");
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Ici///////////////////////////////////////
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }


                            if (true) {

                                if(device.getName()!=null && !list.contains(device.getName())) {
                                    list.add(device.getName());
                                    devices.add(device);
                                    //listView.setAdapter(adapter);

                                    //scanLeDevice(false);
                                }

//                                scanLeDevice(false);
                            }
                        }
                    });
                }
            };



}