package com.example.livelib.Receiver.Imp;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import com.example.livelib.Receiver.Interface.Decoder;
import com.example.livelib.Util.H264SPSPaser;

import java.util.Arrays;

/**
 * Created by UPC on 2018/1/18.
 */

public class DecoderImp implements Decoder {
    private String MIME_TYPE = null;
    private int mDecodeWidth = 0;
    private int mDecodeHeight = 0;
    private Thread mDecodeThread;
    private Thread mJointFrameThread;
    private MediaCodec mMediaCodec = null;
    private MediaFormat mMediaFormat = null;
    private boolean isDecodeRunning;
    private final String TAG = "DecoderImp";

    DecoderImp() {
        mDecodeThread = new DecodeThread();
        mJointFrameThread = new JointFrameThread();

    }

    @Override
    public void startDecode() {
        isDecodeRunning = true;
        mJointFrameThread.start();
        mDecodeThread.start();
    }

    @Override
    public void stopDecode() {
        isDecodeRunning = false;
    }


    private class DecodeThread extends Thread {


        @Override
        public void run() {
            while (isDecodeRunning) {
                byte[] frameData = QueueManager.getDataFromFrameQueue();
                if (frameData != null) {
                    Log.i(TAG, "the frame content: " + Arrays.toString(frameData));
                    //check video info
                    if (isSpsHead(frameData)) {
                        int len = findNextFrame(frameData, frameData.length);
                        if (len > 0) {
                            byte[] sps = new byte[len-5];

                            System.arraycopy(frameData, 5, sps, 0, len-5);
                            Log.i(TAG, "spsContent: "+Arrays.toString(sps));
                            int decodeWidth = (H264SPSPaser.ue(sps, 34) + 1) * 16;
                            int decodeHeight = (H264SPSPaser.ue(sps, -1) + 1) * 16;
                            Log.i(TAG, "the video width====: " + decodeWidth);
                            Log.i(TAG, "the video ====: " + decodeHeight);
                            Log.i(TAG, "the sps content====: " + Arrays.toString(sps));

                        } else {
                            Log.i(TAG, "Sps error!!!");
                        }
                    }
                }
            }
        }

        private void InitialMediaCodec() {

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
                //  Log.i(TAG, "startJointFrame the OrderListSize===: " + QueueManager.getOrderListSize());
                if (QueueManager.getOrderQueueSize() > 30) {
                    int headPosition = 0;
                    UdpStruct udpStruct = QueueManager.getUdpFromOrderQueue();
                    if (udpStruct != null) {
                        if (udpStruct.getVideoTypeTag() == 'r') {
                            Log.i(TAG, "find this is H264 format");
                            headPosition = find_H264_frame_head(udpStruct.getVideoData(), udpStruct.getVideoData().length);
                            //      Log.i(TAG, "udpStruct.getVideoData().length: ==" + udpStruct.getVideoDataLen());
                        } else if (udpStruct.getVideoTypeTag() == 'e') {
                            Log.i(TAG, "find this is H265 format");
                            headPosition = find_H265_frame_head(udpStruct.getVideoData(), udpStruct.getVideoData().length);
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
                                        QueueManager.addDataToFrameQueue(frameData);
                                        frameShift = 0;
//                                frameData* f = new frameData;
//                                f->time = m_orderedList.front()->getTime();
//                                f->frameDataLen = m_frameShift;
//                                f->data = new char[f->frameDataLen];
//                                memcpy(f->data, m_frame_buf, m_frameShift);
//                                m_queueMut->lock();
//                                m_frameQueue->push(f);
//                                m_queueMut->unlock();
//
//                                ZeroMemory(m_frame_buf, m_frame_buf_size);
//                                m_frameShift = 0;
                                    }
                                    System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, 0, udpStruct.getVideoDataLen());
                                    frameShift += udpStruct.getVideoDataLen();
//                            memcpy(m_frame_buf, videoData, videoDataLen);
//                            m_frameShift += videoDataLen;

                                } else {
                                    //      Log.i(TAG, "run: I frame in the  middle of the packet");
                                    //I frame in the  middle of the packet
                                    System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, frameShift, headPosition);
                                    frameShift += headPosition;
                                    byte[] frameData = new byte[frameShift];
                                    System.arraycopy(frameBuf, 0, frameData, 0, frameShift);
                                    QueueManager.addDataToFrameQueue(frameData);

                                    //      memcpy(m_frame_buf + m_frameShift, videoData, headPosition);
                                    //     m_frameShift += headPosition;

                                    //       frameData *f = new frameData;
                                    //     f->time = m_orderedList.front()->getTime();
                                    //        f->frameDataLen = m_frameShift;
                                    //       f->data = new char[f->frameDataLen];
                                    //       memcpy(f->data, m_frame_buf, m_frameShift);


                                    // m_queueMut->lock();
                                    //    m_frameQueue->push(f);
                                    //     m_queueMut->unlock();


                                    frameShift = 0;
                                    System.arraycopy(udpStruct.getVideoData(), headPosition, frameBuf, 0,
                                            udpStruct.getVideoDataLen() - headPosition);
                                    frameShift += (udpStruct.getVideoDataLen() - headPosition);
                                    //   ZeroMemory(m_frame_buf, m_frame_buf_size);
                                    //   m_frameShift = 0;
                                    //   memcpy(m_frame_buf, videoData + headPosition, videoDataLen - headPosition);
                                    //   m_frameShift += (videoDataLen - headPosition);
                                }
                            } else {
                                //not have I frame
                                //write to frame_buf
                                //        Log.i(TAG, "not have I frame:write to frameBuf ");
                                System.arraycopy(udpStruct.getVideoData(), 0, frameBuf, frameShift, udpStruct.getVideoDataLen());
                                frameShift += udpStruct.getVideoDataLen();

                                //  memcpy(m_frame_buf + m_frameShift, videoData, videoDataLen);
                                // m_frameShift += videoDataLen;
                            }
                        }
                    }

                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    private boolean isSpsHead(byte[] frame) {
        return (frame[0] == 0 && frame[1] == 0 && frame[2] == 0 && frame[3] == 1 && frame[4] == 0x67);
    }

    private int findNextFrame(byte[] buf, int len) {
        for (int i = 0; i < len; i++) {
            if (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x68) {
                return i;
            }
        }
        return 0;
    }


    private void initialH264Decode() {

    }

    private void initialH265Decode() {

    }

}
