package com.tao.luhelper;

import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.text.DecimalFormat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookFilterRegionLayout implements IHook {

    @Override
    public void doHook(Class cls) {
        for (final Method method : cls.getDeclaredMethods()) {
            if ("a".equals(method.getName())) {
                TypeVariable tval[] = method.getTypeParameters();
                if ("int".equals(tval[1].toString())) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            if (!GlobleUtil.getBoolean("Class:FilterRegionLayout:Ready", false)) {
                                GlobleUtil.putBoolean("Class:FilterRegionLayout:Ready", true);
                                XposedBridge.log("FilterRegionLayout ready now.");

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

                                ((Button)XposedHelpers.getObjectField(param.thisObject, "e")).callOnClick();

                                XposedBridge.log("Step7: Start querying the project.");
                                GlobleUtil.putInt("Step", 7);
                            }
                        }
                    });
                    break;
                } else {
                    XposedBridge.log("tval[1] is " + tval[1].toString());
                }
            } else {
                XposedBridge.log("method is " + method.getName());
            }
        }
    }

}
