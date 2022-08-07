package com.example.bayiku.utils;

import java.util.Locale;

public class ByteUtils {
    public static String bytes2HexStr(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i == (bytes.length-1)) {
                b.append(String.format("%02x", bytes[i] & 0xFF));
            } else {
                b.append(String.format("%02x", bytes[i] & 0xFF).toUpperCase(Locale.ROOT)+"-");
            }
        }
        return b.toString();
    }

    public static byte[] hexStr2Bytes(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }
}
