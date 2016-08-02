/**
 * File Name: Logger.java
 */
package com.leautolink.leautocamera.utils;


import android.text.TextUtils;
import android.util.Log;



import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    private static final int MAX_LOG_LINE_LENGTH = 2048;

    private static final String DEFAULT_TAG = "LeCamera";
    private static boolean sDebuggable = true;
    private static long sTimestamp = 0;

    public static void e(Throwable t) {
        e(DEFAULT_TAG, t);
    }
    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void w(String msg) {
        w(DEFAULT_TAG, msg);
    }

    public static void i(String sTag, String msg) {
        if (sDebuggable) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        Log.i(sTag, msg.substring(start, len));
                        break;
                    } else {
                        Log.i(sTag, msg.substring(start, end));
                    }
                }
            } else {
                Log.i(sTag, msg);
            }
        }
    }

    public static void v(String sTag, String msg) {
        if (sDebuggable) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        Log.v(sTag, msg.substring(start, len));
                        break;
                    } else {
                        Log.v(sTag, msg.substring(start, end));
                    }
                }
            } else {
                Log.v(sTag, msg);
            }
        }
    }

    public static void d(String sTag, String msg) {
        if (sDebuggable) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        Log.d(sTag, msg.substring(start, len));
                        break;
                    } else {
                        Log.d(sTag, msg.substring(start, end));
                    }
                }
            } else {
                Log.d(sTag, msg);
            }
        }
    }

    public static void w(String sTag, String msg) {
        if (sDebuggable) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        Log.w(sTag, msg.substring(start, len));
                        break;
                    } else {
                        Log.w(sTag, msg.substring(start, end));
                    }
                }
            } else {
                Log.w(sTag, msg);
            }
        }
    }

    public static void w(String sTag, Throwable tr) {
        if (sDebuggable) {
            Log.w(sTag, "", tr);
        }
    }

    public static void w(String sTag, String msg, Throwable tr) {
        if (sDebuggable && null != msg) {
            Log.w(sTag, msg, tr);
        }
    }

    public static void e(String sTag, String msg) {
        if (sDebuggable) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        Log.e(sTag, msg.substring(start, len));
                        break;
                    } else {
                        Log.e(sTag, msg.substring(start, end));
                    }
                }
            } else {
                Log.e(sTag, msg);
            }
        }
    }

    public static void e(String sTag, Throwable tr) {
        if (sDebuggable) {
            Log.e(sTag, "", tr);
        }
    }

    public static void e(String sTag, String msg, Throwable tr) {
        if (sDebuggable) {
            Log.e(sTag, msg, tr);
        }
    }

    public static void markStart(String msg) {
        sTimestamp = System.currentTimeMillis();
        if (!TextUtils.isEmpty(msg)) {
            e("[Started|" + sTimestamp + "]" + msg);
        }
    }

    public static void elapsed(String msg) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - sTimestamp;
        sTimestamp = currentTime;
        e("[Elapsed|" + elapsedTime + "]" + msg);
    }

    public static boolean isDebugable() {
        return sDebuggable;
    }

    public static void setDebugable(boolean debugable) {
        sDebuggable = debugable;
    }

    public static void log2File(String log, String path) {
        log2File(log, path, true);
    }

    public static void log2File(String log, String path, boolean append) {
//        if (ENCRYPT_LOG) {
//            log = Base64.encodeToString(log.getBytes());
//        }
//        synchronized (sLogLock) {
//            FileUtils.writeFile(log + "\r\n", path, append);
//        }
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }


}
