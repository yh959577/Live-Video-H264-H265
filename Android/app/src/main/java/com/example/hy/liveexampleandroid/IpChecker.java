package com.example.hy.liveexampleandroid;

import java.util.regex.Pattern;

/**
 * Created by Hy on 2018/1/2.
 * <p>
 * [1-9]    1-9
 * <p>
 * [1-9]\\d 10-99
 * <p>
 * 1\\d{2}  100-199
 * <p>
 * 2[0-4]\\d 200-249
 * <p>
 * 25[0-5]  250-255
 */

public class IpChecker {
    private static Pattern p = Pattern.compile("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)"
            + ":(\\d|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-6][0-5]{2}[0-3][0-6])$"
    );

    public static boolean IsIpEmpty(String ip) {
        return ip.isEmpty();
    }

    public static boolean IsIpValid(String ip) {
        return p.matcher(ip).matches();
    }


}
