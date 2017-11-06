package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.view.View;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by Tao on 2017/10/23.
 */

public class HookSample implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

        if (!"com.lufax.android".equals(lpparam.packageName)) {
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                //XposedBridge.log(param.thisObject.toString());


//                try {
//                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                    Class<?> cls = XposedHelpers.findClass("com.lufax.android.navi.BottomBar$AnonymousClass1", cl);
//                    if (cls != null) {
//                        XposedBridge.log("Succeeded to hook BottomBar$AnonymousClass1");
//                    } else {
//                        XposedBridge.log("Failed to hook BottomBar$AnonymousClass1");
//                    }
//                } catch (Exception e) {
//                    XposedBridge.log("Except to hook BottomBar$AnonymousClass1");
//                }

//                try {
//                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                    Class<?> cls = XposedHelpers.findClass("com.lufax.android.navi.BottomBar.AnonymousClass1", cl);
//                    if (cls != null) {
//                        XposedBridge.log("Succeeded to hook BottomBar.AnonymousClass1");
//                    } else {
//                        XposedBridge.log("Failed to hook BottomBar.AnonymousClass1");
//                    }
//                } catch (Exception e) {
//                    XposedBridge.log("Except to hook BottomBar.AnonymousClass1");
//                }

//                try {
//                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                    Class<?> cls = XposedHelpers.findClass("com.lufax.android.navi.BottomBar$1", cl);
//                    if (cls != null) {
//                        XposedBridge.log("Succeeded to hook BottomBar$1");
//                    } else {
//                        XposedBridge.log("Failed to hook BottomBar$1");
//                    }
//                } catch (Exception e) {
//                    XposedBridge.log("Except to hook BottomBar$1");
//                }

//                try {
//                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                    XposedHelpers.findAndHookMethod("com.lufax.android.navi.BottomBar$AnonymousClass1",
//                            cl, "onClick", View.class, new XC_MethodHook() {
//                                @Override
//                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                    super.beforeHookedMethod(param);
//
//                                    XposedBridge.log("---Hook Start---");
//                                }
//
//                                @Override
//                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                    super.afterHookedMethod(param);
//
//                                    XposedBridge.log("---Hook End---");
//                                }
//                            });
//                    XposedBridge.log("Succeeded to hook BottomBar$AnonymousClass1");
//                } catch (Exception e) {
//                    XposedBridge.log("Except to hook BottomBar$AnonymousClass1");
//                }


//                try {
//                    ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                    Class<?> cls = cl.loadClass("com.lufax.android.navi.BottomBar$AnonymousClass1");
//                    if (cls != null) {
//                        XposedBridge.log("Succeeded to hook BottomBar$AnonymousClass1");
//                    } else {
//                        XposedBridge.log("Failed to hook BottomBar$AnonymousClass1");
//                    }
//                } catch (Exception e) {
//                    XposedBridge.log("Except to hook BottomBar$AnonymousClass1");
//                }
            }
        });



//        if (!"com.tao.sample".equals(lpparam.packageName)) {
//            return;
//        }
//
//        XposedBridge.log("sample is hooked");
//
//        XposedHelpers.findAndHookMethod("com.tao.sample.MainActivity$1", lpparam.classLoader, "onClick", View.class, new XC_MethodHook() {
//
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("---Hook Start---");
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("---Hook End---");
//                    }
//                });
    }
}
