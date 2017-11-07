package com.tao.luhelper;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public abstract class HookBase implements IHook {

    void printString(Object o, String name) {
        printObject(o, name, "String");
    }

    void printBoolean(Object o, String name) {
        printObject(o, name, "Boolean");
    }

    void printLong(Object o, String name) {
        printObject(o, name, "Long");
    }

    void printInt(Object o, String name) {
        printObject(o, name, "Int");
    }

    void printTextView(Object o, String name) {
        printObject(o, name, "TextView");
    }

    void printLinearLayout(Object o, String name) {
        printObject(o, name, "LinearLayout");
    }

    void printObject(Object o, String name) {
        printObject(o, name, "Object");
    }

    void printObject(Object o, String name, String key) {
        Object v = XposedHelpers.getObjectField(o, name);
        if (v != null) {
            XposedBridge.log(name + ": " + v.toString());
        } else {
            XposedBridge.log("Failed to getField(" + key + ") \"" + name + "\"");
        }
    }

    void hookSingleMethod(Class cls, String name, String key) {
        for (final Method method: cls.getDeclaredMethods()) {
            if (name.equals(method.getName())) {
                XposedBridge.hookMethod(method, new CommonMethodHook(method, key));
                break;
            }
        }
    }

    void hookAllMethod(Class cls, String key) {
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
