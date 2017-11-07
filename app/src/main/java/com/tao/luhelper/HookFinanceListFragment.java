package com.tao.luhelper;

import android.os.Handler;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookFinanceListFragment implements IHook {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:FinanceListFragment:Ready", false)) {
                    GlobleUtil.putBoolean("Class:FinanceListFragment:Ready", true);
                    XposedBridge.log("FinanceListFragment ready now.");

                    Handler h = new Handler();
                    h.postDelayed(new TaskAquireProject(h, param.thisObject), 1000);
                }
            }
        });
    }

    class TaskAquireProject implements Runnable {
        Handler h;
        private Object o;

        public TaskAquireProject(Handler h, Object o) {
            this.h = h;
            this.o = o;
        }

        public void run() {
            int step = GlobleUtil.getInt("Step", 0);
            if (step < 5) {
                h.postDelayed(this, 1000);
            } else if (step == 5) {
                XposedBridge.log("Step6: Set the filter.");
                GlobleUtil.putInt("Step", 6);

                LinearLayout m = (LinearLayout)XposedHelpers.getObjectField(o, "m");
                if (m != null) {
                    ViewGroup vg = (ViewGroup) m.getChildAt(0); // com.lufax.android.common.widget.PageScrollTab +id/filter_tab_container
                    if (vg != null) {
                        if (vg.getChildCount() > 1) {
                            FrameLayout fl = (FrameLayout) vg.getChildAt(1);
                            if (fl != null) {
                                fl.callOnClick();
                                h.postDelayed(this, 1000);
                            } else {
                                XposedBridge.log("failed to get filter.");
                            }
                        } else {
                            XposedBridge.log("there is not enough child.");
                        }
                    } else {
                        XposedBridge.log("no any child in m.");
                    }
                } else {
                    XposedBridge.log("failed to get m.");
                }
            } else if (step == 7) {
                GlobleUtil.putBoolean("Monitor", false);
                XposedBridge.log("Done");
            }

            //printLinearLayout(o, "m");
            //printLinearLayout(o, "n"); fixedHeadLayout
        }
    }

}
