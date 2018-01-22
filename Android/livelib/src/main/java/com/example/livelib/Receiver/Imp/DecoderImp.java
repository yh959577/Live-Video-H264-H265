package com.example.livelib.Receiver.Imp;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.example.livelib.Push.Queue.QueueManager;
import com.example.livelib.Receiver.Interface.Decoder;
import com.example.livelib.Util.H264SPSPaser;

import java.util.Arrays;

/**
 * Created by UPC on 2018/1/18.
 */

public class DecoderImp implements Decoder {
    private String MIME_TYPE;
    private Thread mDecodeThread;
    private Thread mJointFrameThread;
    private MediaCodec mMediaCodec = null;
    private MediaFormat mMediaFormat = null;
    private boolean isDecodeRunning;


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

    }


    private class DecodeThread extends Thread {


        @Override
        public void run() {
            while (isDecodeRunning) {

            }


        }

        private void InitialMediaCodec() {

        }
    }

    private class JointFrameThread extends Thread {
        private byte[] frameBuf;
        private int frameShift;

        JointFrameThread() {
            frameBuf = new byte[500 * 1024];
            frameShift = 0;
            //  int width = (H264SPSPaser.ue(sps,34) + 1)*16;
            //  int height = (H264SPSPaser.ue(sps,-1) + 1)*16;
        }


        @Override
        public void run() {
            while (isDecodeRunning) {
                if (ReceiveDataManager.getOrderListSize() > 100) {
                    int headPosition = 0;
                    UdpStruct udpStruct = ReceiveDataManager.getUdpFromOrderList(0);
                    if (udpStruct.getVideoTypeTag() == 'r')
                        headPosition = find_H264_frame_head(udpStruct.getVideoData(), udpStruct.getVideoData().length);
                    else if (udpStruct.getVideoTypeTag() == 'e')
                        headPosition = find_H265_frame_head(udpStruct.getVideoData(), udpStruct.getVideoData().length);
                    else headPosition=-2;

                    if (headPosition != -1)
                    {
                        if (headPosition == 0)
                        {   //I frame in the packet head
                            //send frameBuf,clear frameBuf,write frameBuf
                            if (frameShift > 0)
                            {
                                //	beginToDecode(pFrame_buf, totalFrame);//total Frame: the real len of the data
                                //	mVideoDecoder->StartDecode(frame_buf, frameShift);									  //memset(pFrame_buf, 0, FF_INPUT_BUFFER_PADDING_SIZE);
                                frameData* f = new frameData;
                                f->time = m_orderedList.front()->getTime();
                                f->frameDataLen = m_frameShift;
                                f->data = new char[f->frameDataLen];
                                memcpy(f->data, m_frame_buf, m_frameShift);

                                m_queueMut->lock();
                                m_frameQueue->push(f);
                                m_queueMut->unlock();

                                ZeroMemory(m_frame_buf, m_frame_buf_size);
                                m_frameShift = 0;
                            }
                            System.arraycopy();
                            memcpy(m_frame_buf, videoData, videoDataLen);
                            m_frameShift += videoDataLen;

                        }
                        else
                        {
                            //I frame in the  middle of the packet
                            memcpy(m_frame_buf + m_frameShift, videoData, headPosition);
                            m_frameShift += headPosition;

                            //beginToDecode(pFrame_buf, totalFrame);
                            //	mVideoDecoder->StartDecode(frame_buf, frameShift);
                            frameData *f = new frameData;
                            f->time = m_orderedList.front()->getTime();
                            f->frameDataLen = m_frameShift;
                            f->data = new char[f->frameDataLen];
                            memcpy(f->data, m_frame_buf, m_frameShift);
                            //		lock_guard<mutex> lock(mutt);

                            m_queueMut->lock();
                            m_frameQueue->push(f);
                            m_queueMut->unlock();


                            //memset(frame_buf, 0, FF_INPUT_BUFFER_PADDING_SIZE);
                            ZeroMemory(m_frame_buf, m_frame_buf_size);
                            m_frameShift = 0;
                            memcpy(m_frame_buf, videoData + headPosition, videoDataLen - headPosition);
                            m_frameShift += (videoDataLen - headPosition);
				/*		delete[] videoData;
						videoData = NULL;*/
                        }
                    }
                    else
                    {
                        //not have I frame
                        //write to frame_buf
                        memcpy(m_frame_buf + m_frameShift, videoData, videoDataLen);
                        m_frameShift += videoDataLen;
			/*	delete[] videoData;
				videoData = NULL;*/
                    }


                    delete[] videoData;
                    videoData = NULL;


                }


            }


        }

        int find_H264_frame_head(byte[] buf, int len) {
            int i;
            boolean isMatch = false;
            for (i = 0; i < len; i++) {
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
            for (i = 0; i < len; i++) {
                if ((buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 2
                ) || (buf[i] == 0 && buf[i + 1] == 0 && buf[i + 2] == 0 && buf[i + 3] == 1 && buf[i + 4] == 0x40)) {
                    isMatch = true;
                    break;
                }
            }
            return isMatch ? i : -1;
        }
    }
}
