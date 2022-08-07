package com.example.bayiku.easyble.scan;


import com.example.bayiku.easyble.BleDevice;

public interface BleScanCallback {
    void onLeScan(BleDevice device, int rssi, byte[] scanRecord);

    void onStart(boolean startScanSuccess, String info);

    void onFinish();
}
