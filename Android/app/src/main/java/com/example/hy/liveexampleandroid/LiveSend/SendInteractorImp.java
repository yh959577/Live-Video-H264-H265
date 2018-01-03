package com.example.hy.liveexampleandroid.LiveSend;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.widget.Toast;

import com.example.hy.liveexampleandroid.PermissionUtil;
import com.example.hy.liveexampleandroid.ToastUtil;

/**
 * Created by Administrator on 2018/1/2.
 */

public class SendInteractorImp implements SendInteractor {

  //  private AppCompatActivity mActivity;
    private CameraManager mCameraManager;
    private String[] cameraList;
    private CameraDevice.StateCallback mCameraDeviceStateCallback;
    private CameraCaptureSession.StateCallback mSessionStateCallback;
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback;

    public SendInteractorImp() {

    }
}
