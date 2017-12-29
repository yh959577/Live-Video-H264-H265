package com.example.hy.liveexampleandroid.LiveSend;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hy.liveexampleandroid.R;
import com.example.hy.liveexampleandroid.ToastUtil;

/**
 * Created by Administrator on 2017/12/29.
 */

public class SendActivity extends AppCompatActivity implements SendView, View.OnClickListener {

    private Button mSendBtn;
    private TextureView mTextureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        mSendBtn = findViewById(R.id.send_live_btn);
        mSendBtn.setOnClickListener(this);
        findViewById(R.id.take_pic_btn).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSettingPopWindow();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void btnTextChange(String changeText) {
        mSendBtn.setText(changeText);
    }

    @Override
    public void toastMessage(String message) {
        ToastUtil.toast(this, message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showSettingPopWindow() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_live_btn:
                break;
            case R.id.take_pic_btn:
                break;
            default:
                break;
        }
    }
}
