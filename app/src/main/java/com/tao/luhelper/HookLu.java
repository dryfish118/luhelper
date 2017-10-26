package com.tao.luhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
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
                hook(cl, "com.lufax.android.navi.a", "a", new Hooka());
                hook(cl, "com.lufax.android.navi.ui.BottomBar$AnonymousClass1", "AnonymousClass1", new HookAnonymousClass1());
            }
        });
    }

    interface IHook {
        void doHook(Class cls);
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
        public void doHook(Class cls) {
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
        }
    }

    class Hooka implements IHook {
        @Override
        public void doHook(Class cls) {
            hookAllMethod(cls, "A");
        }
    }

    class HookAnonymousClass1 implements IHook {
        @Override
        public void doHook(Class cls) {
            hookAllMethod(cls, "AnonymousClass1");
        }
    }

    class HookBottomBar implements IHook {
        @Override
        public void doHook(Class cls) {
            //hookAllMethod(cls, "BottomBar");

//            for (Method method: cls.getDeclaredMethods()) {
//                XposedBridge.hookMethod(method, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        if (param.args.length == 2) {
//                            XposedBridge.log("before: [" + param.method.getName() + "] (" + param.args[0].toString() + ") (" + param.args[1].toString() + ")");
//                        } if (param.args.length == 1) {
//                            XposedBridge.log("before: [" + param.method.getName() + "] (" + param.args[0].toString() + ")");
//                        } else {
//                            XposedBridge.log("before: [" + param.method.getName() + "] ");
//                        }
//
//                        if (param.thisObject != null) {
//                            Object h = XposedHelpers.getObjectField(param.thisObject, "h");
//                            if (h != null) {
//                                XposedBridge.log("[h]" + h.toString());
//                            }
//                            List a = (List) XposedHelpers.getObjectField(param.thisObject, "a");
//                            if (a != null) {
//                                XposedBridge.log("[a]" + a.toString());
//                            }
//                        }
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        if (param.args.length == 2) {
//                            XposedBridge.log("before: [" + param.method.getName() + "] (" + param.args[0].toString() + ") (" + param.args[1].toString() + ")");
//                        } if (param.args.length == 1) {
//                            XposedBridge.log("before: [" + param.method.getName() + "] (" + param.args[0].toString() + ")");
//                        } else {
//                            XposedBridge.log("before: [" + param.method.getName() + "] ");
//                        }
//
//                        if (param.thisObject != null) {
//                            Object h = XposedHelpers.getObjectField(param.thisObject, "h");
//                            if (h != null) {
//                                XposedBridge.log("[h]" + h.toString());
//                            }
//                            List a = (List) XposedHelpers.getObjectField(param.thisObject, "a");
//                            if (a != null) {
//                                XposedBridge.log("[a]" + a.toString());
//                            }
//                        }
//                    }
//                });
//            }


            for (final Method method : cls.getDeclaredMethods()) {
                if ("setItemsIconResource".equals(method.getName())) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("Hook: setItemsIconResource");

                            (new Timer()).schedule(new FlowTask(param.thisObject), 1000, 1000);
                        }
                    });
                } else if ("dispatchTouchEvent".equals(method.getName())) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            MotionEvent me = (MotionEvent)param.args[0];
                            XposedBridge.log("beforeHookedMethod: dispatchTouchEvent " + me.toString());
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_X): " + me.getAxisValue(MotionEvent.AXIS_X));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_Y): " + me.getAxisValue(MotionEvent.AXIS_Y));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_PRESSURE): " + me.getAxisValue(MotionEvent.AXIS_PRESSURE));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_SIZE): " + me.getAxisValue(MotionEvent.AXIS_SIZE));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR): " + me.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOUCH_MINOR): " + me.getAxisValue(MotionEvent.AXIS_TOUCH_MINOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOOL_MAJOR): " + me.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOOL_MINOR): " + me.getAxisValue(MotionEvent.AXIS_TOOL_MINOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_ORIENTATION): " + me.getAxisValue(MotionEvent.AXIS_ORIENTATION));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_DISTANCE): " + me.getAxisValue(MotionEvent.AXIS_DISTANCE));
                            XposedBridge.log("getActionIndex(): " + me.getActionIndex());
                            XposedBridge.log("getX(): " + me.getX());
                            XposedBridge.log("getY(): " + me.getY());
                            XposedBridge.log("getPressure(): " + me.getPressure());
                            XposedBridge.log("getSize(): " + me.getSize());
                            XposedBridge.log("getPointerCount(): " + me.getPointerCount());
                            XposedBridge.log("getPointerId(): " + me.getPointerId(0));
                            XposedBridge.log("getToolType(): " + me.getToolType(0));
                            XposedBridge.log("getButtonState(): " + me.getButtonState());
                            XposedBridge.log("getRawX(): " + me.getRawX());
                            XposedBridge.log("getRawY(): " + me.getRawY());
                            XposedBridge.log("getXPrecision(): " + me.getXPrecision());
                            XposedBridge.log("getYPrecision(): " + me.getYPrecision());
                            XposedBridge.log("getHistorySize(): " + me.getHistorySize());
                            XposedBridge.log("getEdgeFlags(): " + me.getEdgeFlags());
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            MotionEvent me = (MotionEvent)param.args[0];
                            param.setResult(true);
                            XposedBridge.log("afterHookedMethod: dispatchTouchEvent " + me.toString());
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_X): " + me.getAxisValue(MotionEvent.AXIS_X));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_Y): " + me.getAxisValue(MotionEvent.AXIS_Y));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_PRESSURE): " + me.getAxisValue(MotionEvent.AXIS_PRESSURE));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_SIZE): " + me.getAxisValue(MotionEvent.AXIS_SIZE));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR): " + me.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOUCH_MINOR): " + me.getAxisValue(MotionEvent.AXIS_TOUCH_MINOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOOL_MAJOR): " + me.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_TOOL_MINOR): " + me.getAxisValue(MotionEvent.AXIS_TOOL_MINOR));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_ORIENTATION): " + me.getAxisValue(MotionEvent.AXIS_ORIENTATION));
                            XposedBridge.log("getAxisValue(MotionEvent.AXIS_DISTANCE): " + me.getAxisValue(MotionEvent.AXIS_DISTANCE));
                            XposedBridge.log("getActionIndex(): " + me.getActionIndex());
                            XposedBridge.log("getX(): " + me.getX());
                            XposedBridge.log("getY(): " + me.getY());
                            XposedBridge.log("getPressure(): " + me.getPressure());
                            XposedBridge.log("getSize(): " + me.getSize());
                            XposedBridge.log("getPointerCount(): " + me.getPointerCount());
                            XposedBridge.log("getPointerId(): " + me.getPointerId(0));
                            XposedBridge.log("getToolType(): " + me.getToolType(0));
                            XposedBridge.log("getButtonState(): " + me.getButtonState());
                            XposedBridge.log("getRawX(): " + me.getRawX());
                            XposedBridge.log("getRawY(): " + me.getRawY());
                            XposedBridge.log("getXPrecision(): " + me.getXPrecision());
                            XposedBridge.log("getYPrecision(): " + me.getYPrecision());
                            XposedBridge.log("getHistorySize(): " + me.getHistorySize());
                            XposedBridge.log("getEdgeFlags(): " + me.getEdgeFlags());
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

                    (new Timer()).schedule(new Step3Task(o), 3000);
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

                sendClickMotion(o, 3);
            }
        }

        private void execShellCmd(String cmd) {

            XposedBridge.log("Shell: " + cmd);

            try {
                // 申请获取root权限，这一步很重要，不然会没有作用
                Process process = Runtime.getRuntime().exec("su");
                // 获取输出流
                OutputStream outputStream = process.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(
                        outputStream);
                dataOutputStream.writeBytes(cmd);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        private void sendClickMotion(final Object o, int idx) {
            LinearLayout ll1 = (LinearLayout) o;

//            List a = (List)XposedHelpers.getObjectField(o, "a");
//            if (a != null) {
//
//
//
////                XposedBridge.log("a.class: " + a.getClass().toString());
////                XposedBridge.log("a: " + a.toString());
////                Object v = a.get(idx);
////                if (v != null) {
////                    XposedBridge.log("v.class: " + v.getClass().toString());
////                    XposedBridge.log("v: " + v.toString());
////                    XposedHelpers.callMethod(v, "a");
////                }
//                return;
//            }

            Activity activity = (Activity)ll1.getContext();

            XposedBridge.log("LinearLayout1 " + ll1.getLeft() + " " + ll1.getRight() + " " + ll1.getTop() + " " + ll1.getBottom());

            LinearLayout ll2 = (LinearLayout) ll1.getChildAt(0);
            XposedBridge.log("LinearLayout2 " + ll2.getLeft() + " " + ll2.getRight() + " " + ll2.getTop() + " " + ll2.getBottom());

            LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
            XposedBridge.log("LinearLayout3 " + ll3.getLeft() + " " + ll3.getRight() + " " + ll3.getTop() + " " + ll3.getBottom());

            RelativeLayout rl = (RelativeLayout) ll3.getChildAt(idx);
            XposedBridge.log("RelativeLayout " + rl.getLeft() + " " + rl.getRight() + " " + rl.getTop() + " " + rl.getBottom());

            final int[] location1 = new int[2];
            rl.getLocationOnScreen(location1);
            final int[] location2 = new int[2];
            ll1.getLocationOnScreen(location2);

            execShellCmd("input tap " + (location1[0] + rl.getWidth() / 2) + " " + (location1[1] + rl.getHeight() / 2));

            long downTime = SystemClock.uptimeMillis();
            try
            {
                MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
                pointerProperties[0] = new MotionEvent.PointerProperties();
                pointerProperties[0].id = 0;
                pointerProperties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
                MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
                pointerCoords[0] = new MotionEvent.PointerCoords();
                pointerCoords[0].x = location1[0] + rl.getWidth() / 2;
                pointerCoords[0].y = location1[1] + rl.getHeight() / 2;
//                pointerCoords[0].pressure = 1;
//                pointerCoords[0].size = 1;
//                pointerCoords[0].touchMajor = 100;
//                pointerCoords[0].touchMinor = 100;
//                pointerCoords[0].toolMajor = 100;
//                pointerCoords[0].toolMinor = 100;
//                pointerCoords[0].orientation = 0;
                //pointerCoords[0].x = 952;
                //pointerCoords[0].y = 1722;
                pointerCoords[0].pressure = 0.36f;
                pointerCoords[0].size = 0.8f;
                pointerCoords[0].touchMajor = 149;
                pointerCoords[0].touchMinor = 149;
                pointerCoords[0].toolMajor = 149;
                pointerCoords[0].toolMinor = 149;
                pointerCoords[0].orientation = 0;
                MotionEvent meDown = MotionEvent.obtain(
                        downTime,
                        downTime,
                        MotionEvent.ACTION_DOWN,
                        1,
                        pointerProperties,
                        pointerCoords,
                        0,
                        0,
                        1.236f,
                        1.23f,
                        6,
                        0,
                        0x1002,
                        0);
                //meDown.setLocation(952, 60);
                //meDown.offsetLocation(-location2[0], -location2[1]);
                XposedBridge.log("before down dispatchTouchEvent"
                        + " " + meDown.getAxisValue(MotionEvent.AXIS_X) + " " + meDown.getAxisValue(MotionEvent.AXIS_Y)
                        + " " + meDown.getRawX() + " " + meDown.getRawY());
                //XposedHelpers.callMethod(o, "dispatchTouchEvent", meDown);
                //activity.dispatchTouchEvent(meDown);
                meDown.recycle();
                XposedBridge.log("after down dispatchTouchEvent");
            } catch (Exception e) {

            }
            try
            {
                MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
                pointerProperties[0] = new MotionEvent.PointerProperties();
                pointerProperties[0].id = 0;
                pointerProperties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
                MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
                pointerCoords[0] = new MotionEvent.PointerCoords();
                pointerCoords[0].x = location1[0] + rl.getWidth() / 2;
                pointerCoords[0].y = location1[1] + rl.getHeight() / 2;
//                pointerCoords[0].pressure = 1;
//                pointerCoords[0].size = 1;
//                pointerCoords[0].touchMajor = 100;
//                pointerCoords[0].touchMinor = 100;
//                pointerCoords[0].toolMajor = 100;
//                pointerCoords[0].toolMinor = 100;
//                pointerCoords[0].orientation = 0;
                //pointerCoords[0].x = 952;
                //pointerCoords[0].y = 1722;
                pointerCoords[0].pressure = 0.36f;
                pointerCoords[0].size = 0.8f;
                pointerCoords[0].touchMajor = 149;
                pointerCoords[0].touchMinor = 149;
                pointerCoords[0].toolMajor = 149;
                pointerCoords[0].toolMinor = 149;
                pointerCoords[0].orientation = 0;

                MotionEvent meUp = MotionEvent.obtain(
                        downTime,
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP,
                        1,
                        pointerProperties,
                        pointerCoords,
                        0,
                        0,
                        1.236f,
                        1.23f,
                        6,
                        0,
                        0x1002,
                        0);
                //meUp.setLocation(952, 60);
                //meUp.offsetLocation(-location2[0], -location2[1]);
                XposedBridge.log("before up dispatchTouchEvent"
                        + " " + meUp.getAxisValue(MotionEvent.AXIS_X) + " " + meUp.getAxisValue(MotionEvent.AXIS_Y)
                        + " " + meUp.getRawX() + " " + meUp.getRawY());
                //XposedHelpers.callMethod(o, "dispatchTouchEvent", meUp);
                //activity.dispatchTouchEvent(meUp);
                meUp.recycle();
                XposedBridge.log("after up dispatchTouchEvent");
            } catch (Exception e) {

            }
        }
    }

    class HookLockActivity implements IHook {
        @Override
        public void doHook(Class cls) {
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
    }

    class HookLoginFragment implements IHook {
        @Override
        public void doHook(Class cls) {
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
    }

    class HookMyAccountFragment implements IHook {
        @Override
        public void doHook(Class cls) {
            XposedHelpers.findAndHookMethod(cls, "h", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    TextView v = (TextView) XposedHelpers.getObjectField(param.thisObject, "y");
                    if (v != null) {
                        XposedBridge.log("可用金额： " + v.getText().toString());
                        GlobleUtil.putBoolean("Monitor", false);
                    }
                }
            });
        }
    }

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
        for (final Method method: cls.getDeclaredMethods()) {
            XposedBridge.hookMethod(method, new XC_MethodHook() {
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
            });
        }
    }
}
