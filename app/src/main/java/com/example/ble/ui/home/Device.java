package com.example.ble.ui.home;

import android.bluetooth.BluetoothDevice;

import java.sql.Time;

public class Device {
    BluetoothDevice bluetoothDevice;
    int rssi;
    String id;
    String name;
    boolean status;
    Time connectionStart;
    Time connectionEnd;

    public Device() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Device(BluetoothDevice device, int rssi) {
        this.bluetoothDevice = device;
        this.rssi = rssi;
    }
}

