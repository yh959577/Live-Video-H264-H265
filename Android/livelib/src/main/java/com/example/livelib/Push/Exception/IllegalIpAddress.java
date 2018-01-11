package com.example.livelib.Push.Exception;

/**
 * Created by Hamik Young on 2018/1/11.
 */

public class IllegalIpAddress extends RuntimeException {
       public IllegalIpAddress(){
           super("Ip address illegal! ");
       }

}
