package com.example.hy.liveexampleandroid.LiveSend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hy.liveexampleandroid.PermissionUtil;
import com.example.hy.liveexampleandroid.R;
import com.example.hy.liveexampleandroid.ToastUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/29.
 */

public class SendActivity extends AppCompatActivity implements
        SendView, View.OnClickListener, TextureView.SurfaceTextureListener,ImageReader.OnImageAvailableListener

{

    private Button mSendBtn;
    private TextureView mTextureView;
    private SendPresenter presenter;
    private EditText mEditText;
    private String[] mCameraList;
    private HandlerThread mThreadHandler;
    private Handler mHandler;
    private CameraDevice.StateCallback mCameraDeviceStateCallback;
    private CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback;
    private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback;
    private CaptureRequest.Builder mPreviewBuilder;
    private Size[] mPreviewSupportSize;
    private Size defaultSize;

    private ImageReader mImageReader;

    private static final String TAG="SendActivity";
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        mEditText = findViewById(R.id.push_IP);
        mSendBtn = findViewById(R.id.send_live_btn);
        mTextureView = findViewById(R.id.video_texture);
        mSendBtn.setOnClickListener(this);
        findViewById(R.id.take_pic_btn).setOnClickListener(this);
        initialHandler();
        initialCameraDeviceStateCallback();
        initialCameraCaptureSessionStateCallback();
        initialCameraCaptureSessionCaptureCallback();
        presenter = new SendPresenterImp(this, new SendInteractorImp());
        mTextureView.setSurfaceTextureListener(this);
    }


    private void initialHandler() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
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

    private void startPreview(CameraDevice camera) throws CameraAccessException {
        Log.i(TAG, "startPreview: ");
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(defaultSize.getWidth(), defaultSize.getHeight());

        Surface surface = new Surface(texture);
        mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        mImageReader=ImageReader.newInstance(defaultSize.getWidth(),defaultSize.getHeight(),
                ImageFormat.YUV_420_888,2);
        mImageReader.setOnImageAvailableListener(this,mHandler);
        mPreviewBuilder.addTarget(surface);
        mPreviewBuilder.addTarget(mImageReader.getSurface());
        camera.createCaptureSession(Arrays.asList(surface,mImageReader.getSurface()),mCameraCaptureSessionStateCallback,mHandler);
    }


    private void initialCameraCaptureSessionStateCallback() {
        mCameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                try {
                    session.setRepeatingRequest(mPreviewBuilder.build(),null,mHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        };


    }

    private void initialCameraCaptureSessionCaptureCallback() {
        mCameraCaptureSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
            }

            @Override
            public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                super.onCaptureProgressed(session, request, partialResult);
            }

            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_item:
                showSettingPopWindow();
                break;
            case R.id.switch_camera:
                presenter.switchCamera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void toastMessage(String message) {
        ToastUtil.toast(this, message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showSettingPopWindow() {

    }

    @Override
    public void IpIsEmpty() {
        mEditText.setError(getString(R.string.Ip_Empty_Error));
    }

    @Override
    public void IpError() {
        mEditText.setError(getString(R.string.Ip_Error));
    }

    @Override
    public void btnTextChangeToStart() {
        mSendBtn.setText(R.string.startLive);
        mEditText.setEnabled(true);
    }

    @Override
    public void btnTextChangeToStop() {
        mSendBtn.setText(R.string.stopLive);
        mEditText.setEnabled(false);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void initialCamera() {
        try {
            //获得所有摄像头的管理者CameraManager
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

            assert cameraManager != null;
            mCameraList = cameraManager.getCameraIdList();
            CameraCharacteristics cameraCharacteristics=cameraManager.getCameraCharacteristics(mCameraList[0]);
            StreamConfigurationMap map=cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            assert map!=null;
            mPreviewSupportSize=map.getOutputSizes(SurfaceTexture.class);
            defaultSize=mPreviewSupportSize[2];
            Log.i(TAG, "initialCamera: size array=="+ Arrays.toString(mPreviewSupportSize));
            cameraManager.openCamera(mCameraList[0], mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_live_btn:
                if (mSendBtn.getText().toString().equals(getResources().getString(R.string.startLive))) {
                    presenter.startPushVideo(mEditText.getText().toString());
                } else {
                    presenter.stopPushVideo();
                }
                break;
            case R.id.take_pic_btn:
                presenter.takePic();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        presenter.initialCamera();
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

    private int counts;
    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image=reader.acquireNextImage();
        image.close();
        Log.i(TAG, "onImageAvailable: the count="+counts);
        counts++;
    }
}
