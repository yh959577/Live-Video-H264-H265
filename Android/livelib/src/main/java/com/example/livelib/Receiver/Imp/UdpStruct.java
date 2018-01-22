package com.example.livelib.Receiver.Imp;

import com.example.livelib.Util.ByteTransitionUtil;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public class UdpStruct {
    private int mSequenceNum;
    private long mTimeNum;
    private byte[] mVideoData;
    private byte mVideoTypeTag;

    UdpStruct(byte[] data,int len){
       mSequenceNum= ByteTransitionUtil.byteToInt(data[0],data[1],data[2],data[3]);
       mTimeNum=ByteTransitionUtil.bytesToLong(data[4],data[5],data[6],data[7],
               data[8],data[9],data[10],data[11]);
       mVideoTypeTag=data[12];

       mVideoData=new byte[len-13];
       System.arraycopy(data,13,mVideoData,0,mVideoData.length);
    }

    int getSequenceNum(){
        return mSequenceNum;
    }

    long getTimeNum(){
        return mTimeNum;
    }

    byte[] getVideoData(){
         return mVideoData;
    }

    byte getVideoTypeTag(){return mVideoTypeTag;}

}
