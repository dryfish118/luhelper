package com.tao.luhelper;

import android.app.Application;
import android.content.Context;

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
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment$1", new HookObject("FinanceListFragment$1"));
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment$4", new HookObject("FinanceListFragment$4"));
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment$7", new HookObject("FinanceListFragment$7"));
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment$8", new HookObject("FinanceListFragment$8"));
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment$9", new HookObject("FinanceListFragment$9"));
                hook(cl, "v2.app.finance.ui.fragment", "FinanceListFragment$a", new HookObject("FinanceListFragment$a"));
                hook(cl, "v2.app.finance.ui.widget.filter", "FilterRegionLayout", new HookFilterRegionLayout());
                hook(cl, "v2.app.finance.ui.widget.slideexpandlistview", "FinanceActionSlideExpandBottomFloatListView$1", new HookObject("FinanceActionSlideExpandBottomFloatListView$1"));
                hook(cl, "v2.app.finance.ui.widget.slideexpandlistview", "FinanceActionSlideExpandBottomFloatListView$3", new HookObject("FinanceActionSlideExpandBottomFloatListView$3"));
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

}
