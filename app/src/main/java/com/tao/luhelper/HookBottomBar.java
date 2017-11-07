package com.tao.luhelper;

import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookBottomBar extends HookBase {

    @Override
    public void doHook(final Class cls) {
        for (final Method method : cls.getDeclaredMethods()) {
            if ("setItemsIconResource".equals(method.getName())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        if (!GlobleUtil.getBoolean("Class:BottomBar:Ready", false)) {
                            GlobleUtil.putBoolean("Class:BottomBar:Ready", true);
                            XposedBridge.log("BottomBar ready now.");

                            Handler h = new Handler();
                            h.postDelayed(new TaskDispatch(h, param.thisObject), 1000);
                        }
                    }
                });
                break;
            }
        }
    }

    class TaskDispatch implements Runnable{
        private Handler h;
        private Object o;

        public TaskDispatch(Handler h, Object o) {
            this.h = h;
            this.o = o;
        }

        public void run(){
            int step = GlobleUtil.getInt("Step", 0);
            if (step == 1) {
                XposedBridge.log("Step2: Get the signal to switch to the MyAccount Fragment.");
                GlobleUtil.putInt("Step", 2);

                sendClickMotion(o, 3);
            } else if (step == 3) {
                XposedBridge.log("Step4: Get the signal to switch to the Finance Fragment.");
                GlobleUtil.putInt("Step", 4);

                sendClickMotion(o, 1);
                return;
            }
            h.postDelayed(this, 1000);
        }
    }

    private boolean sendClickMotion(final Object o, int idx) {
        LinearLayout ll1 = (LinearLayout) o;
        LinearLayout ll2 = (LinearLayout) ll1.getChildAt(0);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
        RelativeLayout rl = (RelativeLayout) ll3.getChildAt(idx);
        if (rl.hasOnClickListeners()) {
            try {
                return rl.callOnClick();
            } catch (Exception e) {
                XposedBridge.log(e.toString());
            }
        }
        XposedBridge.log("Failed to click " + rl.toString());
        return false;
    }

}
