package com.example.hy.liveexampleandroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/2.
 */

public class PermissionUtil {
   private static ArrayList<String> requestPermission=new ArrayList<>();

  public static void checkPermission(@NonNull AppCompatActivity activity, @NonNull String...permission){
      for (String perPermission: permission) {
          if (ContextCompat.checkSelfPermission(activity,perPermission)!= PackageManager.PERMISSION_GRANTED){
              requestPermission.add(perPermission);
          }
      }
      if (requestPermission.size()>0){
          ActivityCompat.requestPermissions(activity,requestPermission.toArray(new String[requestPermission.size()]),0);
      }
  }




}
