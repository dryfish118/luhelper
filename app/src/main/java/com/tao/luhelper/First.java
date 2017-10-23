package com.tao.luhelper;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Tao on 2017/10/23.
 */

public class First implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ClassLoader cl = ((Context)param.args[0]).getClassLoader();
                Class<?> homeFragmentClass = null;
                Class<?> myAccountFragmentClass = null;
                try {
                    homeFragmentClass = cl.loadClass("com.lufax.android.v2.app.other.ui.fragment.HomeFragment");
                    myAccountFragmentClass = cl.loadClass("com.lufax.android.myaccount.ui.MyAccountFragment");
                } catch (Exception e) {
                    return;
                }

                if (homeFragmentClass != null) {
                    XposedBridge.log("find class: com.lufax.android.v2.app.other.ui.fragment.HomeFragment");

                    XposedHelpers.findAndHookMethod(homeFragmentClass, "onCreateView",
                            android.view.LayoutInflater.class, android.view.ViewGroup.class,
                            android.os.Bundle.class, new XC_MethodHook(){
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    XposedBridge.log("hook method: onCreateView");
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    boolean v_a = (boolean)XposedHelpers.getObjectField(param.thisObject, "A");
                                    XposedBridge.log("A: " + v_a);

                                    int v_d = (int)XposedHelpers.getObjectField(param.thisObject, "D");
                                    XposedBridge.log("D: " + v_d);

                                    boolean v_f = (boolean)XposedHelpers.getObjectField(param.thisObject, "F");
                                    XposedBridge.log("F: " + v_f);

                                    TextView v_i = (TextView)XposedHelpers.getObjectField(param.thisObject, "I");
                                    XposedBridge.log("I: " + v_i.getText().toString());

                                    TextView v_j = (TextView)XposedHelpers.getObjectField(param.thisObject, "J");
                                    XposedBridge.log("J: " + v_j.getText().toString());
                                }
                            });
                } else if (myAccountFragmentClass != null) {
                    XposedBridge.log("find class: com.lufax.android.myaccount.ui.MyAccountFragment");

                    XposedHelpers.findAndHookMethod(myAccountFragmentClass, "onCreateView",
                            android.view.LayoutInflater.class, android.view.ViewGroup.class,
                            android.os.Bundle.class, new XC_MethodHook(){
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    XposedBridge.log("hook method: onCreateView");
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    TextView v_a = (TextView)XposedHelpers.getObjectField(param.thisObject, "A");
                                    XposedBridge.log("A: " + v_a.getText().toString());

                                    TextView v_b = (TextView)XposedHelpers.getObjectField(param.thisObject, "B");
                                    XposedBridge.log("B: " + v_b.getText().toString());

                                    TextView v_c = (TextView)XposedHelpers.getObjectField(param.thisObject, "C");
                                    XposedBridge.log("C: " + v_c.getText().toString());

                                    TextView v_d = (TextView)XposedHelpers.getObjectField(param.thisObject, "D");
                                    XposedBridge.log("D: " + v_d.getText().toString());

                                    TextView v_g = (TextView)XposedHelpers.getObjectField(param.thisObject, "G");
                                    XposedBridge.log("G: " + v_g.getText().toString());

                                    TextView v_j = (TextView)XposedHelpers.getObjectField(param.thisObject, "J");
                                    XposedBridge.log("J: " + v_j.getText().toString());

                                    TextView v_k = (TextView)XposedHelpers.getObjectField(param.thisObject, "K");
                                    XposedBridge.log("K: " + v_k.getText().toString());

                                    String v_v = (String)XposedHelpers.getObjectField(param.thisObject, "V");
                                    XposedBridge.log("A: " + v_v);
                                }
                            });
                }
            }
        });
    }
}
