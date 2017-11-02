package com.h9studio.h9player.utils;


import android.util.Log;


public class VLog {
    private static final String TAG = "[H9Player]";

    private static boolean DEBUG_FLAG = true;

    private static boolean ERROR_FLAG = true;

    private static boolean INFO_FLAG = true;

    private static boolean WARNING_FLAG = true;

    private static boolean VERBOS_FLAG = true;


    public VLog() {
    }


    public static void d(String message) {
        if (DEBUG_FLAG) {
            String log = buildLogMsg(message);
            Log.d(TAG, log);
        }
    }


    public static void e(String message) {
        if (ERROR_FLAG) {
            String log = buildLogMsg(message);
            Log.e(TAG, log);
        }
    }


    public static void i(String message) {
        if (INFO_FLAG) {
            String log = buildLogMsg(message);
            Log.i(TAG, log);
        }
    }


    public static void w(String message) {
        if (WARNING_FLAG) {
            String log = buildLogMsg(message);
            Log.w(TAG, log);
        }
    }


    public static void v(String message) {
        if (VERBOS_FLAG) {
            String log = buildLogMsg(message);
            Log.v(TAG, log);
        }
    }


    private static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        sb.append(ste.getFileName());

        sb.append(" > ");

        sb.append(ste.getMethodName());

        sb.append(" > #");

        sb.append(ste.getLineNumber());

        sb.append("] ");

        sb.append(message);


        return sb.toString();

    }

}
