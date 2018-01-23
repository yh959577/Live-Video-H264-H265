package com.example.livelib.Util;

import java.nio.ByteBuffer;

/**
 * Created by Hamik Young on 2018/1/12.
 */

public class ByteTransitionUtil {

    public static byte[] intToByte(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static int byteToInt(byte...bytes) {
        return bytes[3] & 0xFF |
                (bytes[2] & 0xFF) << 8 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[0] & 0xFF) << 24;
    }


    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((x >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytesToLong(byte... bytes) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (bytes[ix] & 0xff);
        }
        return num;
    }


}
