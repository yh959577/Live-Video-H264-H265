package com.example.hy.liveexampleandroid.Push.Camera;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.Rect;
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

import com.example.hy.liveexampleandroid.Push.Queue.QueueManager;

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
    private Size[] mPreviewSupportSize;
    private Size settingSize;
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
    public void onDestroy() {
        mImageReader.close();
        mCameraDevice.close();
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        initialCamera();
        adjustTextureViewSize(width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        Log.i(TAG, "onImageAvailable: ");
        Image image = reader.acquireNextImage();
        Log.i(TAG, "YUV_420_888toNV21: width=="+image.getWidth()+" height=="+image.getHeight());
        if (mIsProcessImage) {
            ByteBuffer buffer=image.getPlanes()[0].getBuffer();
            byte[] imageData=new byte[buffer.remaining()];
            buffer.get(imageData);
            try {
                QueueManager.putDataToImageQueue(imageData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onImageAvailable: planes=="+Arrays.toString(image.getPlanes()));


            Log.i(TAG, "onImageAvailable: push Image to queue");
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
        double scale=(double) (settingSize.getHeight())/(double) (settingSize.getWidth());
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
        texture.setDefaultBufferSize(settingSize.getWidth(), settingSize.getHeight());
        Surface surface = new Surface(texture);
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        mImageReader=ImageReader.newInstance(settingSize.getWidth(), settingSize.getHeight(),
                ImageFormat.YV12,2);
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
            mPreviewSupportSize=map.getOutputSizes(SurfaceTexture.class);
            settingSize =mPreviewSupportSize[2];
            Log.i(TAG, "initialCamera: size array=="+ Arrays.toString(mPreviewSupportSize));
            mCameraManager.openCamera(mCameraList[0], mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


//    private  byte[] imageToByteArray(Image image) {
//        byte[] data=YUV_420_888toNV21(image);
//        return data;
//
////        byte[] data = null;
////        if (image. getFormat() == ImageFormat. JPEG) {
////            Image.Plane[] planes = image. getPlanes();
////            ByteBuffer buffer = planes[0]. getBuffer();
////            data = new byte[buffer.capacity()];
////            buffer.get(data);
////            return data;
////        } else if (image. getFormat() == ImageFormat. YUV_420_888) {
////            data = NV21toJPEG(
////                    YUV_420_888toNV21(image),
////                    image. getWidth(), image. getHeight());
////        }
////        return data;
//    }

    private  byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;

        ByteBuffer yBuffer = image. getPlanes()[0]. getBuffer();
        ByteBuffer uBuffer = image. getPlanes()[1]. getBuffer();
        ByteBuffer vBuffer = image. getPlanes()[2]. getBuffer();

        int ySize = yBuffer. remaining();
        int uSize = uBuffer. remaining();
        int vSize = vBuffer. remaining();

        nv21 = new byte[ySize + uSize + vSize];

//U and V are swapped
        yBuffer. get(nv21, 0, ySize);
        vBuffer. get(nv21, ySize, vSize);
        uBuffer. get(nv21, ySize + vSize, uSize);

        return nv21;
    }


//    private byte[] getDataFromYUVImage(Image image){
//        Rect crop=image.getCropRect();
//        int format=image.getFormat();
//        int width=crop.width();
//        int height=crop.height();
//        Image.Plane[] planes=image.getPlanes();
//        byte[] data=new byte[width*height*ImageFormat.getBitsPerPixel(format)/8];
//        byte[] rowData = new byte[planes[0].getRowStride()];
//        int channelOffset = 0;
//        int outputStride = 1;
//        for (int i = 0; i <planes.length ; i++) {
//            switch (i){
//                case 0:
//                    channelOffset=0;
//                    outputStride=1;
//                    break;
//                case 1:
//                    channelOffset = width * height + 1;
//                    outputStride = 2;
//                    break;
//                case 2:
//                    channelOffset = width * height;
//                    outputStride = 2;
//                    break;
//            }
//            ByteBuffer buffer = planes[i].getBuffer();
//            int rowStride = planes[i].getRowStride();
//            int pixelStride = planes[i].getPixelStride();
//
//            int shift = (i == 0) ? 0 : 1;
//            int w = width >> shift;
//            int h = height >> shift;
//            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
//            for (int row = 0; row < h; row++) {
//                int length;
//                if (pixelStride == 1 && outputStride == 1) {
//                    length = w;
//                    buffer.get(data, channelOffset, length);
//                    channelOffset += length;
//                } else {
//                    length = (w - 1) * pixelStride + 1;
//                    buffer.get(rowData, 0, length);
//                    for (int col = 0; col < w; col++) {
//                        data[channelOffset] = rowData[col * pixelStride];
//                        channelOffset += outputStride;
//                    }
//                }
//                if (row < h - 1) {
//                    buffer.position(buffer.position() + rowStride - length);
//                }
//            }
//        }
//        return data;
//    }

//
//    private static byte[] getDataFromImage(Image image, int colorFormat) {
//        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
//            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
//        }
//        if (!isImageFormatSupported(image)) {
//            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
//        }
//        Rect crop = image.getCropRect();
//        int format = image.getFormat();
//        int width = crop.width();
//        int height = crop.height();
//        Image.Plane[] planes = image.getPlanes();
//        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
//        byte[] rowData = new byte[planes[0].getRowStride()];
//        if (VERBOSE) Log.v(TAG, "get data from " + planes.length + " planes");
//        int channelOffset = 0;
//        int outputStride = 1;
//        for (int i = 0; i < planes.length; i++) {
//            switch (i) {
//                case 0:
//                    channelOffset = 0;
//                    outputStride = 1;
//                    break;
//                case 1:
//                    if (colorFormat == COLOR_FormatI420) {
//                        channelOffset = width * height;
//                        outputStride = 1;
//                    } else if (colorFormat == COLOR_FormatNV21) {
//                        channelOffset = width * height + 1;
//                        outputStride = 2;
//                    }
//                    break;
//                case 2:
//                    if (colorFormat == COLOR_FormatI420) {
//                        channelOffset = (int) (width * height * 1.25);
//                        outputStride = 1;
//                    } else if (colorFormat == COLOR_FormatNV21) {
//                        channelOffset = width * height;
//                        outputStride = 2;
//                    }
//                    break;
//            }
//            ByteBuffer buffer = planes[i].getBuffer();
//            int rowStride = planes[i].getRowStride();
//            int pixelStride = planes[i].getPixelStride();
//            if (VERBOSE) {
//                Log.v(TAG, "pixelStride " + pixelStride);
//                Log.v(TAG, "rowStride " + rowStride);
//                Log.v(TAG, "width " + width);
//                Log.v(TAG, "height " + height);
//                Log.v(TAG, "buffer size " + buffer.remaining());
//            }
//            int shift = (i == 0) ? 0 : 1;
//            int w = width >> shift;
//            int h = height >> shift;
//            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
//            for (int row = 0; row < h; row++) {
//                int length;
//                if (pixelStride == 1 && outputStride == 1) {
//                    length = w;
//                    buffer.get(data, channelOffset, length);
//                    channelOffset += length;
//                } else {
//                    length = (w - 1) * pixelStride + 1;
//                    buffer.get(rowData, 0, length);
//                    for (int col = 0; col < w; col++) {
//                        data[channelOffset] = rowData[col * pixelStride];
//                        channelOffset += outputStride;
//                    }
//                }
//                if (row < h - 1) {
//                    buffer.position(buffer.position() + rowStride - length);
//                }
//            }
//            if (VERBOSE) Log.v(TAG, "Finished reading data from plane " + i);
//        }
//        return data;
//    }
}
