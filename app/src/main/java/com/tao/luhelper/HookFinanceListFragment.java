package com.tao.luhelper;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookFinanceListFragment extends HookBase {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:FinanceListFragment:Ready", false)) {
                    GlobleUtil.putBoolean("Class:FinanceListFragment:Ready", true);
                    GlobleUtil.log("FinanceListFragment ready now.");

                    Handler h = new Handler();
                    h.postDelayed(new TaskDispatch(h, param.thisObject), 1000);
                }
            }
        });
    }

    class ProductInfo {
        View view;
        String product;
        float rofit;
        float amount;
    }

    class TaskDispatch implements Runnable {
        Handler h;
        private Object o;

        public TaskDispatch(Handler h, Object o) {
            this.h = h;
            this.o = o;
        }

        public void run() {
            int step = GlobleUtil.getInt("Step", 0);
            if (step == 5) {
                showFilter();
            } else if (step == 7) {
                List<ProductInfo> pis = queryProductListView(GlobleUtil.getFloat("Rofit", 0),
                        GlobleUtil.getFloat("MinMoney", 0), GlobleUtil.getFloat("MaxMoney", 0));
                if (pis != null) {
                    GlobleUtil.log("Step8: Switch to the product fragment.");
                    GlobleUtil.putInt("Step", 8);

                    //pis.get(0).view.callOnClick();
                    return;
                }
            }

            h.postDelayed(this, 1000);
        }

        void showFilter() {
            GlobleUtil.log("Step6: Set the filter.");
            GlobleUtil.putInt("Step", 6);

            LinearLayout m = (LinearLayout)XposedHelpers.getObjectField(o, "m");
            if (m == null) {
                GlobleUtil.log("failed to get m.");
                return;
            }

            ViewGroup vg = (ViewGroup) m.getChildAt(0); // com.lufax.android.common.widget.PageScrollTab +id/filter_tab_container
            if (vg == null) {
                GlobleUtil.log("no any child in m.");
                return;
            }

            if (vg.getChildCount() <= 1) {
                GlobleUtil.log("there is not enough child.");
                return;
            }

            FrameLayout fl = (FrameLayout) vg.getChildAt(1);
            if (fl == null) {
                GlobleUtil.log("failed to get filter.");
                return;
            }

            fl.callOnClick();
        }

        List<ProductInfo> queryProductListView(float rofit, float minMoney, float maxMoney) {
            // PullableViewGroup z
            ViewGroup vg = (ViewGroup) XposedHelpers.getObjectField(o, "z");
            if (vg == null) {
                GlobleUtil.log("failed to get z.");
                return null;
            }
            GlobleUtil.log("vg is " + vg.toString());

            FrameLayout layer1 = (FrameLayout) vg.getChildAt(0);
            if (layer1 == null) {
                GlobleUtil.log("failed to get layer1.");
                return null;
            }
            GlobleUtil.log("layer1 is " + layer1.toString());

            FrameLayout layer2 = (FrameLayout) layer1.getChildAt(0);
            if (layer2 == null) {
                GlobleUtil.log("failed to get layer2.");
                return null;
            }
            GlobleUtil.log("layer2 is " + layer2.toString());

            ListView layer3 = (ListView) layer2.getChildAt(0);
            if (layer3 == null) {
                GlobleUtil.log("failed to get layer3.");
                return null;
            }
            GlobleUtil.log("layer3 is " + layer3.toString());

            List<ProductInfo> pis = new ArrayList<ProductInfo>();
            for (int i = 0; i < layer3.getChildCount(); i++) {
                try {
                    LinearLayout ll = (LinearLayout) layer3.getChildAt(i);
                    if (ll != null) {
                        if (ll.hasOnClickListeners()) {
                            GlobleUtil.log("ll has OnClickListeners");
                        }
                        GlobleUtil.log("ll is " + ll.toString());
                        ProductInfo pi = queryProduct(ll, rofit, minMoney, maxMoney);
                        if (pi != null) {
                            pis.add(pi);
                        }
                    }
                } catch (Exception e) {
                    GlobleUtil.log("Exception " + e.toString());
                }
            }

            return pis;
        }

        ProductInfo queryProduct(LinearLayout ll, float rofit, float minMoney, float maxMoney) {
            try {
                if (ll.getChildCount() >= 2) {
                    RelativeLayout rl = (RelativeLayout) ll.getChildAt(1);
                    if (rl != null) {
                        if (rl.hasOnClickListeners()) {
                            GlobleUtil.log("rl has OnClickListeners");
                        }
                        GlobleUtil.log("rl is " + rl.toString());

                        LinearLayout subLL = (LinearLayout) rl.getChildAt(0);
                        if (subLL.hasOnClickListeners()) {
                            GlobleUtil.log("(LinearLayout) rl.getChildAt(0) has OnClickListeners");
                        }
                        GlobleUtil.log("subLL is " + subLL.toString());

                        String strProduct = parseProduct(((TextView) subLL.getChildAt(0)).getText().toString());
                        if (strProduct != null) {
                            float dRofit = parseRofit(((TextView) rl.getChildAt(3)).getText().toString());
                            if (rofit == 0 || dRofit >= rofit) {
                                float dAmount = parseAmount(((TextView) rl.getChildAt(8)).getText().toString());
                                if (dAmount != 0) {
                                    if ((minMoney == 0 || dAmount >= minMoney) &&
                                            (maxMoney == 0 || dAmount <= maxMoney)) {
                                        ProductInfo pi = new ProductInfo();
                                        pi.view = rl;
                                        pi.product = strProduct;
                                        pi.rofit = dRofit;
                                        pi.amount = dAmount;
                                        GlobleUtil.log("Get product " + pi.toString());
                                        return pi;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

            return null;
        }

        String parseProduct(String str) {
            if (str != null && !str.isEmpty()) {
                Pattern r = Pattern.compile("稳盈-e享计划 (\\d+)");
                Matcher m = r.matcher(str);
                if (m.find()) {
                    return m.group(0);
                }
            }
            return null;
        }

        float parseRofit(String str) {
            if (str != null && !str.isEmpty() && str.charAt(str.length() - 1) == '%') {
                return Float.parseFloat(str.substring(str.length() - 1));
            }
            return 0;
        }

        float parseAmount(String str) {
            if (str != null && !str.isEmpty() && str.charAt(str.length() - 1) == '元') {
                return Float.parseFloat(str.substring(str.length() - 1));
            }
            return 0;
        }
    }

}
