package com.kyty.loadmovie.utils;

import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by Administrator on 2017/9/12.
 */

public class MyUtils {
    public static boolean isMagnet(String url){
        String[] split = url.split(":");
        if (split[split.length-1].length()==40){
            return true;
        }
        return false;
    }
    public static String getClipboardText(Context context){
        ClipboardManager clipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboardManager.getText().toString();
    }
}
