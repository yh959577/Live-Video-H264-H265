package com.example.livelib.Push.Util;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Hamik Young on 2018/1/2.
 */

public class PermissionUtil {
   private static ArrayList<String> requestPermission=new ArrayList<>();

  public static void requestPermission(@NonNull AppCompatActivity activity, @NonNull String...permission){

      for (String perPermission: permission) {
          if (ContextCompat.checkSelfPermission(activity,perPermission)!= PackageManager.PERMISSION_GRANTED){
              requestPermission.add(perPermission);
          }
      }
      if (requestPermission.size()>0){
          ActivityCompat.requestPermissions(activity,requestPermission.toArray(new String[requestPermission.size()]),0);
      }
  }

  public static boolean isPermissionGrant(AppCompatActivity activity, @NonNull String permission){
      return ContextCompat.checkSelfPermission(activity,permission)==PackageManager.PERMISSION_GRANTED;
  }
}
