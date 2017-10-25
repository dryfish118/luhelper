package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
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

    public HookLu() {
        XposedBridge.log("Hook lu inited.");
        GlobleUtil.putBoolean("Monitor", false);
    }

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
                                }, 8000);
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }

            private void hookBottomBar(final Class<?> cls) {
                for (final Method method: cls.getDeclaredMethods()) {
                    if ("dispatchTouchEvent".equals(method.getName())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                super.beforeHookedMethod(param);
//
//                                MotionEvent mv = (MotionEvent)param.args[0];
//                                if (mv.getAction() == MotionEvent.ACTION_DOWN) {
//                                    RelativeLayout rl = (RelativeLayout)((LinearLayout)((LinearLayout)((LinearLayout)param.thisObject).getChildAt(0)).getChildAt(1)).getChildAt(3);
//                                    long tmDown = SystemClock.uptimeMillis();
//                                    MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
//                                    pointerProperties[0] = new MotionEvent.PointerProperties();
//                                    pointerProperties[0].id = 0;
//                                    pointerProperties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
//                                    MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
//                                    pointerCoords[0] = new MotionEvent.PointerCoords();
//                                    pointerCoords[0].x = (rl.getLeft() + rl.getRight()) / 2;
//                                    pointerCoords[0].y = (rl.getTop() + rl.getBottom()) / 2;
//                                    XposedBridge.log("Press to myAccount: " + pointerCoords[0].x + " " + pointerCoords[0].y);
//                                    MotionEvent mvDown = MotionEvent.obtain(tmDown, tmDown, MotionEvent.ACTION_DOWN, 1,
//                                            pointerProperties, pointerCoords, 0, 0, 0, 0, 6, 0, 0x1002, 0);
//                                    XposedBridge.log("Replace: " + mv.getX() + " " + mv.getY() + " " + mvDown.getX() + " " + mvDown.getY() + " " + mvDown.getX(0) + " " + mvDown.getY(0));
//                                    param.args[0] = mvDown;
//                                }
//
//                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                MotionEvent me = (MotionEvent)param.args[0];
                                XposedBridge.log("Hook: dispatchTouchEvent " + me.toString());
                                XposedBridge.log("getPressure " + me.getPressure());
                                XposedBridge.log("getSize " + me.getSize());
                                XposedBridge.log("getTouchMajor " + me.getTouchMajor());
                                XposedBridge.log("getTouchMinor " + me.getTouchMinor());
                                XposedBridge.log("getToolMajor " + me.getToolMajor());
                                XposedBridge.log("getToolMinor " + me.getToolMinor());
                                XposedBridge.log("getOrientation " + me.getOrientation());

                                XposedBridge.log("MotionEvent.AXIS_ORIENTATION " + me.getAxisValue(MotionEvent.AXIS_ORIENTATION));
                                XposedBridge.log("MotionEvent.AXIS_PRESSURE " + me.getAxisValue(MotionEvent.AXIS_PRESSURE));
                                XposedBridge.log("MotionEvent.AXIS_SIZE " + me.getAxisValue(MotionEvent.AXIS_SIZE));
                                XposedBridge.log("MotionEvent.AXIS_TOOL_MAJOR " + me.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR));
                                XposedBridge.log("MotionEvent.AXIS_TOUCH_MAJOR " + me.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR));
                                XposedBridge.log("MotionEvent.AXIS_X " + me.getAxisValue(MotionEvent.AXIS_X));
                                XposedBridge.log("MotionEvent.AXIS_Y " + me.getAxisValue(MotionEvent.AXIS_Y));
                                XposedBridge.log("MotionEvent.AXIS_RELATIVE_X " + me.getAxisValue(MotionEvent.AXIS_RELATIVE_X));
                                XposedBridge.log("MotionEvent.AXIS_RELATIVE_Y " + me.getAxisValue(MotionEvent.AXIS_RELATIVE_Y));

                                XposedBridge.log("getRawX " + me.getRawX());
                                XposedBridge.log("getRawY " + me.getRawY());
                                XposedBridge.log("getXPrecision " + me.getXPrecision());
                                XposedBridge.log("getYPrecision " + me.getYPrecision());
                            }
                        });
                    } else if ("setItemsIconResource".equals(method.getName())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                XposedBridge.log("Hook: setItemsIconResource");

                                final Object o = param.thisObject;
                                Timer timer = new Timer();
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

                                                    try {
                                                        RelativeLayout rl = (RelativeLayout)((LinearLayout)((LinearLayout)((LinearLayout)o).getChildAt(0)).getChildAt(1)).getChildAt(3);
                                                        long tmDown = SystemClock.uptimeMillis();
                                                        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
                                                        pointerProperties[0] = new MotionEvent.PointerProperties();
                                                        pointerProperties[0].id = 0;
                                                        pointerProperties[0].toolType = MotionEvent.TOOL_TYPE_FINGER;
                                                        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
                                                        pointerCoords[0] = new MotionEvent.PointerCoords();
                                                        pointerCoords[0].orientation = 0;
                                                        pointerCoords[0].pressure = 0.25f;
                                                        pointerCoords[0].size = 0.5f;
                                                        pointerCoords[0].toolMajor = 120;
                                                        pointerCoords[0].toolMinor = 120;
                                                        pointerCoords[0].touchMajor = 120;
                                                        pointerCoords[0].touchMinor = 120;
                                                        pointerCoords[0].x = (rl.getLeft() + rl.getRight()) / 2;
                                                        pointerCoords[0].y = (rl.getTop() + rl.getBottom()) / 2;

                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_ORIENTATION, pointerCoords[0].orientation);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_PRESSURE, pointerCoords[0].pressure);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_SIZE, pointerCoords[0].size);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_TOOL_MAJOR, pointerCoords[0].toolMajor);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_TOOL_MINOR, pointerCoords[0].toolMinor);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_TOUCH_MAJOR, pointerCoords[0].touchMajor);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_TOUCH_MINOR, pointerCoords[0].touchMinor);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_X, pointerCoords[0].x);
                                                        pointerCoords[0].setAxisValue(MotionEvent.AXIS_Y, pointerCoords[0].y);

                                                        XposedBridge.log("Press to myAccount: " + pointerCoords[0].x + " " + pointerCoords[0].y);
                                                        MotionEvent meDown = MotionEvent.obtain(tmDown, tmDown, MotionEvent.ACTION_DOWN, 1,
                                                                pointerProperties, pointerCoords, 0, 0, 1.23f, 1.23f, 6, 0, 0x1002, 0);
                                                        XposedBridge.log("Down: " + meDown.getX() + " " + meDown.getY() + " " + meDown.getX(0) + " " + meDown.getY(0));
                                                        XposedHelpers.callMethod(o, "dispatchTouchEvent", meDown);
                                                        MotionEvent meUp = MotionEvent.obtain(tmDown, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 1,
                                                                pointerProperties, pointerCoords, 0, 0, 1.23f, 1.23f, 6, 0, 0x1002, 0);
                                                        XposedBridge.log("Up: " + meUp.getX() + " " + meUp.getY() + " " + meUp.getX(0) + " " + meUp.getY(0));
                                                        XposedHelpers.callMethod(o, "dispatchTouchEvent", meUp);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, 3000);
                                        }
                                    }
                                }, 1000, 1000);
                            }
                        });
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
