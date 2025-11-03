package com.gyf.immersionbar;

/**
 * WindowInsets 变化监听器
 * 用于监听系统栏 Insets 的变化，适配 Android 15 的 Edge-to-Edge 模式
 *
 * @author ImmersionBar Team
 * @date 2025/01/03
 */
public interface OnInsetsChangeListener {

    /**
     * 当系统栏 Insets 改变时回调
     * 在 Android 15+ 的 Edge-to-Edge 模式下，系统栏会自动透明，
     * 应用内容会延伸到系统栏后面，需要通过 Insets 来调整布局
     *
     * @param top    顶部 inset，通常是状态栏高度
     * @param bottom 底部 inset，通常是导航栏高度
     * @param left   左侧 inset，横屏时可能是导航栏宽度
     * @param right  右侧 inset，横屏时可能是导航栏宽度
     */
    void onInsetsChanged(int top, int bottom, int left, int right);
}
