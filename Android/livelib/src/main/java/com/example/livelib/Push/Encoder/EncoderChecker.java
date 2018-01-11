package com.example.livelib.Push.Encoder;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;

/**
 * Created by Hamik Young on 2018/1/11.
 */

public class EncoderChecker {

 public static boolean isSupportEncoderType(String mimeType){
     int CodecNum = MediaCodecList.getCodecCount();
     for (int i = 0; i < CodecNum; i++) {
         MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
         if (!codecInfo.isEncoder()) {
             continue;
         }
         String[] types = codecInfo.getSupportedTypes();

         for (String type:types) {
             if (type.equalsIgnoreCase(mimeType)){
                return true;
            }
         }
     }
     return false;
 }



}
