package com.tao.luhelper;

import android.app.Fragment;
import android.graphics.Point;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Tao on 2017/10/27.
 */

public class ShellUtil {
    private static final Random r = new Random();

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
        if (pnts.size() == 2) {
            execCmd("input swipe " + pnts.get(0).x + " " + pnts.get(0).y + " " + pnts.get(1).x + " " + pnts.get(1).y);
        } else if (pnts.size() >2) {
            execCmd("sendevent /dev/input/event5 3 57 " + r.nextInt(1000));
            for (int i = 0; i < pnts.size(); i++) {
                if (i == 0) {
                    execCmd("sendevent /dev/input/event5 3 53 " + pnts.get(i).x);
                    execCmd("sendevent /dev/input/event5 3 54 " + pnts.get(i).y);
                    execCmd("sendevent /dev/input/event5 3 58 " + (r.nextInt(5) + 88));
                    execCmd("sendevent /dev/input/event5 3 48 " + ((r.nextInt(7) + 3) * 2));
                    execCmd("sendevent /dev/input/event5 0 0 0");
                } else {
                    boolean bSend = false;
                    if (pnts.get(i).x != pnts.get(i - 1).x) {
                        execCmd("sendevent /dev/input/event5 3 53 " + pnts.get(i).x);
                        bSend = true;
                    }
                    if (pnts.get(i).y != pnts.get(i - 1).y) {
                        execCmd("sendevent /dev/input/event5 3 54 " + pnts.get(i).y);
                        bSend = true;
                    }
                    if (bSend) {
                        execCmd("sendevent /dev/input/event5 0 0 0");
                    }
                }
            }
            execCmd("sendevent /dev/input/event5 3 57 -1");
            execCmd("sendevent /dev/input/event5 0 0 0");
        }
    }
}
