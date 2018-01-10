package com.example.hy.liveexampleandroid.Push.Camera;

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

import com.example.hy.liveexampleandroid.Push.PusherImp;
import com.example.hy.liveexampleandroid.Push.Queue.QueueManager;

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
    private Handler mImageHandler;
  //  private CameraDevice.StateCallback mCameraDeviceStateCallback;
  //  private CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback;
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
    private boolean mIsProcessImage=false;
    private SurfaceTexture mTexture;
    private boolean mIsPreviewSizeChanged =false;
    private static final  int CameraBackIndex=0;
    private static final  int CameraFrontIndex=1;
    private int mCameraIndex;

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

        HandlerThread imageThreadHandler=new HandlerThread("ImageHandler");
        imageThreadHandler.start();

        mPreviewHandler = new Handler(previewThreadHandler.getLooper());
        mImageHandler=new Handler(imageThreadHandler.getLooper());
    }

    @Override
    public void initial() {
        initialHandler();
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void setIsProcessImage(boolean isProcessImage) {
        mIsProcessImage = isProcessImage;
    }

    @Override
    public void switchCamera() {
        closeCamera();
        if (mCameraIndex==CameraFrontIndex)
        initialCamera(CameraBackIndex);
        else if (mCameraIndex==CameraBackIndex)
            initialCamera(CameraFrontIndex);
        //   mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void setPreviewSize(Size previewSize) {
        mPreviewSettingSize = previewSize;
        mIsPreviewSizeChanged =true;
        try {
            startPreview();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //reOpen();
    }

    @Override
    public void startPush(Size encoderSize) throws CameraAccessException {
        mIsProcessImage=true;
          closePreviewSession();
        if (mTexture==null)
            mTexture = mTextureView.getSurfaceTexture();
        if (mPreviewBuilder==null)
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        mTexture.setDefaultBufferSize(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight());

        List<Surface> surfaces = new ArrayList<>();
        Surface surface = new Surface(mTexture);
        surfaces.add(surface);
        mPreviewBuilder.addTarget(surface);

        mImageReader=ImageReader.newInstance(encoderSize.getWidth(), encoderSize.getHeight(),
                  ImageFormat.YV12,1);
        //the format is wrong when using YUV420_888 ,have no idea about that
        //using YV12 is useful on my phone
        Surface imageSurface=mImageReader.getSurface();
        surfaces.add(imageSurface);
        mPreviewBuilder.addTarget(imageSurface);


        mImageReader.setOnImageAvailableListener(this, mPreviewHandler);

        mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mPreviewSession=session;
                try {
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(),null, mImageHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, mImageHandler);
    }

    @Override
    public void stopPush() throws CameraAccessException {
        //startPreview();
         mIsProcessImage=false;
         QueueManager.clearYUVQueue();
         QueueManager.clearFrameQueue();
    }

    private void closePreviewSession(){
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    @Override
    public void closeCamera() {
      //  mImageReader.close();
      if (mCameraDevice!=null) {
          mCameraDevice.close();
          mPreviewSettingSize=null;
          mIsPreviewSizeChanged =false;
      }
      QueueManager.clearYUVQueue();
      QueueManager.clearFrameQueue();
        // mTextureView.setSurfaceTextureListener(null);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mCameraDevice==null)
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
        Log.i(TAG, "onSurfaceTextureUpdated: ");
    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        // Log.i(TAG, "onImageAvailable: ");
        Image image = reader.acquireNextImage();

if (mIsProcessImage) {
    Log.i(TAG, "YUV_420_888toNV21: width=="+image.getWidth()+" height=="+image.getHeight());
    if (QueueManager.getYUVQueueSize() >= 30) {
        QueueManager.pollDataFromYUVQueue();
    }
    QueueManager.addDataToYUVQueue(convertImgToYUVData(image));
    //        Log.i(TAG, "onImageAvailable: planes=="+Arrays.toString(image.getPlanes()));

    //      Log.i(TAG, "onImageAvailable: push Image to queue");
}
        image.close();
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
        closePreviewSession();
       if (mTexture==null)
        mTexture = mTextureView.getSurfaceTexture();
      if (mPreviewBuilder==null)
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        mTexture.setDefaultBufferSize(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight());
        Surface surface = new Surface(mTexture);
        //   mImageReader=ImageReader.newInstance(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight(),
        //          ImageFormat.YV12,1);
        //the format is wrong when using YUV420_888 ,have no idea about that
        //using YV12 is useful on my phone
        //    mImageReader.setOnImageAvailableListener(this,mPreviewHandler);
        mPreviewBuilder.addTarget(surface);
        //    mPreviewBuilder.addTarget(mImageReader.getSurface());
        mCameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mPreviewSession=session;
                try {
                    mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(),null, mPreviewHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        }, mPreviewHandler);
        if (mIsPreviewSizeChanged){
            adjustTextureViewSize(mTextureView.getWidth(),mTextureView.getHeight());
        }
    }


    @SuppressLint("MissingPermission")
    private void initialCamera(int cameraIndex) {
        try {
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraList[cameraIndex]);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            mSupportSize = map.getOutputSizes(SurfaceTexture.class);
            if (mPreviewSettingSize == null)
                mPreviewSettingSize = mSupportSize[0];
            PusherImp.supportSize = mSupportSize.clone();
            Log.i(TAG, "initialCamera: size array==" + Arrays.toString(mSupportSize));
            mCameraManager.openCamera(mCameraList[cameraIndex], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice=camera;
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
            mCameraIndex=cameraIndex;
            //     adjustTextureViewSize(0,mTextureView.getHeight());
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
}
