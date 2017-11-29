package com.tao.luhelper;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookFilterRegionLayout extends HookBase {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "onClick", View.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                GlobleUtil.log("Step7: Set the filter.");
                GlobleUtil.putInt("Step", 7);
            }
        });

        for (int i = 0; i < cls.getDeclaredMethods().length; i++) {
            final Method method = cls.getDeclaredMethods()[i];
            if ("a".equals(method.getName())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        if (!GlobleUtil.getBoolean("Class:FilterRegionLayout:Ready", false)) {
                            GlobleUtil.putBoolean("Class:FilterRegionLayout:Ready", true);
                            GlobleUtil.log("FilterRegionLayout ready now.");

                            float minMoney = GlobleUtil.getFloat("MinMoney", 0);
                            float maxMoney = GlobleUtil.getFloat("MaxMoney", 0);
                            if (minMoney != 0 || maxMoney != 0) {
                                DecimalFormat df = new DecimalFormat();
                                df.applyPattern("0.0");
                                if (minMoney != 0) {
                                    EditText etA = (EditText) XposedHelpers.getObjectField(param.thisObject, "a");
                                    etA.setText(df.format(minMoney / 10000));
                                }
                                if (maxMoney != 0) {
                                    EditText etB = (EditText) XposedHelpers.getObjectField(param.thisObject, "b");
                                    etB.setText(df.format(maxMoney / 10000));
                                }
                            }

                            final Object o = param.thisObject;
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Object e = XposedHelpers.getObjectField(o, "e");
                                    XposedHelpers.callMethod(o, "onClick", e);
                                }
                            }, 100);
                        }
                    }
                });
                break;
            }
        }
    }

}
