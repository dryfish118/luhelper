package com.tao.luhelper;

import android.content.Context;
import android.graphics.Point;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Tao on 2017/10/27.
 */

public class ShellUtil {
    private static void execCmd(String cmd) {
        XposedBridge.log("ShellUtil: " + cmd);
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void tap(int x, int y) {
        execCmd("input tap " + x + " " + y);
    }

    public static void swipe(ArrayList<Point> pnts) {
        execCmd("sendevent /dev/input/event5 3 57 500");
        for (int i = 0; i < pnts.size(); i++) {
            execCmd("sendevent /dev/input/event5 3 53 " + pnts.get(i).x);
            execCmd("sendevent /dev/input/event5 3 54 " + pnts.get(i).y);
            if (i == 0) {
                execCmd("sendevent /dev/input/event5 3 58 91");
                execCmd("sendevent /dev/input/event5 3 48 12");
            }
            execCmd("sendevent /dev/input/event5 0 0 0");
        }
        execCmd("sendevent /dev/input/event5 3 57 -1");
        execCmd("sendevent /dev/input/event5 0 0 0");
    }
}
