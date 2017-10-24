package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Tao on 2017/10/23.
 */

public class HookTest implements IXposedHookLoadPackage {
    static int mHookTime = 0;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

//        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//            int mCurrentTime = 0;
//
//            private void hookSingleMethod(Class<?> c, final String name) {
//                XposedHelpers.findAndHookMethod(c, name,
//                        new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                XposedBridge.log(mCurrentTime + " before " + name);
//                            }
//
//                            @Override
//                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                XposedBridge.log(mCurrentTime + " after " + name);
//                            }
//                        });
//            }
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                ClassLoader cl = ((Context)param.args[0]).getClassLoader();
//                Class<?> myAccountFragmentClass = null;
//                try {
//                    myAccountFragmentClass = cl.loadClass("com.lufax.android.myaccount.ui.MyAccountFragment");
//                } catch (Exception e) {
//                    return;
//                }
//
//                if (myAccountFragmentClass != null) {
//                    mCurrentTime = ++HookTest.this.mHookTime;
//
//                    XposedBridge.log("find class: (" + mCurrentTime +") MyAccountFragment");

//                    XposedHelpers.findAndHookMethod(myAccountFragmentClass, "onCreateView",
//                            android.view.LayoutInflater.class, android.view.ViewGroup.class,
//                            android.os.Bundle.class,
//                            new XC_MethodHook() {
//                                @Override
//                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                    XposedBridge.log(mCurrentTime + " before " + "onCreateView");
//                                }
//
//                                @Override
//                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                    XposedBridge.log(mCurrentTime + " after " + "onCreateView");
//                                }
//                            });
//                    hookSingleMethod(myAccountFragmentClass, "a");
//                    hookSingleMethod(myAccountFragmentClass, "b");
//                    hookSingleMethod(myAccountFragmentClass, "c");
//                    hookSingleMethod(myAccountFragmentClass, "d");
//                    hookSingleMethod(myAccountFragmentClass, "e");
//                    hookSingleMethod(myAccountFragmentClass, "f");
//                    hookSingleMethod(myAccountFragmentClass, "g");
//                    hookSingleMethod(myAccountFragmentClass, "h");
//                    hookSingleMethod(myAccountFragmentClass, "i");
//                    hookSingleMethod(myAccountFragmentClass, "j");
//                    hookSingleMethod(myAccountFragmentClass, "k");
//                    hookSingleMethod(myAccountFragmentClass, "l");
//                    hookSingleMethod(myAccountFragmentClass, "m");
//                    hookSingleMethod(myAccountFragmentClass, "n");
//                    hookSingleMethod(myAccountFragmentClass, "o");
//                    hookSingleMethod(myAccountFragmentClass, "p");
//                    hookSingleMethod(myAccountFragmentClass, "q");
//                    hookSingleMethod(myAccountFragmentClass, "r");
//                    hookSingleMethod(myAccountFragmentClass, "s");
//                    hookSingleMethod(myAccountFragmentClass, "t");
//                    hookSingleMethod(myAccountFragmentClass, "u");
//                    hookSingleMethod(myAccountFragmentClass, "v");
//                    hookSingleMethod(myAccountFragmentClass, "x");
//                    hookSingleMethod(myAccountFragmentClass, "y");
//                    hookSingleMethod(myAccountFragmentClass, "z");


//                    XposedHelpers.findAndHookMethod(myAccountFragmentClass, "onCreateView",
//                            android.view.LayoutInflater.class, android.view.ViewGroup.class,
//                            android.os.Bundle.class,
//                    XposedHelpers.findAndHookMethod(myAccountFragmentClass, "a",
//                            java.lang.Object.class, java.lang.String.class, boolean.class, int.class,
//                    XposedHelpers.findAndHookMethod(myAccountFragmentClass, "h",
//                            new XC_MethodHook(){
//                                @Override
//                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                    XposedBridge.log("hook method: (" + mCurrentTime +") h");
//                                }
//
//                                @Override
//                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                    printTextView(param.thisObject, "A");
//                                    printTextView(param.thisObject, "B");
//                                    printTextView(param.thisObject, "C");
//                                    printTextView(param.thisObject, "D");
//                                    printTextView(param.thisObject, "G");
//                                    printTextView(param.thisObject, "J");
//                                    printTextView(param.thisObject, "K");
//
//                                    printString(param.thisObject, "V");
//
//                                    printTextView(param.thisObject, "ac");
//                                    printBoolean(param.thisObject, "aq");
//                                    printLong(param.thisObject, "ar");
//
//                                    printTextView(param.thisObject, "i");
//                                    printTextView(param.thisObject, "q");
//                                    printBoolean(param.thisObject, "t");
//                                    printTextView(param.thisObject, "u");
//                                    printTextView(param.thisObject, "v");
//                                    printTextView(param.thisObject, "w");
//                                    printTextView(param.thisObject, "x");
//                                    printTextView(param.thisObject, "y");
//                                    printTextView(param.thisObject, "z");
//                                }
//
//                                private void printTextView(Object o, String name) {
//                                    Object v = XposedHelpers.getObjectField(o, name);
//                                    if (v != null) {
//                                        XposedBridge.log(name + ": (" + mCurrentTime + ") " + ((TextView)v).getText().toString());
//                                    } else {
//                                        XposedBridge.log("(" + mCurrentTime + ") failed to getField(TextView) \"" + name + "\"");
//                                    }
//                                }
//
//                                private void printString(Object o, String name) {
//                                    Object v = XposedHelpers.getObjectField(o, name);
//                                    if (v != null) {
//                                        XposedBridge.log(name + ": (" + mCurrentTime + ") " + (String)v);
//                                    } else {
//                                        XposedBridge.log("(" + mCurrentTime + ") failed to getField(String) \"" + name + "\"");
//                                    }
//                                }
//
//                                private void printBoolean(Object o, String name) {
//                                    Object v = XposedHelpers.getObjectField(o, name);
//                                    if (v != null) {
//                                        XposedBridge.log(name + ": (" + mCurrentTime + ") " + (boolean)v);
//                                    } else {
//                                        XposedBridge.log("(" + mCurrentTime + ") failed to getField(boolean) \"" + name + "\"");
//                                    }
//                                }
//
//                                private void printLong(Object o, String name) {
//                                    Object v = XposedHelpers.getObjectField(o, name);
//                                    if (v != null) {
//                                        XposedBridge.log(name + ": (" + mCurrentTime + ") " + (long)v);
//                                    } else {
//                                        XposedBridge.log("(" + mCurrentTime + ") failed to getField(long) \"" + name + "\"");
//                                    }
//                                }
//                            });
//                            myAccountFragmentClass = null;
//                }
//            }
//        });
    }
}
