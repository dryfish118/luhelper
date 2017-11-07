package com.tao.luhelper;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

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
                hook(cl, "v2.app.home", "HomeFragmentV2", new HookHomeFragment());
                hook(cl, "navi", "BottomBar", new HookBottomBar());
                hook(cl, "gesturelock", "LockActivity", new HookLockActivity());
                hook(cl, "activity.fragments", "LoginFragment", new HookLoginFragment());
                hook(cl, "myaccount.ui", "MyAccountFragment", new HookMyAccountFragment());
                hook(cl, "v2.app.finance.kotlin", "Finance4Fragment", new HookFinance4Fragment());
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment", new HookFinanceListFragment());
                hook(cl, "v2.app.finance.ui.widget.filter", "FilterRegionLayout", new HookFilterRegionLayout());
            }
        });
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


//    private void printString(Object o, String name) {
//        printObject(o, name, "String");
//    }
//
//    private void printBoolean(Object o, String name) {
//        printObject(o, name, "Boolean");
//    }
//
//    private void printLong(Object o, String name) {
//        printObject(o, name, "Long");
//    }
//
//    private void printInt(Object o, String name) {
//        printObject(o, name, "Int");
//    }
//
//    private void printTextView(Object o, String name) {
//        printObject(o, name, "TextView");
//    }
//
//    private void printLinearLayout(Object o, String name) {
//        printObject(o, name, "LinearLayout");
//    }
//
//    private void printObject(Object o, String name) {
//        printObject(o, name, "Object");
//    }
//
//    private void printObject(Object o, String name, String key) {
//        Object v = XposedHelpers.getObjectField(o, name);
//        if (v != null) {
//            XposedBridge.log(name + ": " + v.toString());
//        } else {
//            XposedBridge.log("Failed to getField(" + key + ") \"" + name + "\"");
//        }
//    }
//
//    private void hookSingleMethod(Class cls, String name, String key) {
//        for (final Method method: cls.getDeclaredMethods()) {
//            if (name.equals(method.getName())) {
//                XposedBridge.hookMethod(method, new CommonMethodHook(method, key));
//                break;
//            }
//        }
//    }
}
