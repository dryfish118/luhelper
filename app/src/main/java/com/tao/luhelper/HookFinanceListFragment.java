package com.tao.luhelper;

import android.os.Handler;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

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
                    XposedBridge.log("FinanceListFragment ready now.");

                    Handler h = new Handler();
                    h.postDelayed(new TaskDispatch(h, param.thisObject), 1000);
                }
            }
        });
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
                queryProduct();
                return;
            }

            h.postDelayed(this, 1000);
        }

        void showFilter() {
            XposedBridge.log("Step6: Set the filter.");
            GlobleUtil.putInt("Step", 6);

            LinearLayout m = (LinearLayout)XposedHelpers.getObjectField(o, "m");
            if (m == null) {
                XposedBridge.log("failed to get m.");
                return;
            }

            ViewGroup vg = (ViewGroup) m.getChildAt(0); // com.lufax.android.common.widget.PageScrollTab +id/filter_tab_container
            if (vg == null) {
                XposedBridge.log("no any child in m.");
                return;
            }

            if (vg.getChildCount() <= 1) {
                XposedBridge.log("there is not enough child.");
                return;
            }

            FrameLayout fl = (FrameLayout) vg.getChildAt(1);
            if (fl == null) {
                XposedBridge.log("failed to get filter.");
                return;
            }

            fl.callOnClick();
        }

        void printField(Object o, String name) {
            Object filed = XposedHelpers.getObjectField(o, name);
            if (filed == null) {
                XposedBridge.log("    " + name + " is null.");
            } else {
                XposedBridge.log("    " + name + " is " + filed.toString());
            }
        }


        void queryProduct() {
            // ProductListGson b
            Object b = XposedHelpers.getObjectField(o, "b");
            if (b == null) {
                XposedBridge.log("b is null.");
                return;
            }
            XposedBridge.log("b is " + b.toString());

            // List<Product> products
            List products = (List)XposedHelpers.getObjectField(b, "products");
            if (products == null) {
                XposedBridge.log("products is null.");
                return;
            }
            XposedBridge.log("products is " + products.toString());

            for (int i = 0; i < products.size(); i++) {
                Object product = products.get(i);
                if (product == null) {
                    XposedBridge.log("product" + (i + 1) + " is null.");
                    continue;
                }
                XposedBridge.log("product" + (i + 1) + " is " + product.toString());

                // List<ProductInfo> productList;
                List productList = (List)XposedHelpers.getObjectField(product, "productList");
                if (productList == null) {
                    XposedBridge.log("productList is null.");
                    continue;
                }
                XposedBridge.log("productList is " + productList.toString());

                for (int j = 0; j < productList.size(); j++) {
                    Object pi = productList.get(j);
                    if (pi == null) {
                        XposedBridge.log("ProductInfo" + (j + 1) + " is null.");
                        continue;
                    }
                    XposedBridge.log("ProductInfo" + (j + 1) + " is " + pi.toString());

                    printField(pi, "bannerType");
                    printField(pi, "canRealized");
                    printField(pi, "code");
                    printField(pi, "collectionMode");
                    printField(pi, "commonRcmdReason");
                    printField(pi, "creditServiceInstitution");
                    printField(pi, "currentFundPriceDesc");
                    printField(pi, "discountTagDisplay");
                    printField(pi, "displayName");
                    printField(pi, "district");
                    printField(pi, "extAnyiRemainInvestAmount");
                    printField(pi, "extAuth");
                    printField(pi, "extBottomInfo");
                    printField(pi, "extDianjinDisplay");
                    printField(pi, "extForceOrder");
                    printField(pi, "extInterestRateDisplay");
                    printField(pi, "extInterestRatePercentage");
                    printField(pi, "extInterestRateSuffix");
                    printField(pi, "extInterestRateUnit");
                    printField(pi, "extInvestAmountDisplay");
                    printField(pi, "extInvestAmoutUnitDisplay");
                    printField(pi, "extInvestPeriodDisplay");
                    printField(pi, "extInvestProfitDesc");
                    printField(pi, "extIsShowProgress");
                    printField(pi, "extIsVipGroup");
                    printField(pi, "extMinHoldingDaysDisplay");
                    printField(pi, "extMinInvestAmountDisplay");
                    printField(pi, "extNeedLogin");
                    printField(pi, "extNextCollectionDate");
                    printField(pi, "extProductNameDisplay");
                    printField(pi, "extProductNameDisplayTip");
                    printField(pi, "extProductStatusDesc");
                    printField(pi, "extProgress");
                    printField(pi, "extPromotionDisplay");
                    printField(pi, "extReducePriceDisplay");
                    printField(pi, "extRiskLevelStarCountDisplay");
                    printField(pi, "extSameAnyiProductCounts");
                    printField(pi, "extTransferPriceDisplay");
                    printField(pi, "features");
                    printField(pi, "guaranteeDesc");
                    printField(pi, "id");
                    printField(pi, "increaseInvestAmount");
                    printField(pi, "initProductStatus");
                    printField(pi, "insuranceContent");
                    printField(pi, "insuranceInvestUnitDisplay");
                    printField(pi, "interestRate");
                    printField(pi, "interestRateDesc");
                    printField(pi, "interestRateDisplay");
                    printField(pi, "interestRatePerSevenDay");
                    printField(pi, "investPeriod");
                    printField(pi, "investPeriodUnit");
                    printField(pi, "itemStyle");
                    printField(pi, "itemType");
                    printField(pi, "listType");
                    printField(pi, "listTypeName");
                    printField(pi, "maxInvestAmount");
                    printField(pi, "minInvestAmount");
                    printField(pi, "position1");
                    printField(pi, "position10");
                    printField(pi, "position11");
                    printField(pi, "position12");
                    printField(pi, "position13");
                    printField(pi, "position14");
                    printField(pi, "position15");
                    printField(pi, "position16");
                    printField(pi, "position17");
                    printField(pi, "position2");
                    printField(pi, "position2Color");
                    printField(pi, "position3");
                    printField(pi, "position3Type");
                    printField(pi, "position4");
                    printField(pi, "position5");
                    printField(pi, "position6");
                    printField(pi, "position7");
                    printField(pi, "position8");
                    printField(pi, "position9");
                    printField(pi, "price");
                    printField(pi, "principal");
                    printField(pi, "productCategory");
                    printField(pi, "productFeature");
                    printField(pi, "productName");
                    printField(pi, "productNameSuffixDisplay");
                    printField(pi, "productPropDtoList");
                    printField(pi, "productStatus");
                    printField(pi, "productType");
                    printField(pi, "ratio");
                    printField(pi, "remainingAmount");
                    printField(pi, "riskLevel");
                    printField(pi, "ruleId");
                    printField(pi, "salesArea");
                    printField(pi, "salesChannel");
                    printField(pi, "schemaLink");
                    printField(pi, "skuCode");
                    printField(pi, "smallProductImage");
                    printField(pi, "sourceType");
                    printField(pi, "subProductCategory");
                    printField(pi, "subType");
                    printField(pi, "tradingMode");
                    printField(pi, "userSpecialRcmdReason");
                    printField(pi, "version");

                }
            }
        }
    }

}
