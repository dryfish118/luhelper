package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import org.json.JSONException;
import org.json.JSONObject;

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

            private void hookSingleMethod(Class<?> c, final String name) {
                XposedHelpers.findAndHookMethod(c, name, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("before " + name);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("after " + name);
                    }
                });
            }

            private void printTextView(Object o, String name) {
                Object v = XposedHelpers.getObjectField(o, name);
                if (v != null) {
                    XposedBridge.log(name + ": " + ((TextView)v).getText().toString());
                } else {
                    XposedBridge.log("Failed to getField(TextView) \"" + name + "\"");
                }
            }

            private void printString(Object o, String name) {
                Object v = XposedHelpers.getObjectField(o, name);
                if (v != null) {
                    XposedBridge.log(name + ": " + (String)v);
                } else {
                    XposedBridge.log("Failed to getField(String) \"" + name + "\"");
                }
            }

            private void printBoolean(Object o, String name) {
                Object v = XposedHelpers.getObjectField(o, name);
                if (v != null) {
                    XposedBridge.log(name + ": " + (boolean)v);
                } else {
                    XposedBridge.log("Failed to getField(boolean) \"" + name + "\"");
                }
            }

            private void printLong(Object o, String name) {
                Object v = XposedHelpers.getObjectField(o, name);
                if (v != null) {
                    XposedBridge.log(name + ": " + (long)v);
                } else {
                    XposedBridge.log("Failed to getField(long) \"" + name + "\"");
                }
            }

            private void printInt(Object o, String name) {
                Object v = XposedHelpers.getObjectField(o, name);
                if (v != null) {
                    XposedBridge.log(name + ": " + (int)v);
                } else {
                    XposedBridge.log("Failed to getField(int) \"" + name + "\"");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                try {
                    Class<?> cls = cl.loadClass("com.lufax.android.v2.app.other.ui.fragment.HomeFragment");
                    if (cls != null) {
                        hookHomeFragment(cls);
                    }
                } catch (Exception e) {
                }

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

            private void hookHomeFragment(Class<?> cls) {
                XposedHelpers.findAndHookMethod(cls, "g", new XC_MethodHook() {
                    private FrameLayout findSubFrameLayout(ViewGroup vg) {
                        for (int i = 0; i < vg.getChildCount(); i++) {
                            View v = vg.getChildAt(i);
                            if (v instanceof FrameLayout) {
                                return (FrameLayout)v;
                            }
                        }
                        return null;
                    }

                    private LinearLayout findSubLinearLayout(ViewGroup vg) {
                        for (int i = 0; i < vg.getChildCount(); i++) {
                            View v = vg.getChildAt(i);
                            if (v instanceof LinearLayout) {
                                return (LinearLayout)v;
                            }
                        }
                        return null;
                    }

                    private RelativeLayout findSubRelativeLayout(ViewGroup vg) {
                        return findSubRelativeLayout(vg, 1);
                    }

                    private RelativeLayout findSubRelativeLayout(ViewGroup vg, int times) {
                        int t = 0;
                        for (int i = 0; i < vg.getChildCount(); i++) {
                            View v = vg.getChildAt(i);
                            if (v instanceof RelativeLayout) {
                                if (++t == times) {
                                    return (RelativeLayout) v;
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        LinearLayout ll = (LinearLayout)XposedHelpers.getObjectField(param.thisObject, "G");
                        if (ll == null) {
                            XposedBridge.log("failed to get field \"G\"");
                            return;
                        }
                        FrameLayout fl = findSubFrameLayout(ll);
                        if (fl == null) {
                            return;
                        }
                        ll = findSubLinearLayout(fl);
                        if (ll == null) {
                            return;
                        }
                        fl = findSubFrameLayout(ll);
                        if (fl == null) {
                            return;
                        }
                        RelativeLayout rl = findSubRelativeLayout(fl);
                        if (rl == null) {
                            return;
                        }
                        ll = findSubLinearLayout(rl);
                        if (ll == null) {
                            return;
                        }
                        ll = findSubLinearLayout(ll);
                        if (ll == null) {
                            return;
                        }
                        rl = findSubRelativeLayout(ll);
                        if (rl == null) {
                            return;
                        }
                        rl = findSubRelativeLayout(rl, 3);
                        if (rl == null) {
                            return;
                        }
                        ll = findSubLinearLayout(rl);
                        if (ll == null) {
                            return;
                        }
                        rl = findSubRelativeLayout(ll);
                        if (rl == null) {
                            return;
                        }

//                        printBoolean(param.thisObject, "A");
//                        printInt(param.thisObject, "D");
//                        printBoolean(param.thisObject, "F");
//                        printInt(param.thisObject, "b");
//                        printInt(param.thisObject, "c");

//                        TextView v = (TextView) XposedHelpers.getObjectField(param.thisObject, "y");
//                        if (v != null) {
//                            XposedBridge.log("可用金额： " + v.getText().toString());
//                        }
                    }
                });
            }

            private void hookMyAccountFragment(Class<?> cls) {
                XposedHelpers.findAndHookMethod(cls, "h", new XC_MethodHook() {
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
