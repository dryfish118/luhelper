package com.tao.luhelper;

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
