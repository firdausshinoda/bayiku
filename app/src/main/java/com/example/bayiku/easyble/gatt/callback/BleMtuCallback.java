package com.example.bayiku.easyble.gatt.callback;


import com.example.bayiku.easyble.BleDevice;


public interface BleMtuCallback extends BleCallback {
    void onMtuChanged(int mtu, BleDevice device);
}
