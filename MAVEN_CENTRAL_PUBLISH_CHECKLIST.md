# Maven Central 发布配置检查报告

## 📋 检查概览

**检查日期**: 2025-01-03
**当前版本**: 3.2.2
**建议发布版本**: 3.3.0
**检查状态**: ⚠️ **需要配置签名凭证**

---

## ✅ 已就绪的配置

### 1. Maven 发布脚本 ✅

**文件**: `gradle/publish-mavencentral.gradle`

**配置完整性**: ✅ 完整

#### 核心配置

```gradle
// 库信息
groupId: com.geyifeng.immersionbar
artifactId: 动态（根据模块名）
version: 3.2.2 (从 immersionbar_version)

// 仓库地址
Release: https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
Snapshot: https://s01.oss.sonatype.org/content/repositories/snapshots/

// 开发者信息
ID: gyf-dev
Name: gyf-dev
Email: gyf.dev@gmail.com

// SCM
Connection: git@github.com:gyf-dev/ImmersionBar.git
URL: https://github.com/gyf-dev/ImmersionBar/tree/master

// License
Apache License 2.0
```

#### 发布的模块

✅ **immersionbar** - 核心库
✅ **immersionbar-ktx** - Kotlin 扩展
✅ **immersionbar-components** - Fragment 组件

❌ **immersionbar-sample** - 示例应用（不发布，正确）

---

### 2. Gradle 构建配置 ✅

**文件**: `build.gradle`

#### 版本定义

```gradle
ext.immersionbar_version = '3.2.2'  // ⚠️ 建议更新为 3.3.0
ext.kotlin_version = '1.9.22'
```

#### 自动应用发布脚本

```gradle
subprojects.forEach {
    ["com.android.library"].forEach { pluginId ->
        project.plugins.withId(pluginId) {
            project.afterEvaluate {
                def file = new File(project.projectDir.parent,
                    "gradle/publish-mavencentral.gradle")
                if (file.exists()) {
                    project.apply from: file
                }
            }
        }
    }
}
```

✅ 所有 library 模块会自动应用发布脚本

---

### 3. 发布任务验证 ✅

**可用的发布任务**:

```
Publishing tasks
----------------
✅ publishToMavenLocal - 发布到本地 Maven 仓库（测试用）
✅ publish - 发布所有 publication
✅ publishAllPublicationsToMavenRepository - 发布到 Maven Central
✅ publishUploadPublicationToMavenRepository - 发布指定模块
```

**测试结果**:

```bash
$ ./gradlew tasks --group publishing
BUILD SUCCESSFUL ✅

发布任务已正确配置
```

---

## ⚠️ 需要配置的项

### 1. 签名凭证配置 ⚠️

**状态**: ❌ **缺失**

**位置**: `local.properties`（或环境变量）

#### 需要添加的配置

在 `local.properties` 中添加以下配置：

```properties
# GPG 签名配置
signing.keyId=<GPG 密钥后 8 位>
signing.password=<GPG 密钥密码>
signing.secretKeyRingFile=<secring.gpg 文件路径>

# Sonatype 凭证
ossrhUsername=<Sonatype 用户名>
ossrhPassword=<Sonatype 密码或 Token>
```

#### 如何获取这些凭证

##### 1. GPG 签名密钥

**生成 GPG 密钥**:
```bash
# 生成密钥
gpg --gen-key

# 查看密钥列表
gpg --list-keys

# 导出密钥到服务器
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>

# 导出私钥（Gradle 需要）
gpg --export-secret-keys -o secring.gpg
```

**获取 keyId**:
```bash
# 列出密钥
gpg --list-keys

# 输出示例：
# pub   rsa3072 2025-01-03 [SC]
#       ABCDEF1234567890ABCDEF1234567890ABCDEF12
# uid           [ultimate] Your Name <your.email@example.com>

# keyId 是最后 8 位: ABCDEF12
```

##### 2. Sonatype 凭证

**注册账号**:
1. 访问 https://issues.sonatype.org/
2. 注册账号
3. 创建 Issue 申请 groupId：`com.geyifeng.immersionbar`
4. 等待审核通过（通常需要几小时到 1 天）

**生成 Token**（推荐）:
1. 登录 https://s01.oss.sonatype.org/
2. 点击右上角用户名 → Profile
3. User Token → Access User Token
4. 复制 Username 和 Password

---

### 2. 版本号更新 ⚠️

**当前版本**: 3.2.2
**建议版本**: 3.3.0

**原因**:
- ✅ Android 15/16 完整支持
- ✅ SDK 36 升级
- ✅ 新增 4 个公开 API
- ✅ Kotlin 扩展增强
- ✅ 重大功能更新

#### 更新方法

**文件**: `build.gradle`

```gradle
// 修改
ext.immersionbar_version = '3.2.2'

// 为
ext.immersionbar_version = '3.3.0'
```

---

## 📝 发布前检查清单

### 代码准备

- [x] ✅ 所有代码已提交到 Git
- [x] ✅ 所有模块编译成功
- [ ] ⏳ 已在真机上测试（建议）
- [ ] ⏳ 已更新 CHANGELOG.md（建议）
- [ ] ⏳ 已更新 README.md 中的版本号（建议）

### 版本配置

- [ ] ❌ 更新 `immersionbar_version` 到 3.3.0
- [x] ✅ compileSdk 和 targetSdk 已升级到 36
- [x] ✅ 所有 API 常量已使用官方常量

### 发布配置

- [x] ✅ Maven 发布脚本存在且完整
- [x] ✅ 发布任务可用
- [ ] ❌ GPG 签名密钥已配置
- [ ] ❌ Sonatype 凭证已配置
- [ ] ❌ groupId 已在 Sonatype 获得授权

### 文档准备

- [x] ✅ README.md 已更新 Android 15/16 支持
- [ ] ⏳ CHANGELOG.md 已创建（建议）
- [x] ✅ 技术文档已完成
  - [x] ANDROID_15_ADAPTATION.md
  - [x] ANDROID_15_EXAMPLES.md
  - [x] USAGE_CHANGES_SUMMARY.md
  - [x] SDK_36_UPGRADE_REPORT.md

---

## 🚀 发布流程

### 阶段 1: 准备工作

#### 1.1 配置签名凭证

**创建/编辑** `local.properties`:

```properties
# Android SDK 路径（已存在）
sdk.dir=/Users/lucas/Library/Android/sdk

# 添加以下内容
# GPG 签名配置
signing.keyId=ABCDEF12
signing.password=your_gpg_password
signing.secretKeyRingFile=/Users/lucas/.gnupg/secring.gpg

# Sonatype 凭证
ossrhUsername=your_sonatype_username
ossrhPassword=your_sonatype_password_or_token
```

⚠️ **重要**: `local.properties` 已在 `.gitignore` 中，不会提交到 Git

#### 1.2 更新版本号

**文件**: `build.gradle`

```gradle
ext.immersionbar_version = '3.3.0'
```

#### 1.3 创建 Git Tag

```bash
git add .
git commit -m "Release v3.3.0

- Android 15/16 完整支持
- SDK 36 升级
- 新增 Edge-to-Edge API
- Kotlin 扩展增强

🎉 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>"

git tag -a v3.3.0 -m "Release v3.3.0

Features:
- Android 15/16 Edge-to-Edge support
- SDK 36 upgrade
- New OnInsetsChangeListener API
- Kotlin version detection extensions

Build:
- Gradle 8.5, AGP 8.2.2, Kotlin 1.9.22
- compileSdk 36, targetSdk 36

Full backward compatibility maintained (Android 4.4+)"
```

---

### 阶段 2: 测试发布

#### 2.1 本地 Maven 测试

```bash
# 清理构建
./gradlew clean

# 发布到本地 Maven（~/.m2/repository）
./gradlew publishToMavenLocal

# 检查结果
ls -la ~/.m2/repository/com/geyifeng/immersionbar/
```

**预期输出**:
```
~/.m2/repository/com/geyifeng/immersionbar/
├── immersionbar/3.3.0/
│   ├── immersionbar-3.3.0.aar
│   ├── immersionbar-3.3.0.aar.asc  (GPG 签名)
│   ├── immersionbar-3.3.0.pom
│   ├── immersionbar-3.3.0.pom.asc
│   ├── immersionbar-3.3.0-sources.jar
│   └── immersionbar-3.3.0-sources.jar.asc
├── immersionbar-ktx/3.3.0/
│   └── ... (同上)
└── immersionbar-components/3.3.0/
    └── ... (同上)
```

#### 2.2 验证签名

```bash
# 验证 AAR 签名
gpg --verify ~/.m2/repository/com/geyifeng/immersionbar/immersionbar/3.3.0/immersionbar-3.3.0.aar.asc

# 预期输出：
# gpg: Good signature from "Your Name <your.email@example.com>"
```

---

### 阶段 3: 发布到 Maven Central

#### 3.1 发布到 Staging 仓库

```bash
# 清理并构建
./gradlew clean build -x test

# 发布所有模块
./gradlew publish

# 或单独发布
./gradlew :immersionbar:publish
./gradlew :immersionbar-ktx:publish
./gradlew :immersionbar-components:publish
```

**预期输出**:
```
> Task :immersionbar:publishUploadPublicationToMavenRepository
> Task :immersionbar-ktx:publishUploadPublicationToMavenRepository
> Task :immersionbar-components:publishUploadPublicationToMavenRepository

BUILD SUCCESSFUL
```

#### 3.2 在 Sonatype 验证

1. 登录 https://s01.oss.sonatype.org/
2. 点击左侧 **Staging Repositories**
3. 搜索 `comgeyifeng` 或 `immersionbar`
4. 找到你的 staging repository（通常是 `comgeyifeng-xxxx`）

#### 3.3 Close Staging Repository

1. 选中你的 staging repository
2. 点击 **Close** 按钮
3. 等待验证完成（2-10 分钟）
4. 检查 Activity 标签页，确认无错误

**常见验证项**:
- ✅ Signature Validation - GPG 签名验证
- ✅ POM Validation - POM 文件完整性
- ✅ javadoc.jar 存在
- ✅ sources.jar 存在

#### 3.4 Release 到 Maven Central

1. 验证通过后，选中 staging repository
2. 点击 **Release** 按钮
3. 确认释放

**同步时间**:
- Maven Central 索引更新: 2-4 小时
- 搜索可见: 24-48 小时
- 完全同步到所有镜像: 1-3 天

#### 3.5 验证发布

**Maven Central 搜索**:
- https://search.maven.org/artifact/com.geyifeng.immersionbar/immersionbar

**Gradle 使用**:
```gradle
dependencies {
    implementation 'com.geyifeng.immersionbar:immersionbar:3.3.0'
    implementation 'com.geyifeng.immersionbar:immersionbar-ktx:3.3.0'
    implementation 'com.geyifeng.immersionbar:immersionbar-components:3.3.0'
}
```

---

### 阶段 4: 发布后工作

#### 4.1 推送到 GitHub

```bash
# 推送代码和标签
git push origin master
git push origin v3.3.0
```

#### 4.2 创建 GitHub Release

1. 访问 https://github.com/gyf-dev/ImmersionBar/releases
2. 点击 **Draft a new release**
3. 选择 tag: `v3.3.0`
4. 标题: `ImmersionBar v3.3.0 - Android 15/16 Support`
5. 描述: 使用 CHANGELOG 内容
6. 点击 **Publish release**

#### 4.3 通知用户

- 在 GitHub Release 中说明变更
- 更新 README.md badge 版本
- （可选）发布博客文章

---

## ⚠️ 常见问题与解决方案

### 问题 1: GPG 签名失败

**错误信息**:
```
gpg: signing failed: Inappropriate ioctl for device
```

**解决方案**:
```bash
export GPG_TTY=$(tty)
```

或在 `~/.bashrc` / `~/.zshrc` 中添加：
```bash
export GPG_TTY=$(tty)
```

---

### 问题 2: signing.secretKeyRingFile 不存在

**错误信息**:
```
secring.gpg does not exist
```

**解决方案**:
```bash
# 导出私钥
gpg --export-secret-keys -o ~/.gnupg/secring.gpg

# 或使用新格式
gpg --export-secret-keys -o secring.gpg <KEY_ID>
```

---

### 问题 3: 401 Unauthorized

**错误信息**:
```
Could not PUT ... Received status code 401 from server: Unauthorized
```

**解决方案**:
1. 检查 `ossrhUsername` 和 `ossrhPassword` 是否正确
2. 确认使用的是 User Token 而不是账号密码（推荐）
3. 检查 groupId 是否已获得授权

---

### 问题 4: Close Staging Repository 失败

**可能原因**:
- ❌ javadoc.jar 缺失
- ❌ sources.jar 缺失
- ❌ GPG 签名无效
- ❌ POM 文件不完整

**解决方案**:
1. 查看 Activity 标签页的具体错误
2. 修复问题后 Drop repository
3. 重新执行 publish

---

## 📊 发布配置总结

### 完整性评分

| 检查项 | 状态 | 完成度 |
|--------|------|--------|
| **Maven 发布脚本** | ✅ 完整 | 100% |
| **Gradle 构建配置** | ✅ 完整 | 100% |
| **发布任务可用** | ✅ 可用 | 100% |
| **签名配置** | ❌ 缺失 | 0% |
| **Sonatype 凭证** | ❌ 缺失 | 0% |
| **版本号更新** | ⏳ 待更新 | 50% |
| **文档准备** | ✅ 完整 | 95% |
| **总体就绪度** | ⚠️ 需配置 | **70%** |

---

## ✅ 立即可执行的操作

### 不需要凭证的准备工作

1. ✅ **更新版本号** - 可立即执行
   ```gradle
   ext.immersionbar_version = '3.3.0'
   ```

2. ✅ **创建 CHANGELOG.md** - 可立即执行
   ```bash
   # 创建变更日志文件
   ```

3. ✅ **测试本地构建** - 可立即执行
   ```bash
   ./gradlew clean build -x test
   ```

4. ✅ **提交代码** - 可立即执行
   ```bash
   git add .
   git commit -m "Release v3.3.0"
   git tag v3.3.0
   ```

### 需要凭证的操作

5. ⏳ **配置 GPG 签名** - 需要生成 GPG 密钥
6. ⏳ **配置 Sonatype** - 需要注册账号并获得授权
7. ⏳ **发布到 Maven Central** - 需要完成上述配置

---

## 🎯 推荐的发布策略

### 策略 A: 立即发布（推荐）

**前提条件**: 已有 GPG 密钥和 Sonatype 凭证

1. 更新版本号到 3.3.0
2. 配置签名凭证到 `local.properties`
3. 测试本地发布
4. 发布到 Maven Central
5. 创建 GitHub Release

**预计耗时**: 2-4 小时（主要是等待 Maven Central 同步）

---

### 策略 B: 分阶段发布

**第一阶段（立即）**:
1. ✅ 更新版本号
2. ✅ 创建 CHANGELOG
3. ✅ 提交代码到 GitHub
4. ✅ 创建 GitHub Release（标记为 Pre-release）

**第二阶段（准备好凭证后）**:
1. ⏳ 配置 GPG 签名
2. ⏳ 配置 Sonatype
3. ⏳ 发布到 Maven Central
4. ⏳ 更新 GitHub Release 为正式版本

**优点**:
- 立即向用户展示新版本
- 用户可以通过 GitHub Release 下载 AAR
- 有更多时间准备 Maven Central 发布

---

## 📋 总结

### 当前状态

✅ **技术准备完成**:
- 所有代码已完成
- 编译构建成功
- 发布脚本完整
- 文档体系完整

⚠️ **需要配置**:
- GPG 签名密钥
- Sonatype 凭证
- 版本号更新

### 建议操作

1. **立即执行**:
   - 更新版本号到 3.3.0
   - 创建 CHANGELOG.md
   - 提交代码并创建 tag

2. **准备凭证**:
   - 生成或准备 GPG 密钥
   - 注册 Sonatype 账号（如未注册）
   - 申请 groupId 授权（如未申请）

3. **发布流程**:
   - 配置 local.properties
   - 测试本地发布
   - 发布到 Maven Central
   - 创建 GitHub Release

### 预计时间表

- **代码准备**: ✅ 已完成
- **凭证配置**: 1-2 天（如需注册和授权）
- **发布执行**: 2-4 小时
- **Maven Central 同步**: 1-3 天

**总计**: 最快 1 天，最慢 7 天

---

**检查完成日期**: 2025-01-03
**项目版本**: v3.2.2 → v3.3.0 (待发布)
**发布就绪度**: 70% ⚠️
**阻塞项**: GPG 签名和 Sonatype 凭证配置
**建议操作**: 先更新版本号并提交代码，同时准备发布凭证
