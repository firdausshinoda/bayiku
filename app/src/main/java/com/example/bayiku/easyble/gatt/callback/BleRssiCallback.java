package com.example.bayiku.easyble.gatt.callback;


import com.example.bayiku.easyble.BleDevice;

public interface BleRssiCallback extends BleCallback {

    void onRssi(int rssi, BleDevice bleDevice);
}
