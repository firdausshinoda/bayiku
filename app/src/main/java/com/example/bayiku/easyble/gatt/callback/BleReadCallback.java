package com.example.bayiku.easyble.gatt.callback;


import com.example.bayiku.easyble.BleDevice;

public interface BleReadCallback extends BleCallback {
    void onReadSuccess(byte[] data, BleDevice device);
}
