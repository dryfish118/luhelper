package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by Tao on 2017/10/23.
 */

public class HookSample implements IXposedHookLoadPackage {

    boolean found = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (found) {
                    return;
                }

                Application app = (Application) param.thisObject;
                Object loadedApk = XposedHelpers.getObjectField(app, "mLoadedApk");
                //log("mLoadedApk is " + loadedApk.toString());
                String packageName = (String) XposedHelpers.getObjectField(loadedApk, "mPackageName");
                //log("mPackageName is " + packageName);

                if (!packageName.equals("com.lufax.android")) {
                    return;
                }

                log("com.lufax.android found");
                found = true;


                try {
                    log("sourceDir " + lpparam.appInfo.sourceDir);
                    DexFile dexFile = new DexFile(lpparam.appInfo.sourceDir);
                    Enumeration<String> classNames = dexFile.entries();
                    if (classNames != null) {
                        while (classNames.hasMoreElements()) {
                            String className = classNames.nextElement();

                            if (className != null &&
                                    className.startsWith("com.lufax.android.v2.app") &&
                                    !className.contains("BuildConfig") &&
                                    !className.contains(".R$")) {
                                //log("className " + className);

                                final Class clazz = Class.forName(className, false, lpparam.classLoader);
                                if (clazz != null) {
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method != null) {
                                            //log("   Method: " + method.toString());
                                            if (!Modifier.isAbstract(method.getModifiers())) {
                                                XposedBridge.hookMethod(method, new XC_MethodHook() {
                                                    @Override
                                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                                        log(clazz.getName().substring(25) + "-" + param.method.getName());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log(e);
                } catch (Exception e) {
                    log(e);
                }


//                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                try {
//                    Class<?> cls = cl.loadClass("com.lufax.android." + packageName + "." + clsName);
//                    if (cls != null) {
//                        if (!GlobleUtil.getBoolean("Class:" + clsName, false)) {
//                            GlobleUtil.putBoolean("Class:" + clsName, true);
//                            XposedBridge.log("Start hook: " + clsName);
//                            hookInstance.doHook(cls);
//                        }
//                    }
//                } catch (Exception e) {
//                }
            }
        });



//        if (!"com.lufax.android".equals(lpparam.packageName)) {
//            return;
//        }
//
//        try {
//            log("sourceDir " + lpparam.appInfo.sourceDir);
//            DexFile dexFile = new DexFile(lpparam.appInfo.sourceDir);
//            Enumeration<String> classNames = dexFile.entries();
//            if (classNames != null) {
//                while (classNames.hasMoreElements()) {
//                    String className = classNames.nextElement();
//
//                    if (className != null &&
//                            className.startsWith(lpparam.packageName) &&
//                            !className.contains("BuildConfig") &&
//                            !className.contains(".R$")) {
//                        log("className " + className);
//
//                        final Class clazz = Class.forName(className, false, lpparam.classLoader);
//                        if (clazz != null) {
//                            for (Method method : clazz.getDeclaredMethods()) {
//                                if (method != null) {
//                                    log("   Method: " + method.toString());
//                                    if (!Modifier.isAbstract(method.getModifiers())) {
//                                        XposedBridge.hookMethod(method, new XC_MethodHook() {
//                                            @Override
//                                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                                log("HOOKED: " + clazz.getName() + "\\" + param.method.getName());
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (ClassNotFoundException e) {
//
//        } catch (Exception e) {
//
//        }
    }

    public void log(Object str) {

        java.util.Date today = new java.util.Date();
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("mm:ss:SSS");
        GlobleUtil.log("[" + dateTimeFormat.format(today) + "]" + str.toString());
    }
}
