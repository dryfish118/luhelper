package com.tao.luhelper;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookObject implements IHook {
    private String key;

    HookObject(String key) {
        this.key = key;
    }

    @Override
    public void doHook(final Class cls) {
        hookAllMethod(cls, key);
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
