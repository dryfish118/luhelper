package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
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
                hook(cl, "v2.app.other.ui.fragment", "HomeFragment", new HookHomeFragment());
                hook(cl, "navi", "BottomBar", new HookBottomBar());
                hook(cl, "gesturelock", "LockActivity", new HookLockActivity());
                hook(cl, "activity.fragments", "LoginFragment", new HookLoginFragment());
                hook(cl, "myaccount.ui", "MyAccountFragment", new HookMyAccountFragment());
                hook(cl, "v2.app.finance.ui.fragment", "FinanceFragment", new HookFinanceFragment());
            }
        });
    }

    interface IHook {
        void doHook(final Class cls);
    }

    private void hook(ClassLoader cl, String packageName, String clsName, IHook hookInstance) {
        try {
            Class<?> cls = cl.loadClass("com.lufax.android." + packageName + "." + clsName);
            if (cls != null) {
                if (!GlobleUtil.getBoolean("Class:" + clsName, false)) {
                    GlobleUtil.putBoolean("Class:" + clsName, true);
                    XposedBridge.log("Start hook: " + clsName);
                    hookInstance.doHook(cls);
                }
            }
        } catch (Exception e) {
        }
    }

    class HookHomeFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:HomeFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:HomeFragment:Ready", true);
                        XposedBridge.log("HomeFragment ready now.");

                        XposedBridge.log("Step1: Start the task.");
                        GlobleUtil.putInt("Step", 1);

                        (new Timer()).schedule(new TimerTask() {
                            public void run() {
                                XposedBridge.log("Step2: Set the signal to switch to my account fragment.");
                                GlobleUtil.putInt("Step", 2);
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

                            if (!GlobleUtil.getBoolean("Class:BottomBar:Ready", false)) {
                                GlobleUtil.putBoolean("Class:BottomBar:Ready", true);
                                XposedBridge.log("BottomBar ready now.");

                                (new Timer()).schedule(new TaskDispatch(param.thisObject), 1000, 1000);
                            }
                        }
                    });
                }
            }
        }

        class TaskDispatch extends java.util.TimerTask{
            private Object o;
            public TaskDispatch(Object o) {
                this.o = o;
            }

            public void run(){
                int step = GlobleUtil.getInt("Step", 0);
                if (step == 2) {
                    XposedBridge.log("Step3: Get the signal to switch to the MyAccount Fragment.");
                    GlobleUtil.putInt("Step", 3);

                    (new Timer()).schedule(new TaskSwitchToMyAccountFragment(o), 1000);
                } else if (step == 5) {
                    XposedBridge.log("Step6: Get the signal to switch to the Finance Fragment.");
                    GlobleUtil.putInt("Step", 6);

                    (new Timer()).schedule(new TaskSwitchToFinanceHomeFragment(o), 1000);
                }
            }
        }

        class TaskSwitchToMyAccountFragment extends java.util.TimerTask{
            private Object o;
            public TaskSwitchToMyAccountFragment(Object o) {
                this.o = o;
            }

            public void run(){
                XposedBridge.log("Step4: Switch page to the MyAccount Fragment");
                GlobleUtil.putInt("Step", 4);

                sendClickMotion(o, 3);
            }
        }

        class TaskSwitchToFinanceHomeFragment extends java.util.TimerTask{
            private Object o;
            public TaskSwitchToFinanceHomeFragment(Object o) {
                this.o = o;
            }

            public void run(){
                XposedBridge.log("Step7: Switch page to the FinanceHome Fragment.");
                GlobleUtil.putInt("Step", 7);

                sendClickMotion(o, 1);
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
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:LockActivity:Ready", false)) {
                        GlobleUtil.putBoolean("Class:LockActivity:Ready", true);
                        XposedBridge.log("LockActivity ready now.");

                        Object v = XposedHelpers.getObjectField(param.thisObject, "e");
                        if (v != null) {
                            XposedBridge.log("Switch page to the Login Activity by username and password");
                            XposedHelpers.callMethod(param.thisObject, "onClick", v);
                        }
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

                    if (!GlobleUtil.getBoolean("Class:LoginFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:LoginFragment:Ready", true);
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
                }
            });
        }
    }

    class HookMyAccountFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "g", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:MyAccountFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:MyAccountFragment:Ready", true);
                        XposedBridge.log("MyAccountFragment ready now.");

                        final Object o = param.thisObject;
                        (new Timer()).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                TextView v = (TextView) XposedHelpers.getObjectField(o, "y");
                                if (v != null) {
                                    GlobleUtil.putFloat("AvailableMoney", Float.valueOf(v.getText().toString()));
                                    XposedBridge.log("可用金额： " + v.getText().toString());

                                    XposedBridge.log("Step5: Set the signal to switch to the Finance Fragment");
                                    GlobleUtil.putInt("Step", 5);
                                }
                            }
                        }, 2000);
                    }
                }
            });
        }
    }

    class HookFinanceFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:FinanceFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:FinanceFragment:Ready", true);
                        XposedBridge.log("FinanceFragment ready now.");

                        (new Timer()).schedule(new TaskSwitchE(cls, param.thisObject), 1000);
                    }
                }
            });
        }

        class TaskSwitchE extends java.util.TimerTask{
            Class cls;
            private Object o;
            public TaskSwitchE(Class cls, Object o) {
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

            String s = key + " before: [" + method.getName() + "]";
            for (int i = 0; i < param.args.length; i++) {
                Object p = param.args[i];
                if (p == null) {
                    s = s + " (null)";
                } else {
                    s = s + " (" + p.toString() + ")";
                }
            }
            XposedBridge.log(s);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);

            String s = key + " after: [" + method.getName() + "]";
            for (int i = 0; i < param.args.length; i++) {
                Object p = param.args[i];
                if (p == null) {
                    s = s + " (null)";
                } else {
                    s = s + " (" + p.toString() + ")";
                }
            }
            XposedBridge.log(s);
        }
    }
}
