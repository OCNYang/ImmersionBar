# Android 15/16 适配后的使用变化总结

## 📌 核心结论

### ✅ 对现有用户：**零变化，完全兼容**

**好消息**：您的现有代码**完全不需要修改**，库会自动适配 Android 15/16！

```java
// 原有代码继续工作，无需任何修改
ImmersionBar.with(this)
    .statusBarColor(R.color.colorPrimary)
    .navigationBarColor(R.color.colorPrimary)
    .init();
```

✅ Android 4.4-14：使用传统模式
✅ Android 15-16：自动使用新的 Edge-to-Edge 模式
✅ 100% 向后兼容，零破坏性变更

---

## 📊 使用场景分类

### 场景 1：完全不修改代码（推荐给大多数用户）

**适用对象**：
- 现有项目升级库版本
- 不需要特殊定制的应用
- 追求最简单的升级路径

**操作**：
```gradle
// 只需更新版本号
implementation 'com.geyifeng.immersionbar:immersionbar:3.3.0'
```

**结果**：
- ✅ 自动支持 Android 15/16
- ✅ 自动使用 Edge-to-Edge 模式（Android 15+）
- ✅ 完全兼容旧版本 Android
- ✅ 无需修改任何代码

---

### 场景 2：启用 Android 15 增强特性（推荐给新项目）

**适用对象**：
- 新项目
- 想要最佳 Android 15 体验
- 需要精确控制系统栏 insets

**代码变化**：

#### Java 使用方式
```java
ImmersionBar.with(this)
    .statusBarColor(Color.TRANSPARENT)  // 透明状态栏
    .navigationBarColor(Color.TRANSPARENT)  // 透明导航栏
    .statusBarDarkFont(true)

    // 🆕 新增：监听系统栏 insets 变化（Android 15+ 生效）
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        // 动态调整布局以适应系统栏
        findViewById(R.id.toolbar).setPadding(0, top, 0, 0);
        findViewById(R.id.content).setPadding(0, 0, 0, bottom);
    })

    .init();
```

#### Kotlin 使用方式
```kotlin
immersionBar {
    statusBarColor(Color.TRANSPARENT)
    navigationBarColor(Color.TRANSPARENT)
    statusBarDarkFont(true)

    // 🆕 新增：监听 insets 变化
    setOnInsetsChangeListener { top, bottom, left, right ->
        toolbar.updatePadding(top = top)
        content.updatePadding(bottom = bottom)
    }
}
```

**新增 API 说明**：
| API | 参数 | 说明 | 生效版本 |
|-----|------|------|----------|
| `setOnInsetsChangeListener()` | OnInsetsChangeListener | 监听系统栏 insets 变化 | Android 15+ |
| `edgeToEdgeEnabled()` | boolean | 启用/禁用 Edge-to-Edge | Android 15+ |
| `debugPrintVersionInfo()` | boolean | 打印版本适配信息 | All |
| `debugForceEdgeToEdge()` | boolean | 强制 Edge-to-Edge（测试用） | Android 11+ |

---

### 场景 3：调试和版本检测

**适用对象**：
- 开发测试阶段
- 需要了解适配行为
- 排查问题

**Java 示例**：
```java
// 1. 启用调试日志
ImmersionBar.with(this)
    .debugPrintVersionInfo(true)  // 🆕 打印版本信息到 Logcat
    .statusBarColor(R.color.colorPrimary)
    .init();

// Logcat 输出示例：
// D/ImmersionBar: Android 15+ Edge-to-Edge mode: Android 15.0 (API 35) - WindowInsetsController + Edge-to-Edge (enforced)

// 2. 版本检测
if (VersionAdapter.isAndroid15OrAbove()) {
    // Android 15+ 特殊处理
    Log.d("TAG", "Running on: " + VersionAdapter.getVersionInfo());
}
```

**Kotlin 扩展**：
```kotlin
import com.gyf.immersionbar.ktx.*

// 🆕 版本检测扩展
if (isAndroid15OrAbove) {
    Log.d("TAG", "Android 15+ detected")
    Log.d("TAG", "Version: $versionInfo")
    Log.d("TAG", "Approach: $recommendedApproach")
}
```

---

## 🔄 迁移指南

### 从旧版本升级到 3.3.0

#### 步骤 1：更新依赖
```gradle
dependencies {
    // 基础库
    implementation 'com.geyifeng.immersionbar:immersionbar:3.3.0'  // 升级版本号

    // Kotlin 扩展（可选）
    implementation 'com.geyifeng.immersionbar:immersionbar-ktx:3.3.0'
}
```

#### 步骤 2：代码适配（可选）

**选项 A：完全不修改**
```java
// 原有代码继续工作
ImmersionBar.with(this)
    .statusBarColor(R.color.colorPrimary)
    .init();
```

**选项 B：使用新特性（推荐用于 Android 15+）**
```java
ImmersionBar.with(this)
    .statusBarColor(Color.TRANSPARENT)
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        // 处理 insets
    })
    .init();
```

#### 步骤 3：布局调整（仅 Android 15+ 使用透明系统栏时）

**不推荐（Android 15+ Edge-to-Edge 模式）**：
```xml
<!-- ❌ 不要在根布局使用 fitsSystemWindows -->
<LinearLayout
    android:fitsSystemWindows="true">
</LinearLayout>
```

**推荐（使用 OnInsetsChangeListener 动态处理）**：
```xml
<!-- ✅ 通过代码动态设置 padding -->
<LinearLayout
    android:id="@+id/root">
</LinearLayout>
```

```java
// 代码中处理
.setOnInsetsChangeListener((top, bottom, left, right) -> {
    root.setPadding(0, top, 0, bottom);
})
```

---

## 📱 不同 Android 版本的行为

### Android 4.4 - 10 (API 19-29)
- 使用 SYSTEM_UI_FLAG_* 方式
- 传统颜色设置方式
- `setOnInsetsChangeListener()` **不生效**（无调用）

### Android 11 - 14 (API 30-34)
- 使用 WindowInsetsController API
- 更好的深色模式支持
- `setOnInsetsChangeListener()` **不生效**（仅 Android 15+）

### Android 15+ (API 35+)
- ✨ **强制 Edge-to-Edge 模式**（targetSdk 35+）
- 使用 WindowInsetsController API
- `setOnInsetsChangeListener()` **生效**
- 系统栏默认透明，内容延伸到系统栏后面

---

## 🎯 推荐用法对比

### 传统用法（继续支持）
```java
// ✅ Android 4.4 - 16 都可用
ImmersionBar.with(this)
    .statusBarColor(R.color.colorPrimary)      // 有背景色
    .navigationBarColor(R.color.colorPrimary)  // 有背景色
    .statusBarDarkFont(true)
    .fitsSystemWindows(true)  // 自动处理重叠
    .init();
```

**适用场景**：
- 不想改变现有 UI 设计
- 追求稳定性
- 兼容所有版本

### Edge-to-Edge 用法（推荐新项目）
```java
// ✅ 自动适配所有版本，Android 15+ 获得最佳体验
ImmersionBar.with(this)
    .statusBarColor(Color.TRANSPARENT)         // 透明
    .navigationBarColor(Color.TRANSPARENT)     // 透明
    .statusBarDarkFont(true)
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        // Android 15+ 会调用，动态调整布局
        // Android 14 及以下不会调用，但不影响功能
    })
    .init();
```

**适用场景**：
- 新项目
- 追求现代化 UI
- 想要 Android 15 最佳体验

---

## ⚠️ 常见问题

### Q1: 升级后 UI 会不会错乱？
**A**: 不会。库会自动检测 Android 版本并使用最合适的方式。

### Q2: 必须使用 `setOnInsetsChangeListener()` 吗？
**A**: 不必须。如果不使用，库会像以前一样工作。

### Q3: Android 15+ 上内容被状态栏遮挡了？
**A**: 有两种解决方案：

**方案 1：使用传统模式（最简单）**
```java
ImmersionBar.with(this)
    .statusBarColor(R.color.colorPrimary)  // 给状态栏一个颜色
    .fitsSystemWindows(true)
    .init();
```

**方案 2：使用 Edge-to-Edge 模式（推荐）**
```java
ImmersionBar.with(this)
    .statusBarColor(Color.TRANSPARENT)
    .setOnInsetsChangeListener((top, bottom, left, right) -> {
        findViewById(R.id.toolbar).setPadding(0, top, 0, 0);
    })
    .init();
```

### Q4: 如何知道当前使用的是哪种模式？
**A**: 启用调试日志：
```java
ImmersionBar.with(this)
    .debugPrintVersionInfo(true)
    .init();

// 查看 Logcat，会输出类似：
// D/ImmersionBar: Android 15+ Edge-to-Edge mode: ...
```

### Q5: 可以禁用 Android 15 的 Edge-to-Edge 吗？
**A**: 可以（不推荐）：
```java
ImmersionBar.with(this)
    .edgeToEdgeEnabled(false)  // 强制使用传统模式
    .statusBarColor(R.color.colorPrimary)
    .init();
```

---

## 📚 参考文档

- **技术详情**：[ANDROID_15_ADAPTATION.md](ANDROID_15_ADAPTATION.md)
- **完整示例**：[ANDROID_15_EXAMPLES.md](ANDROID_15_EXAMPLES.md)
- **构建配置**：[BUILD_OPTIMIZATION_NOTES.md](BUILD_OPTIMIZATION_NOTES.md)

---

## 🎉 总结

### 对于现有用户
✅ **完全不需要修改代码**
✅ **自动支持 Android 15/16**
✅ **100% 向后兼容**

### 对于新项目
✨ **可使用新的 Edge-to-Edge API**
✨ **获得最佳的 Android 15 体验**
✨ **仍然兼容所有 Android 版本**

### 关键点
1. **不修改代码也能工作** - 库会自动适配
2. **新 API 完全可选** - 想用就用，不用也没问题
3. **向后兼容 100%** - 从 Android 4.4 到 Android 16 全覆盖

---

**版本**: 3.3.0
**更新日期**: 2025-01-03
**Android 支持**: 4.4 - 16 (API 19 - 36)
