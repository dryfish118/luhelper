package com.tao.luhelper;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookFinance4Fragment implements IHook {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:Finance4Fragment:Ready", false)) {
                    GlobleUtil.putBoolean("Class:Finance4Fragment:Ready", true);
                    XposedBridge.log("Finance4Fragment ready now.");

                    Handler h = new Handler();
                    h.postDelayed(new TaskIntoList(h, param.thisObject), 1000);
                }
            }
        });
    }

    class TaskIntoList implements Runnable {
        Handler h = null;
        Object o = null;

        TaskIntoList(Handler h, Object o) {
            this.h = h;
            this.o = o;
        }

        @Override
        public void run() {
            int step = GlobleUtil.getInt("Step", 0);
            if (step < 4) {
                h.postDelayed(this, 1000);
            } else if (step == 4) {
                XposedBridge.log("Step5: Switch to the Finance List Fragment.");
                GlobleUtil.putInt("Step", 5);
                h.postDelayed(this, 2000);
            } else if (step == 5) {
                FrameLayout fl = (FrameLayout) XposedHelpers.getObjectField(o, "h");
                if (fl != null) {
                    if (fl.getChildCount() > 0) {
                        // WrapLayout fl.getChildAt(0)
                        ViewGroup wl = (ViewGroup) fl.getChildAt(0);
                        if (wl != null) {
                            for (int i = 0; i < wl.getChildCount(); i++) {
                                // NavCategoryView ncv
                                ViewGroup ncv = (ViewGroup) wl.getChildAt(i);
                                if (ncv != null) {
                                    TextView l = (TextView) XposedHelpers.getObjectField(ncv, "l");
                                    if ("会员交易区".equals(l.getText().toString())) {
                                        XposedBridge.log("Click into FinanceList");
                                        View child = (View) ncv.getChildAt(3);
                                        child.callOnClick();
                                        break;
                                    }
                                } else {
                                    XposedBridge.log("failed to get ncv" + i);
                                }
                            }
                        } else {
                            XposedBridge.log("failed to get wl");
                        }
                    } else {
                        XposedBridge.log("no any child in h");
                    }
                } else {
                    XposedBridge.log("failed to get h");
                }
            }
        }
    }

}
