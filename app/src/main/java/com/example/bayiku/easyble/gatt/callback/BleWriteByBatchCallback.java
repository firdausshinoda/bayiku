package com.example.bayiku.easyble.gatt.callback;


import com.example.bayiku.easyble.BleDevice;

public interface BleWriteByBatchCallback extends BleCallback {
    void writeByBatchSuccess(byte[] data, BleDevice device);
}
