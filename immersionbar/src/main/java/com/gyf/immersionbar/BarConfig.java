package com.gyf.immersionbar;

import static com.gyf.immersionbar.Constants.IMMERSION_NAVIGATION_BAR_HEIGHT;
import static com.gyf.immersionbar.Constants.IMMERSION_NAVIGATION_BAR_HEIGHT_LANDSCAPE;
import static com.gyf.immersionbar.Constants.IMMERSION_NAVIGATION_BAR_WIDTH;
import static com.gyf.immersionbar.Constants.IMMERSION_STATUS_BAR_HEIGHT;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

/**
 * The type Bar config.
 *
 * @author geyifeng
 * @date 2017 /5/11
 */
class BarConfig {

    private int mStatusBarHeight;
    private final int mActionBarHeight;
    private boolean mHasNavigationBar;
    private int mNavigationBarHeight;
    private int mNavigationBarWidth;
    private final boolean mInPortrait;
    private final float mSmallestWidthDp;

    // Android 15+ 新增字段
    private Insets mSystemBarsInsets;
    private Insets mDisplayCutoutInsets;
    private Insets mNavigationBarsInsets;

    /**
     * Instantiates a new Bar config.
     *
     * @param activity the activity
     */
    BarConfig(Activity activity) {
        Resources res = activity.getResources();
        mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        mSmallestWidthDp = getSmallestWidthDp(activity);

        // Android 15+ 优先使用 WindowInsets API
        if (VersionAdapter.isAndroid15OrAbove()) {
            initForAndroid15(activity);
        } else {
            initLegacy(activity);
        }

        mActionBarHeight = getActionBarHeight(activity);
    }

    /**
     * Android 15+ 使用 WindowInsets API 初始化
     */
    @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private void initForAndroid15(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        WindowMetrics metrics = wm.getCurrentWindowMetrics();
        WindowInsets insets = metrics.getWindowInsets();

        // 获取系统栏 insets
        mSystemBarsInsets = toAndroidXInsets(insets.getInsets(WindowInsets.Type.systemBars()));
        mNavigationBarsInsets = toAndroidXInsets(insets.getInsets(WindowInsets.Type.navigationBars()));
        mDisplayCutoutInsets = toAndroidXInsets(insets.getInsets(WindowInsets.Type.displayCutout()));

        // 状态栏高度优先从 insets 获取
        int statusBarHeight = mSystemBarsInsets.top;
        if (statusBarHeight <= 0) {
            // 兜底：使用传统方法
            statusBarHeight = getInternalDimensionSize(activity, IMMERSION_STATUS_BAR_HEIGHT);
        }
        mStatusBarHeight = statusBarHeight;

        // 导航栏高度和宽度
        int navHeight = mNavigationBarsInsets.bottom;
        int navWidth = Math.max(mNavigationBarsInsets.left, mNavigationBarsInsets.right);

        if (navHeight <= 0 && navWidth <= 0) {
            // 兜底：使用传统方法
            if (hasNavBar(activity)) {
                navHeight = getNavigationBarHeightInternal(activity);
                navWidth = getInternalDimensionSize(activity, IMMERSION_NAVIGATION_BAR_WIDTH);
            }
        }

        mNavigationBarHeight = navHeight;
        mNavigationBarWidth = navWidth;
        mHasNavigationBar = (navHeight > 0 || navWidth > 0);
    }

    /**
     * 转换为 AndroidX Insets
     */
    @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private Insets toAndroidXInsets(android.graphics.Insets platformInsets) {
        return Insets.of(
            platformInsets.left,
            platformInsets.top,
            platformInsets.right,
            platformInsets.bottom
        );
    }

    /**
     * 传统方法初始化（Android 14 及以下）
     */
    private void initLegacy(Activity activity) {
        mStatusBarHeight = getInternalDimensionSize(activity, IMMERSION_STATUS_BAR_HEIGHT);
        mNavigationBarHeight = getNavigationBarHeight(activity);
        mNavigationBarWidth = getNavigationBarWidth(activity);
        mHasNavigationBar = (mNavigationBarHeight > 0);

        // 初始化为 null
        mSystemBarsInsets = null;
        mDisplayCutoutInsets = null;
        mNavigationBarsInsets = null;
    }

    @TargetApi(14)
    private int getActionBarHeight(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            View actionBar = activity.getWindow().findViewById(R.id.action_bar_container);
            if (actionBar != null) {
                result = actionBar.getMeasuredHeight();
            }
            if (result == 0) {
                TypedValue tv = new TypedValue();
                activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
                result = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }
        }
        return result;
    }

    @TargetApi(14)
    private int getNavigationBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                return getNavigationBarHeightInternal(context);
            }
        }
        return result;
    }

    @TargetApi(14)
    private int getNavigationBarWidth(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                return getInternalDimensionSize(context, IMMERSION_NAVIGATION_BAR_WIDTH);
            }
        }
        return result;
    }

    @TargetApi(14)
    private boolean hasNavBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            GestureUtils.GestureBean gestureBean = GestureUtils.getGestureBean(activity);
            if (!gestureBean.checkNavigation && gestureBean.isGesture) {
                return false;
            }
        }
        //其他手机根据屏幕真实高度与显示高度是否相同来判断
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                        !key.equals(IMMERSION_STATUS_BAR_HEIGHT))) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    return (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
                }
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }

    @SuppressLint("NewApi")
    private float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    /**
     * Should a navigation bar appear at the bottom of the screen in the current
     * device configuration? A navigation bar may appear on the right side of
     * the screen in certain configurations.
     *
     * @return True if navigation should appear at the bottom of the screen, False otherwise.
     */
    boolean isNavigationAtBottom() {
        return (mSmallestWidthDp >= 600 || mInPortrait);
    }

    /**
     * Get the height of the system status bar.
     *
     * @return The height of the status bar (in pixels).
     */
    int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    /**
     * Get the height of the action bar.
     *
     * @return The height of the action bar (in pixels).
     */
    int getActionBarHeight() {
        return mActionBarHeight;
    }

    /**
     * Does this device have a system navigation bar?
     *
     * @return True if this device uses soft key navigation, False otherwise.
     */
    boolean hasNavigationBar() {
        return mHasNavigationBar;
    }

    /**
     * Get the height of the system navigation bar.
     *
     * @return The height of the navigation bar (in pixels). If the device does not have soft navigation keys, this will always return 0.
     */
    int getNavigationBarHeight() {
        return mNavigationBarHeight;
    }

    /**
     * Get the width of the system navigation bar when it is placed vertically on the screen.
     *
     * @return The width of the navigation bar (in pixels). If the device does not have soft navigation keys, this will always return 0.
     */
    int getNavigationBarWidth() {
        return mNavigationBarWidth;
    }

    /**
     * 获得导航栏高度
     *
     * @param context
     * @return
     */
    static int getNavigationBarHeightInternal(@NonNull Context context) {
        String key;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            key = IMMERSION_NAVIGATION_BAR_HEIGHT;
        } else {
            key = IMMERSION_NAVIGATION_BAR_HEIGHT_LANDSCAPE;
        }
        return getInternalDimensionSize(context, key);
    }

    static int getNavigationBarWidthInternal(@NonNull Context context) {
        return getInternalDimensionSize(context, IMMERSION_NAVIGATION_BAR_WIDTH);
    }

    // ============ Android 15+ 新增 getter 方法 ============

    /**
     * 获取系统栏 Insets (Android 15+)
     * 包含状态栏和导航栏的 insets
     *
     * @return System bars insets, null if Android < 15
     */
    Insets getSystemBarsInsets() {
        return mSystemBarsInsets;
    }

    /**
     * 获取刘海屏/打孔屏 Insets (Android 15+)
     *
     * @return Display cutout insets, null if Android < 15
     */
    Insets getDisplayCutoutInsets() {
        return mDisplayCutoutInsets;
    }

    /**
     * 获取导航栏 Insets (Android 15+)
     *
     * @return Navigation bars insets, null if Android < 15
     */
    Insets getNavigationBarsInsets() {
        return mNavigationBarsInsets;
    }
}