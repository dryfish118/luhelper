package com.tao.luhelper;

import java.io.DataOutputStream;
import java.io.OutputStream;

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
}
