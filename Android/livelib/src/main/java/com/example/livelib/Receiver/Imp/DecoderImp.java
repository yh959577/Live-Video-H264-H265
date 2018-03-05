package com.example.livelib.Receiver.Imp;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.livelib.Receiver.Interface.Decoder;
import com.example.livelib.Receiver.Interface.Receiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by UPC on 2018/1/18.
 */

public class DecoderImp implements Decoder {
    private String MIME_TYPE = null;
    private Thread mDecodeThread;
    private Thread mJointFrameThread;
    private MediaCodec mMediaCodec = null;
    private MediaFormat mMediaFormat = null;
    private boolean isDecodeRunning;
    private final String TAG = "DecoderImp";
    private byte[] mDecodePpsH264 = null;
    private byte[] mDecoderSpsH264 = null;
    private SurfaceHolder mHolder;
    private FileOutputStream fileOutputStream;

    DecoderImp(SurfaceHolder holder) {
        mHolder = holder;
        mDecodeThread = new DecodeThread();
        mJointFrameThread = new JointFrameThread();
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().
                    getPath() + "/udp264.h264"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startDecode() {
        isDecodeRunning = true;
        mJointFrameThread.start();
        //mDecodeThread.start();
    }

    @Override
    public void stopDecode() {
        isDecodeRunning = false;
    }


    private class DecodeThread extends Thread {


        @Override
        public void run() {
            while (isDecodeRunning) {
                byte[] frameData = ReceiveQueueManager.getDataFromFrameQueue();
                if (frameData != null) {
                    Log.i(TAG, "the frame content: " + Arrays.toString(frameData));
                        //check video info
                    if (mMediaCodec==null) {
                        if (isH264SpsHead(frameData)) {
                            Log.i(TAG, "Find 264 frame!!!");
                            MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
                            int spsLen = findSpsLen(frameData, frameData.length);
                            int ppsLen = findPpsLen(frameData, frameData.length) - spsLen;
                            if (mDecodePpsH264 == null || mDecoderSpsH264 == null ||
                                    (mDecoderSpsH264.length != spsLen) || (mDecodePpsH264.length != ppsLen)) {
                                mDecoderSpsH264 = new byte[spsLen];
                                mDecodePpsH264 = new byte[ppsLen];
                            }
                            if (spsLen > 0) {
                                byte[] sps = new byte[spsLen];
                                byte[] pps = new byte[ppsLen];
                                System.arraycopy(frameData, 0, sps, 0, spsLen);
                                System.arraycopy(frameData, spsLen, pps, 0, ppsLen);
                                if ((!Arrays.equals(mDecoderSpsH264, sps)) || (!Arrays.equals(mDecodePpsH264, pps))) {
                                    mDecoderSpsH264 = sps.clone();
                                    mDecodePpsH264 = pps.clone();
                                    try {
                                        resetMediaDecoder(mDecoderSpsH264, mDecodePpsH264, MIME_TYPE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.i(TAG, "spsContent: " + Arrays.toString(sps));
                                Log.i(TAG, "ppsContent: " + Arrays.toString(pps));
                                if (mMediaCodec != null)
                                    decodeData(frameData);
                            } else {
                                Log.i(TAG, "Sps error!!!");
                            }
                        }
                    }
                        else {
                            Log.i(TAG, "Normal frame!!!");
                            if (mMediaCodec != null)
                                decodeData(frameData);
                        }

                }else{
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


        private void resetMediaDecoder(byte[] sps, byte[] pps, String codecType) throws IOException {
            Log.i(TAG, "resetMediaDecoder: ");
            if (mMediaCodec != null || mMediaFormat != null) {
                mMediaCodec.release();
                mMediaCodec.stop();
                mMediaCodec = null;
                mMediaFormat = null;
            }
            mMediaCodec = MediaCodec.createDecoderByType(codecType);
            //set sps pps can ignore width and height
            mMediaFormat = MediaFormat.createVideoFormat(codecType, 0, 0);
            mMediaFormat.setInteger(MediaFormat.KEY_ROTATION, 90);
            if (codecType.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
                mMediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
                mMediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
            } else if (codecType.equals(MediaFormat.MIMETYPE_VIDEO_HEVC)) {

            }
            mMediaCodec.configure(mMediaFormat, mHolder.getSurface(), null, 0);
            mMediaCodec.start();
        }

        private void decodeData(byte[] frame) {
            try {
                fileOutputStream.write(frame);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //  -1表示一直等待；0表示不等待；其他大于0的参数表示等待毫秒数
            int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex);
                //清空buffer
                inputBuffer.clear();
                //put需要解码的数据
                inputBuffer.put(frame, 0, frame.length);
                //解码
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, frame.length, 0, 0);

            }
            // 获取输出buffer index
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100);
            //循环解码，直到数据全部解码完成
            while (outputBufferIndex >= 0) {
                //logger.d("outputBufferIndex = " + outputBufferIndex);
                //true : 将解码的数据显示到surface上
                mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
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
                while (isDecodeRunning) {
                    //  Log.i(TAG, "startJointFrame the OrderListSize===: " + ReceiveQueueManager.getOrderListSize());
                    if (ReceiveQueueManager.getOrderQueueSize() > 30) {
                        int headPosition = 0;
                        UdpStruct udpStruct = ReceiveQueueManager.getUdpFromOrderQueue();
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
                                            ReceiveQueueManager.addDataToFrameQueue(frameData);
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
                                        ReceiveQueueManager.addDataToFrameQueue(frameData);
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

                    }
                    if ((!mDecodeThread.isAlive())&&ReceiveQueueManager.getFrameQueueSize()>30)
                        mDecodeThread.start();

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
            for (int i = 0; i < len; i++) {
                if (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x68) {
                    return i;
                }
            }
            return 0;
        }

        private int findPpsLen(byte[] buf, int len) {
            for (int i = 0; i < len; i++) {
                if (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x65) {
                    return i;
                }
            }
            return 0;
        }
    }

