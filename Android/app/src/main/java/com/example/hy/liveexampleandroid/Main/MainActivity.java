package com.example.hy.liveexampleandroid.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hy.liveexampleandroid.LivePlayer.PlayActivity;
import com.example.hy.liveexampleandroid.LiveSend.SendActivity;
import com.example.hy.liveexampleandroid.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sendButton).setOnClickListener(this);
        findViewById(R.id.playButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
     switch (view.getId()){
         case R.id.sendButton:
             startActivity(new Intent(this, SendActivity.class));
             break;
         case R.id.playButton:
             startActivity(new Intent(this, PlayActivity.class));
             break;
             default:
                 break;
     }
    }
}
