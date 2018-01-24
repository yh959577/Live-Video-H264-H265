package com.example.livelib.Util;

import android.util.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hamik Young on 2018/1/24.
 */

public class SupportSizeUtil {
   private static Size[] usuallySizes=new Size[]{
           new Size(1920,1080),new Size(1440,1080),
           new Size(1280,960),new Size(1280,720),
           new Size(800,600),new Size(800,480),new Size(720,480),
           new Size(480,320),new Size(320,240)};



public static Size[] getOptimisticSizes(Size[] supportSizes){
    List<Size> list=new ArrayList<>();
    for (Size supportSize: supportSizes) {
        for (Size preSetSize: usuallySizes) {
            if (supportSize.equals(preSetSize))
                list.add(supportSize);
        }
    }
    Size[] optimisticSizes=new Size[list.size()];
    for (int i = 0; i <optimisticSizes.length ; i++) {
           optimisticSizes[i]=list.get(i);
    }
    list.clear();
    return optimisticSizes;
}



}
