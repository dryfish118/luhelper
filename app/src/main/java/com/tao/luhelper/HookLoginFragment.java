package com.tao.luhelper;

import android.view.View;
import android.widget.EditText;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by dengtao on 2017/11/7.
 */

public class HookLoginFragment extends HookBase {

    @Override
    public void doHook(final Class cls) {
        XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (!GlobleUtil.getBoolean("Class:LoginFragment:Ready", false)) {
                    GlobleUtil.putBoolean("Class:LoginFragment:Ready", true);
                    GlobleUtil.log("LoginFragment ready now.");

                    String userName = GlobleUtil.getString("UserName", "");
                    String loginPassword = GlobleUtil.getString("LoginPassword", "");
                    if (!"".equals(userName) && !"".equals(loginPassword)) {
                        Object v = XposedHelpers.getObjectField(param.thisObject, "h");
                        if (v != null) {
                            EditText[] ets = (EditText[]) v;
                            ets[0].setText(userName);
                            ets[1].setText(loginPassword);

                            GlobleUtil.log("Click to login.");
                            ((View)XposedHelpers.getObjectField(param.thisObject, "l")).callOnClick();
                        }
                    }
                }
            }
        });
    }

}
