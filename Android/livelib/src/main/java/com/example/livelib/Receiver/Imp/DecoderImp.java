package com.example.livelib.Receiver.Imp;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.livelib.Receiver.Interface.Decoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by UPC on 2018/1/18.
 */

public class DecoderImp implements Decoder {
    private String MIME_TYPE = null;


    private MediaCodec mH264MediaCodec = null;
    private MediaFormat mH264MediaFormat = null;

    private MediaCodec mH265MediaCodec=null;
    private MediaFormat mH265MediaFormat=null;

    private boolean isDecodeRunning;
    private final String TAG = "DecoderImp";
    private byte[] mH264Pps = null;
    private byte[] mH264Sps = null;
    private SurfaceHolder mHolder;
    //private FileOutputStream fileOutputStream;
    private ExecutorService mDecodeService;

    private byte[] m264FrameBuf;
    private int m264FrameShift=0;

    private byte[] m265FrameBuf;
    private int m265FrameShift=0;
    private JointFrameThread jointFrameThread;

    DecoderImp(SurfaceHolder holder) {
        mHolder = holder;
        mDecodeService= Executors.newFixedThreadPool(1) ;
        m264FrameBuf=new byte[1024*1024];
        m265FrameBuf=new byte[1024*1024];
//        try {
//            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().
//                    getPath() + "/udp264.h264"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        try {
            initialH264Decoder();
            //initialH265Decoder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialH264Decoder() throws IOException {
        mH264MediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        //set sps pps can ignore width and height
        mH264MediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 0, 0);
        mH264MediaCodec.configure(mH264MediaFormat, mHolder.getSurface(), null, 0);
        mH264MediaCodec.start();
        jointFrameThread=new JointFrameThread();
    }

    private void initialH265Decoder() throws IOException {

        mH265MediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
        //set sps pps can ignore width and height
        mH265MediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, 0, 0);
        mH265MediaCodec.configure(mH265MediaFormat, mHolder.getSurface(), null, 0);
        mH265MediaCodec.start();

    }

    @Override
    public void startDecode() {

//        mDecodeService.submit(()->{
//            //joinFrame
//            while (!Thread.currentThread().isInterrupted()){
//                try {
//                    if (ReceiveQueueManager.get264OrderQueueSize() > 30) {
//                        Log.i(TAG, "startJointFrame: ");
//
//
//
//
//
//
//
//
//
//
//
//
//
//                   //  m264FrameShift= jointFrame(ReceiveQueueManager.get264UdpFromOrderQueue(), new FrameMethod(FrameMethod.H264_METHOD)
//                //        ,m264FrameBuf,m264FrameShift);
//                        //joint264Frame();
//                    } else {
//                        Thread.sleep(1);
//                    }
//                }
//                 catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        mDecodeService.submit(()->{
               while (!Thread.currentThread().isInterrupted()) {
                   try {
                       byte[] frameData = ReceiveQueueManager.getDataFromH264FrameQueue();
                       Log.i(TAG, "the frame content: " + Arrays.toString(frameData));
                       //check video info
                           if (isH264SpsHead(frameData)) {
                               Log.i(TAG, "Find 264 key frame!!!");
                               //MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;

                               int spsLen = findSpsLen(frameData, frameData.length);
                               int ppsLen = findPpsLen(frameData, frameData.length) - spsLen;

                               byte[] sps = new byte[spsLen];
                               byte[] pps = new byte[ppsLen];
                               System.arraycopy(frameData, 0, sps, 0, spsLen);
                               System.arraycopy(frameData, spsLen, pps, 0, ppsLen);

                               if (!Arrays.equals(mH264Sps,sps)){
                                   mH264Sps=sps.clone();
                                   mH264MediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));

                               }
                               if (!Arrays.equals(mH264Pps,pps)) {
                                   mH264Pps = pps.clone();
                                   mH264MediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
                               }
                           }
                           decodeData(frameData);

                   }catch (InterruptedException e){
                       e.printStackTrace();
                   }
               }
        });

        jointFrameThread.start();

    }


//    private int jointFrame(UdpStruct udpStruct, FrameMethod frameMethod,byte[] frameBuf,int frameShift){
//
//        int headPosition = frameMethod.find_frame_head(udpStruct.getVideoData(), udpStruct.getVideoDataLen());
//        if (headPosition != -1) {
//            if (headPosition == 0) {
//                //I frame in the packet head
//                //send frameBuf,clear frameBuf,write frameBuf
//                //        Log.i(TAG, "I frame in the packet head");
//                if (frameShift > 0) {
//                    byte[] frameData = new byte[frameShift];
//                    System.arraycopy(frameBuf, 0, frameData, 0, frameShift);
//                    //ReceiveQueueManager.addDataToFrameQueue(frameData);
//                    //ReceiveQueueManager.addDataToH264FrameQueue(frameData);
//                    frameMethod.addDataToFrameQueue(frameData);
//                    frameShift = 0;
//                }
//                System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, 0, udpStruct.getVideoDataLen());
//                frameShift += udpStruct.getVideoDataLen();
//
//            } else {
//
//                //I frame in the  middle of the packet
//                System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, frameShift, headPosition);
//                frameShift += headPosition;
//                byte[] frameData = new byte[frameShift];
//                System.arraycopy(frameBuf, 0, frameData, 0, frameShift);
//                //ReceiveQueueManager.addDataToFrameQueue(frameData);
//                //ReceiveQueueManager.addDataToH264FrameQueue(frameData);
//                frameMethod.addDataToFrameQueue(frameData);
//                frameShift = 0;
//                System.arraycopy(udpStruct.getVideoData(), headPosition, frameBuf, 0,
//                        udpStruct.getVideoDataLen() - headPosition);
//                frameShift += (udpStruct.getVideoDataLen() - headPosition);
//            }
//        } else {
//            //not have I frame
//            //write to frame_buf
//
//            System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, frameShift, udpStruct.getVideoDataLen());
//            frameShift += udpStruct.getVideoDataLen();
//        }
//       return frameShift;
//    }


//    private static  class FrameMethod{
//         static int H264_METHOD=0x91;
//         static int H265_METHOD=0x92;
//         int mType;
//
//        FrameMethod(int type){
//          mType=type;
//        }
//        public int find_frame_head(byte[] buf, int len){
//            if (mType==H264_METHOD)
//                return find_H264_frame_head(buf,len);
//            else if (mType==H265_METHOD)
//                return find_H265_frame_head(buf,len);
//            else return -1;
//        }
//
//        public void addDataToFrameQueue(byte[] frameData){
//
//            if (mType==H264_METHOD)
//                 ReceiveQueueManager.addDataToH264FrameQueue(frameData);
//            else if (mType==H265_METHOD)
//                 ReceiveQueueManager.addDataToH265FrameQueue(frameData);
//        }
//
//        private int find_H264_frame_head(byte[] buf, int len) {
//            int i;
//            boolean isMatch = false;
//            for (i = 0; i < len - 4; i++) {
//                if ((buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x41
//                ) || (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x67)) {
//                    isMatch = true;
//                    break;
//                }
//            }
//            return isMatch ? i : -1;
//        }
//
//        int find_H265_frame_head(byte[] buf, int len) {
//            int i;
//            boolean isMatch = false;
//            for (i = 0; i < len - 4; i++) {
//                if ((buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 2
//                ) || (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x40)) {
//                    isMatch = true;
//                    break;
//                }
//            }
//            return isMatch ? i : -1;
//        }
//    }

    @Override
    public void stopDecode() {
       // isDecodeRunning = false;
        mDecodeService.shutdown();
    }


//    private class DecodeThread extends Thread {
//
//
//        @Override
//        public void run() {
//            while (isDecodeRunning) {
//                byte[] frameData = ReceiveQueueManager.getDataFromFrameQueue();
//                if (frameData != null) {
//                    Log.i(TAG, "the frame content: " + Arrays.toString(frameData));
//                        //check video info
//                    if (mH264MediaCodec==null) {
//                        if (isH264SpsHead(frameData)) {
//                            Log.i(TAG, "Find 264 frame!!!");
//                            MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
//                            int spsLen = findSpsLen(frameData, frameData.length);
//                            int ppsLen = findPpsLen(frameData, frameData.length) - spsLen;
//                            if (mDecodePpsH264 == null || mDecoderSpsH264 == null ||
//                                    (mDecoderSpsH264.length != spsLen) || (mDecodePpsH264.length != ppsLen)) {
//                                mDecoderSpsH264 = new byte[spsLen];
//                                mDecodePpsH264 = new byte[ppsLen];
//                            }
//                            if (spsLen > 0) {
//                                byte[] sps = new byte[spsLen];
//                                byte[] pps = new byte[ppsLen];
//                                System.arraycopy(frameData, 0, sps, 0, spsLen);
//                                System.arraycopy(frameData, spsLen, pps, 0, ppsLen);
//                                if ((!Arrays.equals(mDecoderSpsH264, sps)) || (!Arrays.equals(mDecodePpsH264, pps))) {
//                                    mDecoderSpsH264 = sps.clone();
//                                    mDecodePpsH264 = pps.clone();
//                                    try {
//                                        resetMediaDecoder(mDecoderSpsH264, mDecodePpsH264, MIME_TYPE);
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                Log.i(TAG, "spsContent: " + Arrays.toString(sps));
//                                Log.i(TAG, "ppsContent: " + Arrays.toString(pps));
//                                if (mH264MediaCodec != null)
//                                    decodeData(frameData);
//                            } else {
//                                Log.i(TAG, "Sps error!!!");
//                            }
//                        }
//                    }
//                        else {
//                            Log.i(TAG, "Normal frame!!!");
//                            if (mH264MediaCodec != null)
//                                decodeData(frameData);
//                        }
//
//                }else{
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }


//        private void resetMediaDecoder(byte[] sps, byte[] pps, String codecType) throws IOException {
//            Log.i(TAG, "resetMediaDecoder: ");
//            if (mH264MediaCodec != null || mH264MediaFormat != null) {
//                mH264MediaCodec.release();
//                mH264MediaCodec.stop();
//                mH264MediaCodec = null;
//                mH264MediaFormat = null;
//            }
//            mH264MediaCodec = MediaCodec.createDecoderByType(codecType);
//            //set sps pps can ignore width and height
//            mH264MediaFormat = MediaFormat.createVideoFormat(codecType, 0, 0);
//            mH264MediaFormat.setInteger(MediaFormat.KEY_ROTATION, 90);
//            if (codecType.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
//                mH264MediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
//                mH264MediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
//            } else if (codecType.equals(MediaFormat.MIMETYPE_VIDEO_HEVC)) {
//
//            }
//            mH264MediaCodec.configure(mH264MediaFormat, mHolder.getSurface(), null, 0);
//            mH264MediaCodec.start();
//        }

        private void decodeData(byte[] frame) {
//            try {
//                fileOutputStream.write(frame);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //  -1表示一直等待；0表示不等待；其他大于0的参数表示等待毫秒数
            int inputBufferIndex = mH264MediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mH264MediaCodec.getInputBuffer(inputBufferIndex);
                //清空buffer
                inputBuffer.clear();
                //put需要解码的数据
                inputBuffer.put(frame, 0, frame.length);
                //解码
                mH264MediaCodec.queueInputBuffer(inputBufferIndex, 0, frame.length, 0, 0);

            }
            // 获取输出buffer index
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mH264MediaCodec.dequeueOutputBuffer(bufferInfo, 100);
            //循环解码，直到数据全部解码完成
            while (outputBufferIndex >= 0) {
                //logger.d("outputBufferIndex = " + outputBufferIndex);
                //true : 将解码的数据显示到surface上
                mH264MediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mH264MediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
            if (outputBufferIndex < 0) {
                //logger.e("outputBufferIndex = " + outputBufferIndex);
                Log.i(TAG, "decodeDataFailed: ");
            }
        }



        private class JointFrameThread extends Thread {
            private byte[] frameBuf;
            private int frameShift;

            JointFrameThread() {
                frameBuf = new byte[1024 * 1024];
                frameShift = 0;
            }
            @Override
            public void run() {
                while (true) {
                    //  Log.i(TAG, "startJointFrame the OrderListSize===: " + ReceiveQueueManager.getOrderListSize());
                    if (ReceiveQueueManager.get264OrderQueueSize() > 30) {
                       try {


                           int headPosition = 0;
                           UdpStruct udpStruct = ReceiveQueueManager.get264UdpFromOrderQueue();
                           if (udpStruct != null) {
                               if (udpStruct.getVideoTypeTag() == 'r') {
                                   Log.i(TAG, "find this is H264 format");
                                   MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
                                   headPosition = find_H264_frame_head(udpStruct.getVideoData(), udpStruct.getVideoDataLen());
                                   //      Log.i(TAG, "udpStruct.getVideoData().length: ==" + udpStruct.getVideoDataLen());
                               } else if (udpStruct.getVideoTypeTag() == 'e') {
                                   Log.i(TAG, "find this is H265 format");
                                   MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_HEVC;
                                   headPosition = find_H265_frame_head(udpStruct.getVideoData(), udpStruct.getVideoDataLen());
                               } else {
                                   Log.i(TAG, "find this is wrong format no format info in packet: ");
                                   headPosition = -2;
                               }
                               if (headPosition != -2) {

                                   if (headPosition != -1) {
                                       if (headPosition == 0) {   //I frame in the packet head
                                           //send frameBuf,clear frameBuf,write frameBuf
                                           //        Log.i(TAG, "I frame in the packet head");
                                           if (frameShift > 0) {
                                               byte[] frameData = new byte[frameShift];
                                               System.arraycopy(frameBuf, 0, frameData, 0, frameShift);
                                               ReceiveQueueManager.addDataToH264FrameQueue(frameData);
                                               frameShift = 0;
                                           }
                                           System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, 0, udpStruct.getVideoDataLen());
                                           frameShift += udpStruct.getVideoDataLen();

                                       } else {

                                           //I frame in the  middle of the packet
                                           System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, frameShift, headPosition);
                                           frameShift += headPosition;
                                           byte[] frameData = new byte[frameShift];
                                           System.arraycopy(frameBuf, 0, frameData, 0, frameShift);
                                           ReceiveQueueManager.addDataToH264FrameQueue(frameData);
                                           frameShift = 0;
                                           System.arraycopy(udpStruct.getVideoData(), headPosition, frameBuf, 0,
                                                   udpStruct.getVideoDataLen() - headPosition);
                                           frameShift += (udpStruct.getVideoDataLen() - headPosition);
                                       }
                                   } else {
                                       //not have I frame
                                       //write to frame_buf

                                       System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, frameShift, udpStruct.getVideoDataLen());
                                       frameShift += udpStruct.getVideoDataLen();

                                   }
                               }
                           }
                       }catch (InterruptedException e){

                           e.printStackTrace();
                       }
                    }else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            int find_H264_frame_head(byte[] buf, int len) {
                int i;
                boolean isMatch = false;
                for (i = 0; i < len - 4; i++) {
                    if ((buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x41
                    ) || (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x67)) {
                        isMatch = true;
                        break;
                    }
                }
                return isMatch ? i : -1;
            }

            int find_H265_frame_head(byte[] buf, int len) {
                int i;
                boolean isMatch = false;
                for (i = 0; i < len - 4; i++) {
                    if ((buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 2
                    ) || (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x40)) {
                        isMatch = true;
                        break;
                    }
                }
                return isMatch ? i : -1;
            }
        }

        private boolean isH264Head(byte[] frame) {
            return (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 1 && frame[4] == 0x41
            ) || (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 1 && frame[4] == 0x67);
        }

        private boolean isH265Head(byte[] frame) {
            return (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 1 && frame[4] == 2
            ) || (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 1 && frame[4] == 0x40);
        }

        private boolean isH264SpsHead(byte[] frame) {
            return (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 1 && frame[4] == 0x67);
        }

        private int findSpsLen(byte[] buf, int len) {
            for (int i = 0; i < len-4; i++) {
                if (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x68) {
                    return i;
                }
            }
            return 0;
        }

        private int findPpsLen(byte[] buf, int len) {
            for (int i = 0; i < len-4; i++) {
                if (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x65) {
                    return i;
                }
            }
            return 0;
        }

    }

