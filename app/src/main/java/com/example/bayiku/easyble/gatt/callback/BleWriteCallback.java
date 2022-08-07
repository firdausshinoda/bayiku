package com.example.bayiku.easyble.gatt.callback;


import com.example.bayiku.easyble.BleDevice;

public interface BleWriteCallback extends BleCallback {
    void onWriteSuccess(byte[] data, BleDevice device);
}
