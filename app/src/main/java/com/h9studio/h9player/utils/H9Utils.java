package com.h9studio.h9player.utils;

/**
 * Created by Hyeongu on 2017-11-02.
 */

public class H9Utils {
    public static String changeTimeToText(int duration) {
        int inputSecond = duration/1000;
        int hour = 60*60;
        int minute = 60;

        int resultHour;
        int resultMinute;
        int remain;
        String result;
        if (inputSecond >= hour) {
            resultHour = inputSecond/hour;
            remain = inputSecond%hour;
            resultMinute = remain/minute;
            remain = remain%minute;
            result = String.format("%02d:%02d:%02d", resultHour, resultMinute,remain);
        } else {
            resultMinute = inputSecond/minute;
            remain = inputSecond%minute;
            result = String.format("%02d:%02d", resultMinute,remain);
        }
        return result;

    }
}
