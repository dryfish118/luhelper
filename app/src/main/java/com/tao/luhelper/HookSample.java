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

    XC_LoadPackage.LoadPackageParam loadPackageParam;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

        if (!"com.lufax.android".equals(lpparam.packageName)) {
            return;
        }

        loadPackageParam = lpparam;

        try {
            log("sourceDir " + loadPackageParam.appInfo.sourceDir);
            DexFile dexFile = new DexFile(loadPackageParam.appInfo.sourceDir);
            Enumeration<String> classNames = dexFile.entries();
            while (classNames.hasMoreElements()) {
                String className = classNames.nextElement();

                if (isClassNameValid(className)) {
                    log("className " + className);

                    final Class clazz = Class.forName(className, false, loadPackageParam.classLoader);

                    for (Method method : clazz.getDeclaredMethods()) {
                        log("   Method: " + method.toString());
                        if (!Modifier.isAbstract(method.getModifiers())) {
                            XposedBridge.hookMethod(method, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    log("HOOKED: " + clazz.getName() + "\\" + param.method.getName());
                                }
                            });
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {

        } catch (Exception e) {

        }
    }

    public void log(Object str) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        XposedBridge.log("[" + df.format(new Date()) + "]:  "
                + str.toString());
    }

    public boolean isClassNameValid(String className) {
        return className.startsWith(loadPackageParam.packageName)
                && !className.contains("BuildConfig")
                && !className.equals(loadPackageParam.packageName + ".R$");
    }
}
