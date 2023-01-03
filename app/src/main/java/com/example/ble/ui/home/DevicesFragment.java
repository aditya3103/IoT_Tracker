package com.example.ble.ui.home;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

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
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ble.R;

import java.util.ArrayList;
import java.util.List;

public class DevicesFragment extends Fragment {


    public static RecyclerView recyclerView;
    private static BluetoothGatt gatt;
    ArrayList<Device> list = new ArrayList<>();
    ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> rssiVals = new ArrayList<>();
    ArrayList<Location> locations = new ArrayList<Location>();

    BluetoothAdapter bluetoothAdapter;
    DevicesListAdapter adapter;
    boolean mScanning;

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler = new Handler();
    BluetoothManager bluetoothManager;

    private static final long SCAN_PERIOD = 10000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
        }

        bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.devices_recycler);
        adapter = new DevicesListAdapter(list, getContext());
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    mScanning = false;
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
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void run() {
                            if (device.getName() != null && !devices.contains(device)) {

                                devices.add(device);
                                rssiVals.add(rssi);
                                names.add(device.getName());
                                list.add(new Device(device, rssi));
                                recyclerView.setAdapter(adapter);

                            }
                        }
                    });
                }
            };

    @SuppressLint("MissingPermission")
    public static void startFeature(BluetoothDevice device, int feature, int state) {


        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == STATE_CONNECTED)

                    gatt.discoverServices();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                int dataON = 0x31;
                int dataOFF = 0x30;
                List<BluetoothGattService> services = gatt.getServices();
                BluetoothGattCharacteristic sound = services.get(2).getCharacteristics().get(1);
                BluetoothGattCharacteristic light = services.get(2).getCharacteristics().get(0);
                gatt.setCharacteristicNotification(services.get(2).getCharacteristics().get(2), true);

                if (feature == 1) {
                    light.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    light.setValue(state == 0 ? dataOFF : dataON, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Log.i("TAG", String.valueOf(gatt.writeCharacteristic(light)));
                } else if (feature == 2) {

                    sound.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    sound.setValue(state == 0 ? dataOFF : dataON, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Log.i("TAG", String.valueOf(gatt.writeCharacteristic(sound)));
                } else {

                }

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.i("TAG", "Pressed");
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }
        };

        gatt = device.connectGatt(recyclerView.getContext(), true, gattCallback);
    }

}
