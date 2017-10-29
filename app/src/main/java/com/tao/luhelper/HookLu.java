package com.tao.luhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
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
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                hook(cl, "com.lufax.android.v2.app.other.ui.fragment.HomeFragment", "HomeFragment", new HookHomeFragment());
                hook(cl, "com.lufax.android.navi.BottomBar", "BottomBar", new HookBottomBar());
                hook(cl, "com.lufax.android.gesturelock.LockActivity", "LockActivity", new HookLockActivity());
                hook(cl, "com.lufax.android.activity.fragments.LoginFragment", "LoginFragment", new HookLoginFragment());
                hook(cl, "com.lufax.android.myaccount.ui.MyAccountFragment", "MyAccountFragment", new HookMyAccountFragment());
                hook(cl, "com.lufax.android.v2.app.finance.ui.fragment.FinanceFragment", "FinanceFragment", new HookFinanceFragment());
            }
        });
    }

    interface IHook {
        void doHook(final Class cls);
    }

    private void hook(ClassLoader cl, String clsName, String key, IHook pHook) {
        try {
            Class<?> cls = cl.loadClass(clsName);
            if (cls != null) {
                if (!GlobleUtil.getBoolean(key, false)) {
                    GlobleUtil.putBoolean(key, true);
                    XposedBridge.log("Start hook: " + key);
                    pHook.doHook(cls);
                }
            }
        } catch (Exception e) {
        }
    }

    class HookHomeFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("HomeFragment ready now.");

                    if (GlobleUtil.getInt("Step", 0) == 0) {
                        GlobleUtil.putInt("Step", 1);
                        XposedBridge.log("Step: 1");

                        (new Timer()).schedule(new TimerTask() {
                            public void run() {
                                GlobleUtil.putInt("Step", 2);
                                XposedBridge.log("Step: 2");
                            }
                        }, 3000);
                    }
                }
            });
        }
    }

    class HookBottomBar implements IHook {
        @Override
        public void doHook(final Class cls) {
            for (final Method method : cls.getDeclaredMethods()) {
                if ("setItemsIconResource".equals(method.getName())) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("BottomBar ready now.");

                            (new Timer()).schedule(new FlowTask(param.thisObject), 1000, 1000);
                        }
                    });
                }
            }
        }

        class FlowTask extends java.util.TimerTask{
            private Object o;
            public FlowTask(Object o) {
                this.o = o;
            }

            public void run(){
                int step = GlobleUtil.getInt("Step", 0);
                if (step == 2) {
                    GlobleUtil.putInt("Step", 3);
                    XposedBridge.log("Step: 3");

                    (new Timer()).schedule(new Step3Task(o), 1000);
                } else if (step == 5) {
                    GlobleUtil.putInt("Step", 6);
                    XposedBridge.log("Step: 6");

                    XposedBridge.log("Switch page to the FinanceHome Fragment");
                    sendClickMotion(o, 1);
                }
            }
        }

        class Step3Task extends java.util.TimerTask{
            private Object o;
            public Step3Task(Object o) {
                this.o = o;
            }

            public void run(){
                GlobleUtil.putInt("Step", 4);
                XposedBridge.log("Step: 4");

                XposedBridge.log("Switch page to the MyAccount Fragment");
                sendClickMotion(o, 3);
            }
        }

        private void sendClickMotion(final Object o, int idx) {
            LinearLayout ll1 = (LinearLayout) o;
            LinearLayout ll2 = (LinearLayout) ll1.getChildAt(0);
            LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
            RelativeLayout rl = (RelativeLayout) ll3.getChildAt(idx);
            final int[] loc = new int[2];
            rl.getLocationOnScreen(loc);
            ShellUtil.tap(loc[0] + rl.getWidth() / 2, loc[1] + rl.getHeight() / 2);
        }
    }

    class HookLockActivity implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "initViews", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("LockActivity ready now.");

                    Object v = XposedHelpers.getObjectField(param.thisObject, "e");
                    if (v != null) {
                        XposedBridge.log("Switch page to the login activity with username and password");
                        XposedHelpers.callMethod(param.thisObject, "onClick", v);
                    }
                }
            });
        }
    }

    class HookLoginFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("LoginFragment ready now.");

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
    }

    class HookMyAccountFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("MyAccount ready now.");

                    final Object o = param.thisObject;
                    (new Timer()).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            TextView v = (TextView) XposedHelpers.getObjectField(o, "y");
                            if (v != null) {
                                GlobleUtil.putFloat("AvailableMoney", Float.valueOf(v.getText().toString()));
                                XposedBridge.log("可用金额： " + v.getText().toString());

                                GlobleUtil.putInt("Step", 5);
                                XposedBridge.log("Step: 5");
                            }
                        }
                    }, 3000);
                }
            });
        }
    }

    class HookFinanceFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookConstructor(cls, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    XposedBridge.log("FinanceFragment ready now.");

                    (new Timer()).schedule(new DetectTask(cls, param.thisObject), 3000);
                }
            });
        }

        class DetectTask extends java.util.TimerTask{
            Class cls;
            private Object o;
            public DetectTask(Class cls, Object o) {
                this.cls = cls;
                this.o = o;
            }

            public void run(){
                printObject(o, "c");
                printInt(o, "a");
                printInt(o, "b");
                printBoolean(o, "d");
                printLinearLayout(o, "f");
                printLinearLayout(o, "g");
                printLinearLayout(o, "h");
                printLinearLayout(o, "i");
                printObject(o, "j");
                printTextView(o, "k");
                printTextView(o, "l");
                printLinearLayout(o, "m");
                printLinearLayout(o, "n");
                printObject(o, "o");
                printObject(o, "p");
                printObject(o, "t");
                printObject(o, "u");
                printObject(o, "v");
            }
        }
    }

    private void printString(Object o, String name) {
        printObject(o, name, "String");
    }

    private void printBoolean(Object o, String name) {
        printObject(o, name, "Boolean");
    }

    private void printLong(Object o, String name) {
        printObject(o, name, "Long");
    }

    private void printInt(Object o, String name) {
        printObject(o, name, "Int");
    }

    private void printTextView(Object o, String name) {
        printObject(o, name, "TextView");
    }

    private void printLinearLayout(Object o, String name) {
        printObject(o, name, "LinearLayout");
    }

    private void printObject(Object o, String name) {
        printObject(o, name, "Object");
    }

    private void printObject(Object o, String name, String key) {
        Object v = XposedHelpers.getObjectField(o, name);
        if (v != null) {
            XposedBridge.log(name + ": " + v.toString());
        } else {
            XposedBridge.log("Failed to getField(" + key + ") \"" + name + "\"");
        }
    }

    private void hookSingleMethod(Class cls, String name, String key) {
        for (final Method method: cls.getDeclaredMethods()) {
            if (name.equals(method.getName())) {
                XposedBridge.hookMethod(method, new CommonMethodHook(method, key));
                break;
            }
        }
    }

    private void hookAllMethod(Class cls, String key) {
        for (final Method method: cls.getDeclaredMethods()) {
            XposedBridge.hookMethod(method, new CommonMethodHook(method, key));
        }
    }

    class CommonMethodHook extends XC_MethodHook {
        private Method method;
        private String key;

        public CommonMethodHook(Method m, String key) {
            this.method = m;
            this.key = key;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            if (param.args.length == 2) {
                XposedBridge.log(key + " before: [" + method.getName() + "] (" + param.args[0].toString() + ") (" + param.args[1].toString() + ")");
            } if (param.args.length == 1) {
                XposedBridge.log(key + " before: [" + method.getName() + "] (" + param.args[0].toString() + ")");
            } else {
                XposedBridge.log(key + " before: [" + method.getName() + "] ");
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            if (param.args.length == 2) {
                XposedBridge.log(key + " after [" + method.getName() + "] (" + param.args[0].toString() + ") (" + param.args[1].toString() + ")");
            } if (param.args.length == 1) {
                XposedBridge.log(key + " after [" + method.getName() + "] (" + param.args[0].toString() + ")");
            } else {
                XposedBridge.log(key + " after [" + method.getName() + "] ");
            }
        }
    }
}
