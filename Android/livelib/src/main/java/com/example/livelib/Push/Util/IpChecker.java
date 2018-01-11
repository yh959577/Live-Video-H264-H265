package com.example.livelib.Push.Util;

import java.util.regex.Pattern;

/**
 * Created by Hy on 2018/1/2.
 * <patternFormatOne>
 * [1-9]    1-9
 * <patternFormatOne>
 * [1-9]\\d 10-99
 * <patternFormatOne>
 * 1\\d{2}  100-199
 * <patternFormatOne>
 * 2[0-4]\\d 200-249
 * <patternFormatOne>
 * 25[0-5]  250-255
 */

public class IpChecker {
    private static Pattern patternFormatOne = Pattern.compile("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)"
            + ":(\\d|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-6][0-5]{2}[0-3][0-6])$"
    );
    private static Pattern patternFormatTwo=Pattern.compile("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)\\."
            + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|)$");

    public static boolean IsIpEmpty(String ip) {
        return ip.isEmpty();
    }

    public static boolean IsIpValid(String ip) {
        return patternFormatOne.matcher(ip).matches()||patternFormatTwo.matcher(ip).matches();
    }


}
