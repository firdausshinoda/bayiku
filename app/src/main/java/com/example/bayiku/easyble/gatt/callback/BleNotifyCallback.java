package com.example.bayiku.easyble.gatt.callback;


import com.example.bayiku.easyble.BleDevice;

public interface BleNotifyCallback extends BleCallback {
    void onCharacteristicChanged(byte[] data, BleDevice device);

    void onNotifySuccess(String notifySuccessUuid, BleDevice device);
}
