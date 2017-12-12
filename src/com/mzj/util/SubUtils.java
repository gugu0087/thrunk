package com.mzj.util;

import android.util.Log;

/**
 * Created by hasee on 2017/11/30.
 */

public class SubUtils {
    public static String getMd5Str(String url) {
        if (url == null || (!url.contains("?"))) {
            return null;
        }
        String splitStr = url.split("[?]")[1];
        if (splitStr == null || (!splitStr.contains("="))) {
            return null;
        }
        return splitStr.split("=")[1];
    }

    public static boolean isMd5CheckPass(String urlMd5, String filePath) {
        if (urlMd5 == null || filePath == null) {
            return false;
        }
        Log.d("xyc", "isMd5CheckPass: filePath="+filePath);
        return FileMD5Compare.verifyInstallPackage(filePath, urlMd5);
    }
}
