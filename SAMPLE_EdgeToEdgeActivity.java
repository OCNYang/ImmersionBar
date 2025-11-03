package com.gyf.immersionbar.sample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.VersionAdapter;
import com.gyf.immersionbar.sample.R;

/**
 * Android 15+ Edge-to-Edge 示例 Activity
 *
 * 功能演示：
 * 1. 自动检测 Android 15+ 并启用 Edge-to-Edge 模式
 * 2. 使用 OnInsetsChangeListener 处理系统栏 insets
 * 3. 动态调整 Toolbar 和 BottomNavigationView 的边距
 * 4. 显示版本信息和调试日志
 *
 * @author ImmersionBar Team
 * @date 2025-01-03
 */
public class EdgeToEdgeActivity extends AppCompatActivity {

    private static final String TAG = "EdgeToEdgeActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;
    private TextView versionInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edge_to_edge);

        initViews();
        initImmersionBar();
        showVersionInfo();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        bottomNav = findViewById(R.id.bottom_nav);
        versionInfoText = findViewById(R.id.version_info_text);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 设置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setClipToPadding(false);  // 重要：允许内容滚动到 padding 区域

        // TODO: 设置 adapter
        // recyclerView.setAdapter(new YourAdapter());
    }

    private void initImmersionBar() {
        // 检测是否是 Android 15+
        if (VersionAdapter.isAndroid15OrAbove()) {
            Log.d(TAG, "Android 15+ detected, using Edge-to-Edge mode");
            initEdgeToEdgeMode();
        } else {
            Log.d(TAG, "Android < 15, using traditional mode");
            initTraditionalMode();
        }
    }

    /**
     * Android 15+ Edge-to-Edge 模式
     */
    private void initEdgeToEdgeMode() {
        ImmersionBar.with(this)
                .statusBarColor(Color.TRANSPARENT)
                .navigationBarColor(Color.TRANSPARENT)
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .edgeToEdgeEnabled(true)  // 显式启用（默认就是 true）
                .debugPrintVersionInfo(true)  // 启用调试日志
                .setOnInsetsChangeListener((top, bottom, left, right) -> {
                    // 处理系统栏 insets 变化
                    handleInsets(top, bottom, left, right);

                    Log.d(TAG, String.format("Insets changed: top=%d, bottom=%d, left=%d, right=%d",
                            top, bottom, left, right));
                })
                .init();
    }

    /**
     * 传统模式（Android 14 及以下）
     */
    private void initTraditionalMode() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.colorPrimary)
                .navigationBarColor(R.color.colorPrimary)
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .fitsSystemWindows(true)  // 传统方式处理重叠
                .init();
    }

    /**
     * 处理系统栏 insets
     * 这是 Android 15+ Edge-to-Edge 的核心逻辑
     */
    private void handleInsets(int top, int bottom, int left, int right) {
        // 1. Toolbar 添加顶部 padding（状态栏高度）
        toolbar.setPadding(
                toolbar.getPaddingLeft(),
                top,  // 状态栏高度
                toolbar.getPaddingRight(),
                toolbar.getPaddingBottom()
        );

        // 2. RecyclerView 添加顶部和底部 padding
        // 顶部：Toolbar 高度已经包含了状态栏，所以这里不需要额外 padding
        // 底部：导航栏高度 + BottomNavigationView 高度
        int bottomPadding = bottom;  // 导航栏高度
        recyclerView.setPadding(
                left,
                0,  // 顶部由 Toolbar 处理
                right,
                bottomPadding
        );

        // 3. BottomNavigationView 添加底部 padding（导航栏高度）
        bottomNav.setPadding(
                bottomNav.getPaddingLeft(),
                bottomNav.getPaddingTop(),
                bottomNav.getPaddingRight(),
                bottom  // 导航栏高度
        );
    }

    /**
     * 显示版本信息
     */
    private void showVersionInfo() {
        StringBuilder info = new StringBuilder();
        info.append("版本信息 / Version Info\n\n");
        info.append("当前系统: ").append(VersionAdapter.getVersionInfo()).append("\n");
        info.append("推荐方式: ").append(VersionAdapter.getRecommendedApproach()).append("\n\n");

        if (VersionAdapter.isAndroid15OrAbove()) {
            info.append("✅ 已启用 Edge-to-Edge 模式\n");
            info.append("• 系统栏透明\n");
            info.append("• 内容延伸到系统栏后面\n");
            info.append("• 通过 Insets 动态调整布局\n");
        } else {
            info.append("ℹ️ 使用传统模式\n");
            info.append("• 系统栏有背景色\n");
            info.append("• fitsSystemWindows 处理重叠\n");
        }

        info.append("\n特性支持:\n");
        info.append("• WindowInsetsController: ")
                .append(VersionAdapter.shouldUseWindowInsetsController() ? "✅" : "❌")
                .append("\n");
        info.append("• 预测性返回手势: ")
                .append(VersionAdapter.supportsPredictiveBack() ? "✅" : "❌")
                .append("\n");
        info.append("• 原生深色状态栏: ")
                .append(VersionAdapter.supportsNativeStatusBarDarkFont() ? "✅" : "❌")
                .append("\n");
        info.append("• 深色导航栏图标: ")
                .append(VersionAdapter.supportsNavigationBarDarkIcon() ? "✅" : "❌")
                .append("\n");
        info.append("• 刘海屏 API: ")
                .append(VersionAdapter.supportsDisplayCutout() ? "✅" : "❌")
                .append("\n");

        versionInfoText.setText(info.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
