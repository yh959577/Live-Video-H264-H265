package com.example.livelib.Util;

/**
 * Created by Hamik Young on 2018/1/22.
 */

import android.util.Log;

/*
 * Author:Vincent Luo
 * Date: 20150615
 * Description:参考H264标准语法实现对SPS参数的解析
 */
public class H264SPSPaser {
    private static final String TAG = "H264SPSPaser";
    private static int startBit = 0;
    /*
     * 从数据流data中第StartBit位开始读，读bitCnt位，以无符号整形返回
     */
    public static short u(byte[] data,int bitCnt,int StartBit){
        short ret = 0;
        int start = StartBit;
        for(int i = 0;i < bitCnt;i++){
            ret<<=1;
            if ((data[start / 8] & (0x80 >> (start%8))) != 0)
            {
                ret += 1;
            }
            start++;
        }
        return ret;
    }
    /*
     * 无符号指数哥伦布编码
     * leadingZeroBits = −1;
     * for( b = 0; !b; leadingZeroBits++ )
     *    b = read_bits( 1 )
     * 变量codeNum 按照如下方式赋值：
     * codeNum = 2^leadingZeroBits − 1 + read_bits( leadingZeroBits )
     * 这里read_bits( leadingZeroBits )的返回值使用高位在先的二进制无符号整数表示。
     */
    public static short ue(byte[] data,int StartBit){
        short ret = 0;
        int leadingZeroBits = -1;
        int tempStartBit = (StartBit == -1)?startBit:StartBit;//如果传入-1，那么就用上次记录的静态变量
        for( int b = 0; b != 1; leadingZeroBits++ ){//读到第一个不为0的数，计算前面0的个数
            b = u(data,1,tempStartBit++);
        }
        Log.d(TAG,"ue leadingZeroBits = " + leadingZeroBits + ",Math.pow(2, leadingZeroBits) = " + Math.pow(2, leadingZeroBits) + ",tempStartBit = " + tempStartBit);
        ret = (short) (Math.pow(2, leadingZeroBits) - 1 + u(data,leadingZeroBits,tempStartBit));
        startBit = tempStartBit + leadingZeroBits;
        Log.d(TAG,"ue startBit = " + startBit);
        return ret;
    }
    /*
     * 有符号指数哥伦布编码
     * 9.1.1 有符号指数哥伦布编码的映射过程
     *按照9.1节规定，本过程的输入是codeNum。
     *本过程的输出是se(v)的值。
     *表9-3中给出了分配给codeNum的语法元素值的规则，语法元素值按照绝对值的升序排列，负值按照其绝对
     *值参与排列，但列在绝对值相等的正值之后。
     *表 9-3－有符号指数哥伦布编码语法元素se(v)值与codeNum的对应
     *codeNum 语法元素值
     *  0       0
     *  1       1
     *  2       −1
     *  3       2
     *  4       −2
     *  5       3
     *  6       −3
     *  k       (−1)^(k+1) Ceil( k÷2 )
     */
    public static int se(byte[] data,int StartBit){
        int ret = 0;
        short codeNum = ue(data,StartBit);
        ret = (int) (Math.pow(-1, codeNum + 1)*Math.ceil(codeNum/2));
        return ret;
    }
}