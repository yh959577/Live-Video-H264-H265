package com.example.hy.liveexampleandroid.LivePlayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.widget.Button;
import android.widget.EditText;

import com.example.hy.liveexampleandroid.R;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public class PlayActivity extends AppCompatActivity {
    Button mPlayBtn;
    Button mTakePicBtn;
    EditText mEditText;
    TextureView mTexture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mPlayBtn=findViewById(R.id.play_live_btn);
        mTakePicBtn=findViewById(R.id.take_pic_btn);
        mEditText=findViewById(R.id.receive_IP);
        mTexture=findViewById(R.id.play_texture);
    }
}
