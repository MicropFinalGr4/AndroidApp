package com.example.android.misc;

import android.util.Log;

/**
 * Created by Francis on 4/4/2016.
 */
public class Utils {

    public static int byteArrayToUnsignedInt(byte[] bytes){
        int val = 0;
        for (int i = 0; i < bytes.length; i++){
            val |= bytes[i] << 8*i;
        }
        return val;
    }

    final private static String[] hexArray = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    public static String bytesToHexString(byte[] bytes) {
        String rtn = "";
        for ( int j = 0; j < bytes.length; j++ ) {
            int val = bytes[j];
            rtn = hexArray[(val & 0xF0) >> 4] + hexArray[val & 0x0F] + ":" + rtn;
        }
        return rtn;
    }

    public static short[] bytesTotwoBitInt(byte[] bytes){
        String str = bytesToHexString(bytes);
        String[] split = str.split(":");
        short[] shorts = new short[split.length / 2];
        for (int i = 0; i < split.length; i += 2){
            shorts[i/2] = (short) Integer.parseInt(split[i] + split[i+1], 16);
        }
        return shorts;
    }

}
