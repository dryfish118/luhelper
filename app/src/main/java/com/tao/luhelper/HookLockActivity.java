package com.tao.luhelper;

import android.graphics.Point;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookLockActivity implements IHook {
    private static final Point[] positions = {
            new Point(340, 890), new Point(690, 890), new Point(1040, 890),
            new Point(340, 1240), new Point(690, 1240), new Point(1040, 1240),
            new Point(340, 1590), new Point(690, 1590), new Point(1040, 1590)
    };

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "d", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                XposedBridge.log("Failed to login by swiping, try again.");
                loginBySwipe(param.thisObject);
            }
        });

        XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:LockActivity:Ready", false)) {
                    GlobleUtil.putBoolean("Class:LockActivity:Ready", true);
                    XposedBridge.log("LockActivity ready now.");

                    String strGesture = GlobleUtil.getString("Gesture", "");
                    if (strGesture.isEmpty()) {
                        loginByInput(param.thisObject);
                    } else {
                        loginBySwipe(param.thisObject);
                    }
                }
            }
        });
    }

    private void loginByInput(Object o) {
        XposedBridge.log("Switch to the Login Activity by username and password.");
        ((View) XposedHelpers.getObjectField(o, "e")).callOnClick();
    }

    private void loginBySwipe(final Object o) {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String strGesture = GlobleUtil.getString("Gesture", "");
                byte[] chs = strGesture.getBytes();
                ArrayList<Point> pnts = new ArrayList<Point>();
                for (int i = 0; i < chs.length; i++) {
                    int pos = (int)(chs[i] - (byte)'1');
                    if (pos >= 0 && pos < 9) {
                        pnts.add(new Point(positions[pos]));
                    }
                }
                XposedBridge.log("Swipe to login.");
                ShellUtil.swipe(pnts);
            }
        }, 1000);
    }

}
