package com.example.bayiku.easyble.gatt;


import android.bluetooth.BluetoothGatt;

import com.example.bayiku.easyble.BleDevice;
import com.example.bayiku.easyble.gatt.bean.CharacteristicInfo;
import com.example.bayiku.easyble.gatt.bean.ServiceInfo;
import com.example.bayiku.easyble.gatt.callback.BleConnectCallback;
import com.example.bayiku.easyble.gatt.callback.BleMtuCallback;
import com.example.bayiku.easyble.gatt.callback.BleNotifyCallback;
import com.example.bayiku.easyble.gatt.callback.BleReadCallback;
import com.example.bayiku.easyble.gatt.callback.BleRssiCallback;
import com.example.bayiku.easyble.gatt.callback.BleWriteByBatchCallback;
import com.example.bayiku.easyble.gatt.callback.BleWriteCallback;

import java.util.List;
import java.util.Map;

public interface BleGatt {
    void connect(int connectTimeout, BleDevice device, BleConnectCallback callback);

    void disconnect(String address);

    void disconnectAll();

    void notify(BleDevice device, String serviceUuid, String notifyUuid, BleNotifyCallback callback);

    void cancelNotify(BleDevice device, String serviceUuid, String characteristicUuid);

    void read(BleDevice device, String serviceUuid, String readUuid, BleReadCallback callback);

    void write(BleDevice device, String serviceUuid, String writeUuid, byte[] data, BleWriteCallback callback);

    void writeByBatch(BleDevice device, String serviceUuid, String writeUuid, byte[] data, int lengthPerPackage, long writeDelay, BleWriteByBatchCallback callback);

    void readRssi(BleDevice device, BleRssiCallback callback);

    void setMtu(BleDevice device, int mtu, BleMtuCallback callback);

    List<BleDevice> getConnectedDevices();

    Map<ServiceInfo, List<CharacteristicInfo>> getDeviceServices(String address);

    BluetoothGatt getBluetoothGatt(String address);

    boolean isConnecting(String address);

    void destroy();
}
