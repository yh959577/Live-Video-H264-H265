package com.example.hy.liveexampleandroid.Push.Camera;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public class CameraImp implements Camera ,TextureView.SurfaceTextureListener,ImageReader.OnImageAvailableListener{

    private Handler mHandler;
    private CameraDevice.StateCallback mCameraDeviceStateCallback;
    private CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback;
    private CaptureRequest.Builder mPreviewBuilder;
    private Size[] mSupportSize=null;
    private Size mPreviewSettingSize;
    private CameraDevice mCameraDevice;
    private TextureView mTextureView;
    private String[] mCameraList;
    private static String TAG="CameraImp";
    private CameraManager mCameraManager;
    private ImageReader mImageReader;
    private boolean mIsProcessImage;

    public CameraImp(TextureView textureView,CameraManager cameraManager) {
          mCameraManager=cameraManager;
          mTextureView=textureView;
    }

    @Override
    public void initial() {
           initialHandler();
           initialCameraDeviceStateCallback();
           initialCameraCaptureSessionStateCallback();
           mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void setIsProcessImage(boolean isProcessImage) {
        mIsProcessImage=isProcessImage;
    }

    @Override
    public void reOpen() {
         closeCamera();
         initialCamera();
      //   mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void setPreviewSize(Size previewSize) {
            mPreviewSettingSize=previewSize;
            reOpen();
    }


    @Override
    public void closeCamera() {
        mImageReader.close();
        mCameraDevice.close();
       // mTextureView.setSurfaceTextureListener(null);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        initialCamera();
      //  adjustTextureViewSize(width,height);
        Log.i(TAG, "onSurfaceTextureAvailable: ");

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged: ");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureUpdated: ");
    }


    FileOutputStream fileOutputStream;

    @Override
    public void onImageAvailable(ImageReader reader) {
       // Log.i(TAG, "onImageAvailable: ");
        Image image = reader.acquireNextImage();
      //  Log.i(TAG, "YUV_420_888toNV21: width=="+image.getWidth()+" height=="+image.getHeight());
        if (mIsProcessImage) {

            if (QueueManager.getYUVSize()>=30){
                QueueManager.pollDataFromYUVQueue();
            }
            QueueManager.addDataToYUVQueue(convertImgToYUVData(image));
    //        Log.i(TAG, "onImageAvailable: planes=="+Arrays.toString(image.getPlanes()));

      //      Log.i(TAG, "onImageAvailable: push Image to queue");
        }
        image.close();
    }


    private void initialHandler() {
        HandlerThread threadHandler = new HandlerThread("CAMERA2");
        threadHandler.start();
        mHandler = new Handler(threadHandler.getLooper());
    }


    private void initialCameraDeviceStateCallback() {
        mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                try {
                    startPreview(camera);
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
        };
    }


    private void initialCameraCaptureSessionStateCallback() {
        mCameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                try {
                    session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        };
    }

    private void adjustTextureViewSize(int width,int height){
        Log.i(TAG, "onSurfaceTextureAvailable: ");
        double scale=(double) (mPreviewSettingSize.getHeight())/(double) (mPreviewSettingSize.getWidth());
        int suitableWidth=(int)(height*scale);
        ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams(suitableWidth,height);
        layoutParams.topToTop= ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom= ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight=ConstraintLayout.LayoutParams.PARENT_ID;
       
        mTextureView.setLayoutParams(layoutParams);
    }

    private void startPreview(CameraDevice camera) throws CameraAccessException {
        Log.i(TAG, "startPreview: ");
        mCameraDevice=camera;
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight());
        Surface surface = new Surface(texture);
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        mImageReader=ImageReader.newInstance(mPreviewSettingSize.getWidth(), mPreviewSettingSize.getHeight(),
                ImageFormat.YV12,1);
        //the format is wrong when using YUV420_888 ,have no idea about that
        //using YV12 is useful on my phone
        mImageReader.setOnImageAvailableListener(this,mHandler);
        mPreviewBuilder.addTarget(surface);
        mPreviewBuilder.addTarget(mImageReader.getSurface());
        mCameraDevice.createCaptureSession(Arrays.asList(surface,mImageReader.getSurface()),mCameraCaptureSessionStateCallback,mHandler);
    }


    @SuppressLint("MissingPermission")
    private void initialCamera(){
        try {
            assert mCameraManager != null;
            mCameraList = mCameraManager.getCameraIdList();
            CameraCharacteristics cameraCharacteristics=mCameraManager.getCameraCharacteristics(mCameraList[0]);
            StreamConfigurationMap map=cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map!=null;
            mSupportSize =map.getOutputSizes(SurfaceTexture.class);
            if (mPreviewSettingSize ==null)
            mPreviewSettingSize = mSupportSize[0];
            PusherImp.supportSize=mSupportSize.clone();
            Log.i(TAG, "initialCamera: size array=="+ Arrays.toString(mSupportSize));
            mCameraManager.openCamera(mCameraList[0], mCameraDeviceStateCallback, mHandler);
            adjustTextureViewSize(0,mTextureView.getHeight());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private  byte[] convertImgToYUVData(Image image){

        ByteBuffer yBuffer = image. getPlanes()[0].getBuffer(); //Y
        ByteBuffer uBuffer = image. getPlanes()[1]. getBuffer(); //U
        ByteBuffer vBuffer = image. getPlanes()[2]. getBuffer(); //V

        int ySize=yBuffer.remaining();
        int uSize=uBuffer.remaining();
        int vSize=vBuffer.remaining();
        Log.i(TAG, "saveYuv: Ysize==="+ySize+" Usize==="+uSize+" Vsize==="+vSize);

        byte[] data=new byte[ySize+uSize+vSize];

        yBuffer.get(data,0,ySize);
        uBuffer.get(data,ySize,uSize);
        vBuffer.get(data,ySize+uSize,vSize);

        return data;
    }
}
