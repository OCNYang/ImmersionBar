# 构建配置优化建议

## 当前状态

### immersionbar/build.gradle
```gradle
compileSdkVersion 31
targetSdkVersion 31
minSdkVersion 14
```

### 已实现的适配
- ✅ Android 15 (API 35) Edge-to-Edge 支持
- ✅ WindowInsetsController API
- ✅ 使用硬编码整数常量解决 API 级别限制（如 TIRAMISU = 33）
- ✅ 完全向后兼容 Android 4.4+

## 构建配置建议

### 选项 1：保持当前配置（推荐用于稳定版本）

**优点：**
- ✅ 风险最小，已验证可以编译
- ✅ 使用硬编码常量可以支持 Android 15/16 功能
- ✅ 不会引入新的兼容性问题
- ✅ 适合当前稳定发布

**缺点：**
- ⚠️ 无法使用 Android 12+ 的新 API 常量
- ⚠️ IDE 可能显示部分 API 为未知

**使用场景：**
- 立即发布 Android 15 支持版本（v3.3.0）
- 最小化变更风险
- 保持最大兼容性

---

### 选项 2：升级到 compileSdk 35（推荐用于开发分支）

```gradle
android {
    namespace 'com.gyf.immersionbar'
    compileSdkVersion 35  // 升级到 Android 15

    defaultConfig {
        minSdkVersion 14
        targetSdkVersion 31  // 保持 31，避免强制 Edge-to-Edge
    }
}
```

**优点：**
- ✅ 可以使用 Android 15 的所有 API 常量
- ✅ 更好的 IDE 支持和代码提示
- ✅ 可以移除硬编码常量，使用 `Build.VERSION_CODES.VANILLA_ICE_CREAM`
- ✅ 不影响应用行为（targetSdk 仍为 31）

**缺点：**
- ⚠️ 需要 Android SDK Platform 35
- ⚠️ 可能触发新的 lint 警告

**需要的修改：**
1. VersionAdapter.java - 可以使用官方常量替代硬编码整数
2. 测试兼容性确保没有破坏性变更

**修改示例：**
```java
// 当前（使用硬编码）
public static boolean supportsPredictiveBack() {
    return Build.VERSION.SDK_INT >= 33; // TIRAMISU
}

// 升级后（使用官方常量）
public static boolean supportsPredictiveBack() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
}
```

---

### 选项 3：完全升级到 targetSdk 35（仅用于测试）

```gradle
android {
    namespace 'com.gyf.immersionbar'
    compileSdkVersion 35

    defaultConfig {
        minSdkVersion 14
        targetSdkVersion 35  // ⚠️ 强制启用 Edge-to-Edge
    }
}
```

**优点：**
- ✅ 完全测试 Android 15 强制 Edge-to-Edge 行为
- ✅ 符合未来 Google Play 要求

**缺点：**
- ❌ **破坏性变更**：所有应用都会强制启用 Edge-to-Edge
- ❌ 现有用户如果未更新代码，UI 可能错乱
- ❌ 不适合库项目（应由应用决定 targetSdk）

**不推荐用于库项目！** 仅用于 sample app 测试。

---

## 推荐的分阶段升级策略

### 阶段 1：v3.3.0（当前，稳定发布）
```gradle
compileSdkVersion 31
targetSdkVersion 31
```
- 发布当前的 Android 15 适配
- 使用硬编码常量
- 零破坏性变更

### 阶段 2：v3.4.0（下一个主版本）
```gradle
compileSdkVersion 35
targetSdkVersion 31  // 保持
```
- 升级 compileSdk 到 35
- 替换硬编码常量为官方常量
- 改进 IDE 支持
- 充分测试

### 阶段 3：sample app 测试分支
```gradle
// 仅在 immersionbar-sample/build.gradle 中
compileSdkVersion 35
targetSdkVersion 35
```
- 创建测试分支
- sample app 升级 targetSdk 到 35
- 验证强制 Edge-to-Edge 行为
- 收集测试反馈

---

## 依赖版本优化建议

### 当前依赖
```gradle
dependencies {
    compileOnly 'androidx.appcompat:appcompat:1.4.1'
}
```

### 可选升级（仅在升级 compileSdk 时）
```gradle
dependencies {
    compileOnly 'androidx.appcompat:appcompat:1.6.1'  // 更新版本

    // 如果使用 WindowInsetsCompat
    compileOnly 'androidx.core:core:1.12.0'
}
```

**注意：** 使用 `compileOnly` 确保不会强制应用使用特定版本。

---

## 测试清单

如果决定升级 compileSdk 到 35，需要测试：

### 编译测试
- [ ] `./gradlew clean build` - 所有模块编译成功
- [ ] 无新的编译错误
- [ ] 无新的严重 lint 警告

### 功能测试
- [ ] Android 4.4 - 传统模式工作正常
- [ ] Android 5-10 - SYSTEM_UI_FLAG 路径正常
- [ ] Android 11-14 - WindowInsetsController 路径正常
- [ ] Android 15 模拟器 - Edge-to-Edge 模式正常
- [ ] 所有公开 API 向后兼容

### 性能测试
- [ ] 库大小未显著增加
- [ ] 方法数未显著增加
- [ ] 运行时性能无退化

---

## 立即可执行的优化

即使保持当前的 compileSdk 31，也可以做这些优化：

### 1. 添加版本配置注释
```gradle
android {
    namespace 'com.gyf.immersionbar'

    // API 31: 支持 Android 12，同时通过硬编码常量支持 Android 15/16
    compileSdkVersion 31

    defaultConfig {
        minSdkVersion 14  // Android 4.4+
        targetSdkVersion 31  // 避免强制 Edge-to-Edge
    }
}
```

### 2. 改进 Proguard 规则（如果需要）
创建 `consumer-rules.pro`：
```proguard
# ImmersionBar - 保留公开 API
-keep public class com.gyf.immersionbar.ImmersionBar {
    public *;
}
-keep public interface com.gyf.immersionbar.OnInsetsChangeListener {
    *;
}
-keep public class com.gyf.immersionbar.VersionAdapter {
    public static *;
}

# 保留 BarConfig 的 Insets getter 方法
-keepclassmembers class com.gyf.immersionbar.BarConfig {
    public *** getSystemBarsInsets();
    public *** getDisplayCutoutInsets();
    public *** getNavigationBarsInsets();
}
```

### 3. 优化 lint 配置
```gradle
lintOptions {
    checkReleaseBuilds false
    abortOnError false

    // 忽略废弃 API 警告（我们已经通过 @SuppressWarnings 处理）
    disable 'Deprecated'

    // 如果使用了 NewApi 但有版本检查，可以忽略
    disable 'NewApi'
}
```

---

## 建议决策流程

```
是否立即发布 Android 15 支持？
├─ 是 → 选项 1（保持 compileSdk 31）
│   └─ 发布 v3.3.0，稳定可靠
│
└─ 否，可以再测试 1-2 周 → 选项 2（升级到 compileSdk 35）
    ├─ 创建新分支 `android-15-compile-sdk-35`
    ├─ 升级 compileSdk
    ├─ 替换硬编码常量
    ├─ 充分测试
    └─ 确认无问题后发布 v3.4.0
```

---

## 我的推荐

基于当前情况，我推荐：

**短期（v3.3.0 - 本月发布）：**
- ✅ 保持 `compileSdk 31`
- ✅ 使用当前的硬编码常量方案
- ✅ 专注于稳定性和向后兼容
- ✅ 快速响应 Android 15 发布

**中期（v3.4.0 - 1-2 个月后）：**
- ✅ 升级 `compileSdk` 到 35
- ✅ 替换硬编码常量为官方常量
- ✅ 改进代码质量和 IDE 支持
- ✅ 保持 `targetSdk 31` 确保兼容性

**sample app（测试分支）：**
- ✅ 创建独立测试分支
- ✅ sample app 升级 `targetSdk 35`
- ✅ 验证强制 Edge-to-Edge 行为
- ✅ 为用户提供最佳实践示例

---

**日期：** 2025-01-03
**当前版本：** v3.2.2
**计划版本：** v3.3.0 (Android 15 支持)
**状态：** 建议文档，等待决策
