package com.example.livelib.Push.Util;

/**
 * Created by Hamik Young on 2018/1/12.
 */

public class ByteTransitionUtil {
public static byte[] intTobyte(int a){
    return new byte[] {
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),
            (byte) ((a >> 8) & 0xFF),
            (byte) (a & 0xFF)
    };
}

public static int byteToInt(byte[] b){
    return   b[3] & 0xFF |
            (b[2] & 0xFF) << 8 |
            (b[1] & 0xFF) << 16 |
            (b[0] & 0xFF) << 24;
}



}
