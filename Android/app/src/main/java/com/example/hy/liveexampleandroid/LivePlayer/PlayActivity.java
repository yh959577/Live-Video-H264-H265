package com.example.hy.liveexampleandroid.LivePlayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hy.liveexampleandroid.R;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public class PlayActivity extends AppCompatActivity implements PlayView, View.OnClickListener {
    Button mPlayBtn;
    Button mTakePicBtn;
    EditText mEditText;
    TextureView mTextureView;
    PlayPresenter mPlayPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mPlayBtn = findViewById(R.id.play_live_btn);
        mTakePicBtn = findViewById(R.id.take_pic_btn);
        mEditText = findViewById(R.id.receive_IP);
        mTextureView = findViewById(R.id.play_texture);
        mPlayBtn.setOnClickListener(this);
        mTakePicBtn.setOnClickListener(this);
        mPlayPresenter = new PlayPresenterImp(this);
    }

    @Override
    public void btnTextChangeToStart() {

    }

    @Override
    public void btnTextChangeToStop() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void stopProgress() {

    }

    @Override
    public boolean IpIsValid() {
        return false;
    }

    @Override
    public boolean IpIsEmpty() {
        return false;
    }

    @Override
    public void showIpEmptyError() {

    }

    @Override
    public void showIpInvalidError() {

    }

    @Override
    public TextureView supplyTextureView() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_live_btn:
                if (mPlayBtn.getText().toString().equals(getResources().getString(R.string.LivePlay)))
                    mPlayPresenter.startPlay();
                else if (mPlayBtn.getText().toString().equals(getResources().getString(R.string.LiveStop)))
                    mPlayPresenter.stopPlay();
                break;
        }


    }
}
