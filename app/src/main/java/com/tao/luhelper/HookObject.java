package com.tao.luhelper;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookObject extends HookBase {
    private String key;

    HookObject(String key) {
        this.key = key;
    }

    @Override
    public void doHook(final Class cls) {
        hookAllMethod(cls, key);
    }
}
