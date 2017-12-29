package com.example.hy.liveexampleandroid;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/29.
 */

public class ToastUtil {
    private static long Time=0;

 public static void toast(Context context,String message,int duration){
    if ((System.currentTimeMillis()-Time)>500) {
        Toast.makeText(context, message, duration).show();
    }
     Time=System.currentTimeMillis();
 }



}
