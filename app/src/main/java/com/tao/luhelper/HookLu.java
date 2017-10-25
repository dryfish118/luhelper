package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Tao on 2017/10/23.
 */

public class HookLu implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

        if (!GlobleUtil.getBoolean("Monitor", false)) {
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

            private void hookAllMethod(final Class<?> cls, final String key) {
                for (Method method: cls.getDeclaredMethods()) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log(key + ": " + param.method.getName());
                        }
                    });
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                try {
                    Class<?> cls = cl.loadClass("com.lufax.android.v2.app.other.ui.fragment.HomeFragment");
                    if (cls != null) {
                        if (!GlobleUtil.getBoolean("HomeFragment", false)) {
                            GlobleUtil.putBoolean("HomeFragment", true);
                            XposedBridge.log("Start hook: HomeFragment");
                            hookHomeFragment(cls);
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    Class<?> cls = cl.loadClass("com.lufax.android.navi.BottomBar");
                    if (cls != null) {
                        if (!GlobleUtil.getBoolean("BottomBar", false)) {
                            GlobleUtil.putBoolean("BottomBar", true);
                            XposedBridge.log("Start hook: BottomBar");
                            hookBottomBar(cls);
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    Class<?> cls =  cl.loadClass("com.lufax.android.gesturelock.LockActivity");
                    if (cls != null) {
                        if (!GlobleUtil.getBoolean("LockActivity", false)) {
                            GlobleUtil.putBoolean("LockActivity", true);
                            XposedBridge.log("Start hook: LockActivity");
                            hookLockActivity(cls);
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    Class<?> cls =  cl.loadClass("com.lufax.android.activity.fragments.LoginFragment");
                    if (cls != null) {
                        if (!GlobleUtil.getBoolean("LoginFragment", false)) {
                            GlobleUtil.putBoolean("LoginFragment", true);
                            XposedBridge.log("Start hook: LoginFragment");
                            hookLoginFragment(cls);
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    Class<?> cls =  cl.loadClass("com.lufax.android.myaccount.ui.MyAccountFragment");
                    if (cls != null) {
                        if (!GlobleUtil.getBoolean("MyAccountFragment", false)) {
                            GlobleUtil.putBoolean("MyAccountFragment", true);
                            XposedBridge.log("Start hook: MyAccountFragment");
                            hookMyAccountFragment(cls);
                        }
                    }
                } catch (Exception e) {
                }
            }

            private void hookHomeFragment(Class<?> cls) {
                try {
                    XposedHelpers.findAndHookMethod(cls, "g", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            if (GlobleUtil.getInt("Step", 0) == 0) {
                                GlobleUtil.putInt("Step", 1);
                                XposedBridge.log("Step: 1");

                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    public void run() {
                                        GlobleUtil.putInt("Step", 2);
                                        XposedBridge.log("Step: 2");
                                    }
                                }, 5000);
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }

            private void hookBottomBar(final Class<?> cls) {
                //hookAllMethod(cls, "BottomBar");
                for (Method method: cls.getDeclaredMethods()) {
                    if ("setItemsIconResource".equals(method.getName())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                XposedBridge.log("Hook: setItemsIconResource");

                                final Object o = param.thisObject;
                                final Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    public void run() {
                                        int step = GlobleUtil.getInt("Step", 0);
                                        if (step == 2) {
                                            GlobleUtil.putInt("Step", 3);
                                            XposedBridge.log("Step: 3");
                                            Timer timer2 = new Timer();
                                            timer2.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    GlobleUtil.putInt("Step", 4);
                                                    XposedBridge.log("Step: 4");
                                                    XposedHelpers.callMethod(o, "a", 3);
                                                }
                                            }, 5000);
                                        }
                                    }
                                }, 1000, 1000);
                            }
                        });
                        break;
                    }
                }
            }

            private void hookLockActivity(Class<?> cls) {
                XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Object v = XposedHelpers.getObjectField(param.thisObject, "e");
                        if (v != null) {
                            XposedBridge.log("Switch to login by username and password");
                            XposedHelpers.callMethod(param.thisObject, "onClick", v);
                        }
                    }
                });
            }

            private void hookLoginFragment(Class<?> cls) {
                XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        XposedBridge.log("LoginFragment launched.");
                        String userName = GlobleUtil.getString("UserName", "");
                        String loginPassword = GlobleUtil.getString("LoginPassword", "");
                        if (!"".equals(userName) && !"".equals(loginPassword)) {
                            Object v = XposedHelpers.getObjectField(param.thisObject, "h");
                            if (v != null) {
                                EditText[] ets = (EditText[]) v;
                                ets[0].setText(userName);
                                ets[1].setText(loginPassword);

                                v = XposedHelpers.getObjectField(param.thisObject, "l");
                                if (v != null) {
                                    XposedBridge.log("Click to login.");
                                    XposedHelpers.callMethod(param.thisObject, "onClick", v);
                                }
                            }
                        }
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
                            GlobleUtil.putBoolean("Monitor", false);
                        }
                    }
                });
            }
        });
    }
}
