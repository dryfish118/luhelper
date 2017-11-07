package com.tao.luhelper;

import android.os.Handler;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookHomeFragment implements IHook {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:HomeFragmentV2:Ready", false)) {
                    GlobleUtil.putBoolean("Class:HomeFragmentV2:Ready", true);
                    XposedBridge.log("HomeFragmentV2 ready now.");

                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            XposedBridge.log("Step1: Set the signal to switch to my account fragment.");
                            GlobleUtil.putInt("Step", 1);
                        }
                    }, 1000);
                }
            }
        });
    }

}
