# JitPack 发布指南

## 📋 概览

ImmersionBar 现在使用 **JitPack** 进行发布，这是一个更简单、无需配置的 Maven 仓库服务。

**仓库地址**: https://github.com/OCNYang/ImmersionBar
**JitPack 页面**: https://jitpack.io/#OCNYang/ImmersionBar

---

## ✅ 为什么选择 JitPack？

相比 Maven Central，JitPack 有以下优势：

| 特性 | JitPack | Maven Central |
|------|---------|---------------|
| **配置复杂度** | 极简 | 复杂 |
| **签名要求** | 不需要 | 需要 GPG |
| **账号申请** | 不需要 | 需要审核 |
| **发布速度** | 即时 | 2-4 小时 |
| **自动构建** | ✅ | ❌ |
| **版本管理** | Git Tag | 手动上传 |
| **发布流程** | 1 步 | 5+ 步 |

---

## 🚀 发布流程（超简单）

### 完整流程只需 3 步！

#### 步骤 1: 更新版本号

**文件**: `build.gradle`

```gradle
ext.immersionbar_version = '3.3.0'  // 更新版本号
```

#### 步骤 2: 提交代码并创建 Tag

```bash
# 提交代码
git add .
git commit -m "Release v3.3.0

- Android 15/16 完整支持
- SDK 36 升级
- 新增 Edge-to-Edge API
- Kotlin 扩展增强"

# 创建 Git Tag（版本号）
git tag -a 3.3.0 -m "Release v3.3.0"

# 推送到 GitHub
git push origin master
git push origin 3.3.0
```

#### 步骤 3: JitPack 自动构建

**无需任何额外操作！**

1. JitPack 会自动检测到新 Tag
2. 自动从 GitHub 拉取代码
3. 自动执行 Gradle 构建
4. 自动发布到 JitPack 仓库

**构建状态查看**: https://jitpack.io/#OCNYang/ImmersionBar/3.3.0

**就这么简单！** 🎉

---

## 📦 用户如何使用

### 添加 JitPack 仓库

**项目级 build.gradle**:

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // 添加 JitPack
    }
}
```

或者（Gradle 7.0+）在 **settings.gradle** 中：

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 添加依赖

**模块级 build.gradle**:

```gradle
dependencies {
    // 核心库
    implementation 'com.github.OCNYang.ImmersionBar:immersionbar:3.3.0'

    // Kotlin 扩展（可选）
    implementation 'com.github.OCNYang.ImmersionBar:immersionbar-ktx:3.3.0'

    // Fragment 组件（可选）
    implementation 'com.github.OCNYang.ImmersionBar:immersionbar-components:3.3.0'
}
```

### 使用最新版本

```gradle
dependencies {
    // 使用最新 commit
    implementation 'com.github.OCNYang:ImmersionBar:master-SNAPSHOT'

    // 使用最新 tag
    implementation 'com.github.OCNYang:ImmersionBar:latest.release'
}
```

---

## 🔍 版本管理

### 版本号规范

JitPack 使用 Git Tag 作为版本号，推荐使用 **语义化版本**：

```
主版本号.次版本号.修订号

例如：
3.3.0 - 新功能版本（Android 15/16 支持）
3.3.1 - Bug 修复版本
3.4.0 - 下一个功能版本
```

### 创建版本的方式

#### 1. 使用 Git Tag（推荐）

```bash
# 创建 annotated tag
git tag -a 3.3.0 -m "Release v3.3.0"
git push origin 3.3.0
```

**JitPack 地址**: `com.github.OCNYang:ImmersionBar:3.3.0`

#### 2. 使用 GitHub Release

在 GitHub 上创建 Release，JitPack 会自动识别。

#### 3. 使用 Commit Hash

```gradle
implementation 'com.github.OCNYang:ImmersionBar:abc1234'  // commit hash
```

#### 4. 使用分支

```gradle
implementation 'com.github.OCNYang:ImmersionBar:master-SNAPSHOT'
```

---

## 📊 JitPack 配置详解

### 当前配置

**文件**: `gradle/publish-jitpack.gradle`

```gradle
apply plugin: 'maven-publish'

afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release

                groupId = 'com.github.OCNYang'
                artifactId = project.name
                version = immersionbar_version
            }
        }
    }
}
```

### 配置说明

- **groupId**: `com.github.OCNYang` - GitHub 用户名
- **artifactId**: 自动使用模块名
  - `immersionbar`
  - `immersionbar-ktx`
  - `immersionbar-components`
- **version**: 从 `build.gradle` 的 `immersionbar_version` 读取

### 模块应用配置

每个 library 模块的 `build.gradle` 都包含：

```gradle
apply from: "${rootProject.projectDir}/gradle/publish-jitpack.gradle"
```

---

## 🎯 版本发布检查清单

### 发布前检查

- [ ] 更新 `immersionbar_version` 到新版本
- [ ] 所有代码已提交到 Git
- [ ] 所有模块编译成功 (`./gradlew build`)
- [ ] 已更新 README.md（如有需要）
- [ ] 已更新 CHANGELOG.md（如有需要）

### 发布步骤

```bash
# 1. 更新版本号（build.gradle）
ext.immersionbar_version = '3.3.0'

# 2. 提交代码
git add .
git commit -m "Release v3.3.0"

# 3. 创建 Tag
git tag -a 3.3.0 -m "Release v3.3.0"

# 4. 推送
git push origin master
git push origin 3.3.0
```

### 发布后验证

1. **检查 JitPack 构建状态**
   - 访问: https://jitpack.io/#OCNYang/ImmersionBar/3.3.0
   - 状态应为 ✅ "Build passing"

2. **测试依赖可用性**
   ```gradle
   implementation 'com.github.OCNYang.ImmersionBar:immersionbar:3.3.0'
   ```

3. **创建 GitHub Release**（可选）
   - 在 GitHub 上基于 Tag 创建 Release
   - 添加版本说明

---

## 🔧 高级功能

### 1. 构建配置文件（可选）

创建 `jitpack.yml` 在项目根目录：

```yaml
# 指定 JDK 版本
jdk:
  - openjdk21

# 自定义构建命令
install:
  - ./gradlew clean build publishToMavenLocal -x test
```

当前项目**不需要**此文件，默认配置即可。

### 2. 排除某些模块

如果不想发布某个模块，在其 `build.gradle` 中移除：

```gradle
// 移除这一行
apply from: "${rootProject.projectDir}/gradle/publish-jitpack.gradle"
```

### 3. 查看构建日志

访问: https://jitpack.io/com/github/OCNYang/ImmersionBar/3.3.0/build.log

---

## 📝 README 更新建议

建议在 README.md 中添加 JitPack badge 和使用说明：

```markdown
[![](https://jitpack.io/v/OCNYang/ImmersionBar.svg)](https://jitpack.io/#OCNYang/ImmersionBar)

## 添加依赖

**Step 1.** 添加 JitPack 仓库到项目级 build.gradle:

\```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
\```

**Step 2.** 添加依赖:

\```gradle
dependencies {
    implementation 'com.github.OCNYang.ImmersionBar:immersionbar:3.3.0'
    implementation 'com.github.OCNYang.ImmersionBar:immersionbar-ktx:3.3.0'
    implementation 'com.github.OCNYang.ImmersionBar:immersionbar-components:3.3.0'
}
\```
```

---

## ⚠️ 注意事项

### 1. groupId 必须匹配 GitHub 路径

```
GitHub: https://github.com/OCNYang/ImmersionBar
JitPack groupId: com.github.OCNYang
```

### 2. Tag 命名规范

推荐使用纯数字版本号：
- ✅ `3.3.0`
- ✅ `v3.3.0`
- ❌ `release-3.3.0`（不推荐，但可用）

### 3. 首次构建可能较慢

JitPack 首次构建可能需要 2-5 分钟，后续构建会使用缓存。

### 4. 构建失败处理

如果构建失败：
1. 检查构建日志
2. 确保本地 `./gradlew build` 成功
3. 检查 JDK 版本兼容性
4. 删除 Tag 并重新创建

---

## 🆚 与 Maven Central 对比

### Maven Central 发布流程

```
1. 生成 GPG 密钥
2. 注册 Sonatype 账号
3. 申请 groupId 授权（1-2 天）
4. 配置签名和凭证
5. 执行 publish 命令
6. 登录 Sonatype 网站
7. Close staging repository
8. 验证通过
9. Release 到 Central
10. 等待同步（1-3 天）
```

**总耗时**: 3-5 天

### JitPack 发布流程

```
1. 创建 Git Tag
2. 推送到 GitHub
3. （自动完成）
```

**总耗时**: 2-5 分钟

---

## 🎉 总结

### JitPack 优势

✅ **零配置** - 不需要 GPG、不需要账号
✅ **即时发布** - 推送 Tag 即发布
✅ **自动构建** - GitHub + JitPack 自动化
✅ **版本管理** - Git Tag 即版本号
✅ **完全免费** - 开源项目永久免费

### 发布流程总结

```bash
# 1. 更新版本号
vim build.gradle

# 2. 提交并创建 Tag
git commit -am "Release v3.3.0"
git tag 3.3.0
git push origin master 3.3.0

# 完成！
```

**就这么简单！** 🚀

---

## 📚 参考资料

- **JitPack 官网**: https://jitpack.io/
- **JitPack 文档**: https://jitpack.io/docs/
- **本项目 JitPack 页面**: https://jitpack.io/#OCNYang/ImmersionBar
- **GitHub 仓库**: https://github.com/OCNYang/ImmersionBar

---

**更新日期**: 2025-01-03
**当前版本**: 3.3.0
**发布方式**: JitPack (GitHub Tag)
