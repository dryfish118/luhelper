package com.tao.luhelper;

import android.os.Handler;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookMyAccountFragment extends HookBase {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "g", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:MyAccountFragment:Ready", false)) {
                    GlobleUtil.putBoolean("Class:MyAccountFragment:Ready", true);
                    GlobleUtil.log("MyAccountFragment ready now.");

                    final Object o = param.thisObject;
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GlobleUtil.log("Step3: Set the signal to switch to the Finance Fragment");
                            GlobleUtil.putInt("Step", 3);
                        }
                    }, 100);
                }
            }
        });
    }

}
