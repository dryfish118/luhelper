package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Tao on 2017/10/23.
 */

public class HookLuFragment implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

        final XSharedPreferences xsp = new XSharedPreferences(BuildConfig.APPLICATION_ID, "CONFIG");
        xsp.makeWorldReadable();
        if (!xsp.getBoolean("Monitor", false)) {
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                try {
                    Class<?> cls = cl.loadClass("com.lufax.android.myaccount.ui.MyAccountFragment");
                    if (cls != null) {
                        hookMyAccountFragment(cls);
                    }
                } catch (Exception e) {
                }
                try {
                    Class<?> cls = cl.loadClass("com.lufax.android.activity.fragments.LoginFragment");
                    if (cls != null) {
                        hookLoginFragment(cls);
                    }
                } catch (Exception e) {
                }
            }

            private void hookMyAccountFragment(Class<?> cls) {
                XposedHelpers.findAndHookMethod(cls,"h",new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        TextView v = (TextView) XposedHelpers.getObjectField(param.thisObject, "y");
                        if (v != null) {
                            XposedBridge.log("可用金额： " + v.getText().toString());
                        }
                    }
                });
            }

            private void hookLoginFragment(Class<?> cls) {
                XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object v = XposedHelpers.getObjectField(param.thisObject, "h");
                        if (v != null) {
                            xsp.reload();
                            EditText[] ets = (EditText[])v;
                            ets[0].setText(xsp.getString("UserName", ""));
                            ets[1].setText(xsp.getString("LoginPassword", ""));

                            v = XposedHelpers.getObjectField(param.thisObject, "l");
                            if (v != null) {
                                XposedHelpers.callMethod(param.thisObject, "onClick", v);
                            }
                        }
                    }
                });
            }
        });
    }
}
