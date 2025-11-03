package com.gyf.immersionbar;

import android.os.Build;

/**
 * Android 版本适配工具类
 * 用于统一管理不同 Android 版本的适配逻辑
 *
 * @author ImmersionBar Team
 * @date 2025/01/03
 */
public class VersionAdapter {

    /**
     * Android 15 (Vanilla Ice Cream)
     * 使用官方常量 Build.VERSION_CODES.VANILLA_ICE_CREAM
     */
    public static final int ANDROID_15 = Build.VERSION_CODES.VANILLA_ICE_CREAM;

    /**
     * Android 16 (Baklava)
     * 使用官方常量 Build.VERSION_CODES.BAKLAVA
     */
    public static final int ANDROID_16 = Build.VERSION_CODES.BAKLAVA;

    /**
     * 是否是 Android 15 或更高版本
     *
     * @return true if Android 15+
     */
    public static boolean isAndroid15OrAbove() {
        return Build.VERSION.SDK_INT >= ANDROID_15;
    }

    /**
     * 是否是 Android 16 或更高版本
     *
     * @return true if Android 16+
     */
    public static boolean isAndroid16OrAbove() {
        return Build.VERSION.SDK_INT >= ANDROID_16;
    }

    /**
     * 是否是 Android 11 (R) 或更高版本
     * Android 11+ 引入了 WindowInsetsController
     *
     * @return true if Android 11+
     */
    public static boolean isAndroid11OrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    /**
     * 是否是 Android 10 (Q) 或更高版本
     *
     * @return true if Android 10+
     */
    public static boolean isAndroid10OrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 是否强制启用 Edge-to-Edge
     * Android 15+ 且 targetSdkVersion >= 35 的应用会强制启用
     *
     * @param targetSdkVersion 应用的 targetSdkVersion
     * @return true if edge-to-edge is enforced
     */
    public static boolean isEdgeToEdgeEnforced(int targetSdkVersion) {
        return isAndroid15OrAbove() && targetSdkVersion >= ANDROID_15;
    }

    /**
     * 是否应该使用 WindowInsetsController
     * Android 11+ 开始推荐使用，Android 15+ 必须使用
     *
     * @return true if should use WindowInsetsController
     */
    public static boolean shouldUseWindowInsetsController() {
        return isAndroid11OrAbove();
    }

    /**
     * 是否支持预测性返回手势
     * Android 13+ 开始支持
     *
     * @return true if supports predictive back
     */
    public static boolean supportsPredictiveBack() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * 是否支持状态栏深色字体
     * Android 6.0+ 支持原生深色字体
     * 6.0 以下需要使用厂商特殊方法（MIUI、Flyme OS）
     *
     * @return true if supports native dark font
     */
    public static boolean supportsNativeStatusBarDarkFont() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否支持导航栏深色图标
     * Android 8.0+ 支持
     *
     * @return true if supports dark navigation icon
     */
    public static boolean supportsNavigationBarDarkIcon() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 是否支持刘海屏 API
     * Android 9.0+ 支持官方 API
     *
     * @return true if supports display cutout API
     */
    public static boolean supportsDisplayCutout() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 获取推荐的系统栏控制方式描述
     *
     * @return 描述字符串
     */
    public static String getRecommendedApproach() {
        if (isAndroid15OrAbove()) {
            return "WindowInsetsController + Edge-to-Edge (enforced)";
        } else if (isAndroid11OrAbove()) {
            return "WindowInsetsController (recommended)";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return "SYSTEM_UI_FLAG (legacy)";
        } else {
            return "Translucent flags (KitKat)";
        }
    }

    /**
     * 调试信息：打印当前设备的 Android 版本信息
     *
     * @return 版本信息字符串
     */
    public static String getVersionInfo() {
        return "Android " + Build.VERSION.RELEASE +
                " (API " + Build.VERSION.SDK_INT + ")" +
                " - " + getRecommendedApproach();
    }
}
