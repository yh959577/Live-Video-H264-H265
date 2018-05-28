package com.example.livelib.Push.Camera;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;


import com.example.livelib.Push.PusherImp;
import com.example.livelib.Push.Queue.QueueManager;
import com.example.livelib.Util.SupportSizeUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class CameraImp implements Camera, TextureView.SurfaceTextureListener, ImageReader.OnImageAvailableListener {

    private Handler mPreviewHandler;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private Size[] mSupportSize = null;
    private Size mPreviewSettingSize;
    private CameraDevice mCameraDevice;
    private TextureView mTextureView;
    private String[] mCameraList;
    private static String TAG = "CameraImp";
    private CameraManager mCameraManager;
    private ImageReader mImageReader;
    private boolean mIsPreviewSizeChanged = false;
    private static final int CameraBackIndex = 0;
    private static final int CameraFrontIndex = 1;
    private int mCameraIndex;
    private boolean mIsClearQueue;

    public CameraImp(TextureView textureView, CameraManager cameraManager) {
        mCameraManager = cameraManager;
        mTextureView = textureView;
        assert mCameraManager != null;
        try {
            mCameraList = mCameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void initialHandler() {
        HandlerThread previewThreadHandler = new HandlerThread("Preview");
        previewThreadHandler.start();

        HandlerThread imageThreadHandler = new HandlerThread("ImageHandler");
        imageThreadHandler.start();

        mPreviewHandler = new Handler(previewThreadHandler.getLooper());
    }

    @Override
    public void initial() {
        initialHandler();
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void switchCamera() {
        closeCamera();
        if (mCameraIndex == CameraFrontIndex)
            initialCamera(CameraBackIndex);
        else if (mCameraIndex == CameraBackIndex)
            initialCamera(CameraFrontIndex);
        //   mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void setPreviewSize(Size previewSize) {
        mPreviewSettingSize = previewSize;
        mIsPreviewSizeChanged = true;
        try {
            startPreview();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //reOpen();
    }

    @Override
    public void startPush(Size encoderSize) throws CameraAccessException {
     //   mIsProcessImage = true;
        mIsClearQueue=false;
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSettingSize) {
            return;
        }
        closePreviewSession();
       // if (mTexture == null)
          SurfaceTexture  texture = mTextureView.getSurfaceTexture();
    //    if (mPreviewBuilder == null)
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        texture.setDefaultBufferSize(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight());

        List<Surface> surfaces = new ArrayList<>();
        Surface surface = new Surface(texture);
        surfaces.add(surface);
        mPreviewBuilder.addTarget(surface);

        mImageReader = ImageReader.newInstance(encoderSize.getWidth(), encoderSize.getHeight(),
                ImageFormat.YV12, 1);
        //the format is wrong when using YUV420_888 ,have no idea about that
        //using YV12 is useful on my phone
        Surface imageSurface = mImageReader.getSurface();
        surfaces.add(imageSurface);
        mPreviewBuilder.addTarget(imageSurface);


        mImageReader.setOnImageAvailableListener(this, mPreviewHandler);

        mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mPreviewSession = session;
                try {
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mPreviewHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, mPreviewHandler);
    }

    @Override
    public void stopPush() throws CameraAccessException {
      //  ReceiveQueueManager.clearYUVQueue();
        mIsClearQueue=true;
        startPreview();
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    @Override
    public void closeCamera() {
        closePreviewSession();
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mPreviewSettingSize = null;
            mIsPreviewSizeChanged = false;
        }
        QueueManager.clearYUVQueue();

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mCameraDevice == null)
            initialCamera(CameraBackIndex);
        //  adjustTextureViewSize(width,height);
        Log.i(TAG, "onSurfaceTextureAvailable: ");

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Log.i(TAG, "onSurfaceTextureSizeChanged: ");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
   //     Log.i(TAG, "onSurfaceTextureUpdated: ");
    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireNextImage();
        Log.i(TAG, "onImageAvailable: ImageWidth=="+image.getWidth());
        Log.i(TAG, "onImageAvailable: ImageHeigth=="+image.getHeight());

       if (!QueueManager.addDataToYUVQueue(  convertImgToYUVData(image)))
           Log.i(TAG, "queue is full: ");
        image.close();
        if (mIsClearQueue)
            QueueManager.clearYUVQueue();
    }

    private void adjustTextureViewSize(int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable: ");
        double scale = (double) (mPreviewSettingSize.getHeight()) / (double) (mPreviewSettingSize.getWidth());
        int suitableWidth = (int) (height * scale);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(suitableWidth, height);
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;

        mTextureView.setLayoutParams(layoutParams);
    }

    private void startPreview() throws CameraAccessException {
        Log.i(TAG, "startPreview: ");
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSettingSize) {
            return;
        }
        closePreviewSession();
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        texture.setDefaultBufferSize(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight());
        Surface surface = new Surface(texture);
        mPreviewBuilder.addTarget(surface);
        mCameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mPreviewSession = session;
                try {
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mPreviewHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, mPreviewHandler);
        if (mIsPreviewSizeChanged) {
            adjustTextureViewSize(mTextureView.getWidth(), mTextureView.getHeight());
        }
        if (QueueManager.getYUVQueueSize()>0)
            QueueManager.clearYUVQueue();
    }


    @SuppressLint("MissingPermission")
    private void initialCamera(int cameraIndex) {
        try {
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraList[cameraIndex]);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            mSupportSize = SupportSizeUtil.getOptimisticSizes( map.getOutputSizes(SurfaceTexture.class));
            if (mPreviewSettingSize == null)
                mPreviewSettingSize = mSupportSize[0];
            PusherImp.supportSize = mSupportSize.clone();
            Log.i(TAG, "initialCamera: size array==" + Arrays.toString(mSupportSize));
            mCameraManager.openCamera(mCameraList[cameraIndex], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    try {
                        startPreview();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, mPreviewHandler);
            mCameraIndex = cameraIndex;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private byte[] convertImgToYUVData(Image image) {

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); //Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); //U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); //V

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();
        //   Log.i(TAG, "saveYuv: Ysize===" + ySize + " Usize===" + uSize + " Vsize===" + vSize);

        byte[] data = new byte[ySize + uSize + vSize];

        yBuffer.get(data, 0, ySize);
        uBuffer.get(data, ySize, uSize);
        vBuffer.get(data, ySize + uSize, vSize);

        return data;
    }

    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
    {
        byte [] yuv = new byte[imageWidth*imageHeight*3/2];
        // Rotate the Y luma
        int i = 0;
        for(int x = 0;x < imageWidth;x++)
        {
            for(int y = imageHeight-1;y >= 0;y--)
            {
                yuv[i] = data[y*imageWidth+x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth*imageHeight*3/2-1;
        for(int x = imageWidth-1;x > 0;x=x-2)
        {
            for(int y = 0;y < imageHeight/2;y++)
            {
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i--;
                yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i--;
            }
        }
        return yuv;
    }
}
