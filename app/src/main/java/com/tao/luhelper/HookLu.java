package com.tao.luhelper;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by Tao on 2017/10/23.
 */

public class HookLu implements IXposedHookLoadPackage {
    private static final Point[] positions = {
            new Point(340, 890), new Point(690, 890), new Point(1040, 890),
            new Point(340, 1240), new Point(690, 1240), new Point(1040, 1240),
            new Point(340, 1590), new Point(690, 1590), new Point(1040, 1590)
    };

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
            }
        });
    }

    interface IHook {
        void doHook(final Class cls);
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

    class HookHomeFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:HomeFragmentV2:Ready", false)) {
                        GlobleUtil.putBoolean("Class:HomeFragmentV2:Ready", true);
                        XposedBridge.log("HomeFragmentV2 ready now.");

                        XposedBridge.log("Step1: Start the task.");
                        GlobleUtil.putInt("Step", 1);

                        (new Timer()).schedule(new TimerTask() {
                            public void run() {
                                XposedBridge.log("Step2: Set the signal to switch to my account fragment.");
                                GlobleUtil.putInt("Step", 2);
                            }
                        }, 1000);
                    }
                }
            });
        }
    }

    class HookBottomBar implements IHook {
        @Override
        public void doHook(final Class cls) {
            for (final Method method : cls.getDeclaredMethods()) {
                if ("setItemsIconResource".equals(method.getName())) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            if (!GlobleUtil.getBoolean("Class:BottomBar:Ready", false)) {
                                GlobleUtil.putBoolean("Class:HBottomBar:Ready", true);
                                XposedBridge.log("BottomBar ready now.");

                                (new Timer()).schedule(new TaskDispatch(param.thisObject), 1000, 1000);
                            }
                        }
                    });
                }
            }
        }

        class TaskDispatch extends java.util.TimerTask{
            private Object o;
            public TaskDispatch(Object o) {
                this.o = o;
            }

            public void run(){
                int step = GlobleUtil.getInt("Step", 0);
                if (step == 2) {
                    XposedBridge.log("Step3: Get the signal to switch to the MyAccount Fragment.");
                    GlobleUtil.putInt("Step", 3);

                    (new Timer()).schedule(new TaskSwitchToMyAccountFragment(o), 1000);
                } else if (step == 5) {
                    XposedBridge.log("Step6: Get the signal to switch to the Finance Fragment.");
                    GlobleUtil.putInt("Step", 6);

                    (new Timer()).schedule(new TaskSwitchToFinanceHomeFragment(o), 1000);
                }
            }
        }

        class TaskSwitchToMyAccountFragment extends java.util.TimerTask{
            private Object o;
            public TaskSwitchToMyAccountFragment(Object o) {
                this.o = o;
            }

            public void run(){
                XposedBridge.log("Step4: Switch to the MyAccount Fragment.");
                GlobleUtil.putInt("Step", 4);

                if (!sendClickMotion(o, 3)) {
                    XposedBridge.log("Failed to switch to the MyAccount Fragment.");
                    GlobleUtil.putInt("Step", 3);
                    (new Timer()).schedule(new TaskSwitchToMyAccountFragment(o), 1000);
                } else {
                    (new Timer()).schedule(new TaskMonitorTapMyAccountFragment(o), 5000);
                }
            }
        }

        class TaskMonitorTapMyAccountFragment extends java.util.TimerTask{
            private Object o;
            public TaskMonitorTapMyAccountFragment(Object o) {
                this.o = o;
            }

            public void run(){
                if (!GlobleUtil.getBoolean("Class:LockActivity:Ready", false) &&
                        !GlobleUtil.getBoolean("Class:LoginFragment:Ready", false) &&
                        GlobleUtil.getInt("Step", 0) <= 4) {
                    XposedBridge.log("Failed to switch to the MyAccount Fragment.");
                    (new Timer()).schedule(new TaskSwitchToMyAccountFragment(o), 100);
                }
            }
        }

        class TaskSwitchToFinanceHomeFragment extends java.util.TimerTask{
            private Object o;
            public TaskSwitchToFinanceHomeFragment(Object o) {
                this.o = o;
            }

            public void run(){
                XposedBridge.log("Step7: Switch to the FinanceHome Fragment.");
                GlobleUtil.putInt("Step", 7);

                sendClickMotion(o, 1);
            }
        }

        private boolean sendClickMotion(final Object o, int idx) {
            LinearLayout ll1 = (LinearLayout) o;
            LinearLayout ll2 = (LinearLayout) ll1.getChildAt(0);
            LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
            RelativeLayout rl = (RelativeLayout) ll3.getChildAt(idx);
            final int[] loc = new int[2];
            rl.getLocationOnScreen(loc);
            int w = rl.getWidth();
            int h = rl.getHeight();
            if (loc[1] == 0 || w == 0 || h == 0) {
                return false;
            }
            ShellUtil.tap(loc[0] + w / 2, loc[1] + h / 2);
            return true;
        }
    }

    class HookLockActivity implements IHook {

        private void loginByInput(Object o) {
            XposedBridge.log("Switch to the Login Activity by username and password.");
            Object btn = XposedHelpers.getObjectField(o, "e");
            if (btn != null) {
                XposedHelpers.callMethod(o, "onClick", btn);
            }
        }

        private void loginBySwipe(final Object o) {
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    String strGesture = GlobleUtil.getString("Gesture", "");
                    byte[] chs = strGesture.getBytes();
                    ArrayList<Point> pnts = new ArrayList<Point>();
                    for (int i = 0; i < chs.length; i++) {
                        int pos = (int)(chs[i] - (byte)'1');
                        if (pos >= 0 && pos < 9) {
                            pnts.add(new Point(positions[pos]));
                        }
                    }
                    XposedBridge.log("Swipe to login.");
                    ShellUtil.swipe(pnts);
                }
            }, 1000);
        }

        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "d", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    loginBySwipe(param.thisObject);
                }
            });

            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:LockActivity:Ready", false)) {
                        GlobleUtil.putBoolean("Class:LockActivity:Ready", true);
                        XposedBridge.log("LockActivity ready now.");

                        String strGesture = GlobleUtil.getString("Gesture", "");
                        if (strGesture.isEmpty()) {
                            loginByInput(param.thisObject);
                        } else {
                            loginBySwipe(param.thisObject);
                        }
                    }
                }
            });
        }
    }

    class HookLoginFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:LoginFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:LoginFragment:Ready", true);
                        XposedBridge.log("LoginFragment ready now.");

                        String userName = GlobleUtil.getString("UserName", "");
                        String loginPassword = GlobleUtil.getString("LoginPassword", "");
                        if (!"".equals(userName) && !"".equals(loginPassword)) {
                            Object v = XposedHelpers.getObjectField(param.thisObject, "h");
                            if (v != null) {
                                EditText[] ets = (EditText[]) v;
                                ets[0].setText(userName);
                                ets[1].setText(loginPassword);

                                v = XposedHelpers.getObjectField(param.thisObject, "l");
                                if (v != null) {
                                    XposedBridge.log("Click to login.");
                                    XposedHelpers.callMethod(param.thisObject, "onClick", v);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    class HookMyAccountFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "g", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:MyAccountFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:MyAccountFragment:Ready", true);
                        XposedBridge.log("MyAccountFragment ready now.");

                        final Object o = param.thisObject;
                        (new Timer()).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                TextView v = (TextView) XposedHelpers.getObjectField(o, "y");
                                if (v != null) {
                                    GlobleUtil.putFloat("AvailableMoney", Float.valueOf(v.getText().toString()));
                                    XposedBridge.log("可用金额： " + v.getText().toString());

                                    XposedBridge.log("Step5: Set the signal to switch to the Finance Fragment");
                                    GlobleUtil.putInt("Step", 5);
                                }
                            }
                        }, 100);
                    }
                }
            });
        }
    }

    class HookFinance4Fragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:Finance4Fragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:Finance4Fragment:Ready", true);
                        XposedBridge.log("Finance4Fragment ready now.");

                        (new Timer()).schedule(new SwipeToBottom(cls, param.thisObject, 0), 1000);
                    }
                }
            });
        }

        class SwipeToBottom extends java.util.TimerTask{
            Class cls;
            private Object o;
            int times;
            public SwipeToBottom(Class cls, Object o, int times) {
                this.cls = cls;
                this.o = o;
                this.times = times;
            }

            public void run() {
                FrameLayout h = (FrameLayout)XposedHelpers.getObjectField(o, "h");
                DisplayMetrics dm = h.getContext().getResources().getDisplayMetrics();
                ArrayList<Point> pnts = new ArrayList<Point>();
                pnts.add(new Point(dm.widthPixels / 2, dm.heightPixels / 8 * 7));
                pnts.add(new Point(dm.widthPixels / 2, dm.heightPixels / 8));
                ShellUtil.swipe(pnts);

                if (times == 2) {
                    (new Timer()).schedule(new TaskToFinanceList(cls, o), 1000);
                } else {
                    (new Timer()).schedule(new SwipeToBottom(cls, o, times + 1), 1000);
                }
            }
        }

        class TaskToFinanceList extends java.util.TimerTask{
            Class cls;
            private Object o;
            public TaskToFinanceList(Class cls, Object o) {
                this.cls = cls;
                this.o = o;
            }

            public void run(){
                // LufaxMaskView h
                FrameLayout h = (FrameLayout)XposedHelpers.getObjectField(o, "h");
                if (h != null) {
                    if (h.getChildCount() > 0) {
                        // WrapLayout h.getChildAt(0)
                        ViewGroup wl = (ViewGroup)h.getChildAt(0);
                        if (wl != null) {
                            for (int i = 0; i < wl.getChildCount(); i++) {
                                // NavCategoryView ncv
                                ViewGroup ncv = (ViewGroup)wl.getChildAt(i);
                                if (ncv != null) {
                                    TextView l = (TextView) XposedHelpers.getObjectField(ncv, "l");
                                    if ("会员交易区".equals(l.getText().toString())) {
                                        XposedBridge.log("    会员交易区");
                                        View child = (View)ncv.getChildAt(3);
                                        int loc[] = new int[2];
                                        child.getLocationOnScreen(loc);
                                        ShellUtil.tap(loc[0] + child.getWidth() / 2, loc[1] + child.getHeight() / 2);
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

            private void pNavCategoryView(String name) {
                XposedBridge.log("Finance4Fragment : " + name);
                Object u = XposedHelpers.getObjectField(o, name);
                if (u == null) {
                    if (u instanceof FrameLayout) {
                        FrameLayout ncv = (FrameLayout)u;
                        XposedBridge.log("    " + ncv.toString());
                        for (int i = 0; i < ncv.getChildCount(); i++) {
                            Object c = ncv.getChildAt(i);
                            XposedBridge.log("        " + c.toString());
                        }
                    } else {
                        XposedBridge.log("    u is " + u.toString());
                    }
                } else {
                    XposedBridge.log("    u is null");
                }

            }

            private LinearLayout findInContainer(LinearLayout llContainer) {
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    LinearLayout ll = (LinearLayout)llContainer.getChildAt(i);
                    if (ll != null) {
                        if (findInLinearLayout(ll)) {
                            return ll;
                        }
                    }
                }

                return null;
            }

            private boolean findInLinearLayout(LinearLayout ll) {
                for (int i = 0; i < ll.getChildCount(); i++) {
                    Object o = ll.getChildAt(i);
                    if (o != null && o instanceof RelativeLayout) {
                        return findInRelativeLayout((RelativeLayout)o);
                    }
                }

                return false;
            }

            private boolean findInRelativeLayout(RelativeLayout rl) {
                for (int i = 0; i < rl.getChildCount(); i++) {
                    Object o = rl.getChildAt(i);
                    if (o != null && o instanceof LinearLayout) {
                        return findInSubLinearLayout((LinearLayout)o);
                    }
                }

                return false;
            }

            private boolean findInSubLinearLayout(LinearLayout ll) {
                for (int i = 0; i < ll.getChildCount(); i++) {
                    Object o = ll.getChildAt(i);
                    if (o != null && o instanceof TextView) {
                        if (-1 != ((TextView)o).getText().toString().indexOf("e享")) {
                            return true;
                        }
                        break;
                    }
                }

                return false;
            }
        }
    }

    class HookFinanceListFragment implements IHook {
        @Override
        public void doHook(final Class cls) {
            XposedHelpers.findAndHookMethod(cls, "getScreenName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    if (!GlobleUtil.getBoolean("Class:FinanceListFragment:Ready", false)) {
                        GlobleUtil.putBoolean("Class:FinanceListFragment:Ready", true);
                        XposedBridge.log("FinanceListFragment ready now.");

                        (new Timer()).schedule(new TaskAquireProject(cls, param.thisObject), 1000);
                    }
                }
            });
        }

        class TaskAquireProject extends java.util.TimerTask {
            Class cls;
            private Object o;

            public TaskAquireProject(Class cls, Object o) {
                this.cls = cls;
                this.o = o;
            }

            public void run() {
                GlobleUtil.putBoolean("Class:FinanceListFragment:Ready", false);


            }
        }
    }

    private void printString(Object o, String name) {
        printObject(o, name, "String");
    }

    private void printBoolean(Object o, String name) {
        printObject(o, name, "Boolean");
    }

    private void printLong(Object o, String name) {
        printObject(o, name, "Long");
    }

    private void printInt(Object o, String name) {
        printObject(o, name, "Int");
    }

    private void printTextView(Object o, String name) {
        printObject(o, name, "TextView");
    }

    private void printLinearLayout(Object o, String name) {
        printObject(o, name, "LinearLayout");
    }

    private void printObject(Object o, String name) {
        printObject(o, name, "Object");
    }

    private void printObject(Object o, String name, String key) {
        Object v = XposedHelpers.getObjectField(o, name);
        if (v != null) {
            XposedBridge.log(name + ": " + v.toString());
        } else {
            XposedBridge.log("Failed to getField(" + key + ") \"" + name + "\"");
        }
    }

    private void hookSingleMethod(Class cls, String name, String key) {
        for (final Method method: cls.getDeclaredMethods()) {
            if (name.equals(method.getName())) {
                XposedBridge.hookMethod(method, new CommonMethodHook(method, key));
                break;
            }
        }
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
