package com.example.livelib.Receiver.Imp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hamik Young on 2018/1/22.
 */

public class ReceiveDataManager {
    private static List<UdpStruct> udpStructList = new LinkedList<>();
    private static

   static void addDataToOrderList(int index,UdpStruct udpStruct){
        udpStructList.add(index,udpStruct);
    }

    static void removeDataFromOrderList(int index){
        udpStructList.remove(index);
    }

   static UdpStruct getUdpFromOrderList(int index){
       return udpStructList.get(index);
    }

    static UdpStruct getUdpFromOrderList(){
       return udpStructList.get
    }

    static int getOrderListSize(){
        return udpStructList.size();
    }
    static void clearOrderList(){
        udpStructList.clear();
    }


}
