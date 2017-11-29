package com.tao.luhelper;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
                //GlobleUtil.log("mLoadedApk is " + loadedApk.toString());
                String packageName = (String) XposedHelpers.getObjectField(loadedApk, "mPackageName");
                //GlobleUtil.log("mPackageName is " + packageName);

                if (!packageName.equals("com.lufax.android")) {
                    return;
                }

                GlobleUtil.log("com.lufax.android found");
                found = true;


                try {
                    GlobleUtil.log("sourceDir " + lpparam.appInfo.sourceDir);
                    DexFile dexFile = new DexFile(lpparam.appInfo.sourceDir);
                    Enumeration<String> classNames = dexFile.entries();
                    if (classNames != null) {
                        while (classNames.hasMoreElements()) {
                            String className = classNames.nextElement();

                            if (className != null &&
                                    className.startsWith("com.lufax.android.v2.app") &&
                                    !className.contains("BuildConfig") &&
                                    !className.contains(".R$")) {
                                //GlobleUtil.log("className " + className);

                                final Class clazz = Class.forName(className, false, lpparam.classLoader);
                                if (clazz != null) {
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method != null) {
                                            //GlobleUtil.log("   Method: " + method.toString());
                                            if (!Modifier.isAbstract(method.getModifiers())) {
                                                XposedBridge.hookMethod(method, new XC_MethodHook() {
                                                    @Override
                                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                                        String msg = clazz.getName().substring(25) + "::" + param.method.getName() + "(";
                                                        for (int i = 0; i < param.args.length; i++) {
                                                            if (i > 0) {
                                                                msg = msg + ", ";
                                                            }
                                                            Object arg = param.args[i];
                                                            if (arg == null) {
                                                                msg = msg + "null";
                                                            } else {
                                                                msg = msg + arg.toString();
                                                            }
                                                        }
                                                        msg = msg + ")";
                                                        GlobleUtil.log(msg);
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
                    GlobleUtil.log(e.toString());
                } catch (Exception e) {
                    GlobleUtil.log(e.toString());
                }


//                ClassLoader cl = ((Context) param.args[0]).getClassLoader();
//                try {
//                    Class<?> cls = cl.loadClass("com.lufax.android." + packageName + "." + clsName);
//                    if (cls != null) {
//                        if (!GlobleUtil.getBoolean("Class:" + clsName, false)) {
//                            GlobleUtil.putBoolean("Class:" + clsName, true);
//                            GlobleUtil.log("Start hook: " + clsName);
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
//                        GlobleUtil.log("className " + className);
//
//                        final Class clazz = Class.forName(className, false, lpparam.classLoader);
//                        if (clazz != null) {
//                            for (Method method : clazz.getDeclaredMethods()) {
//                                if (method != null) {
//                                    GlobleUtil.log("   Method: " + method.toString());
//                                    if (!Modifier.isAbstract(method.getModifiers())) {
//                                        XposedBridge.hookMethod(method, new XC_MethodHook() {
//                                            @Override
//                                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                                GlobleUtil.log("HOOKED: " + clazz.getName() + "\\" + param.method.getName());
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
}
