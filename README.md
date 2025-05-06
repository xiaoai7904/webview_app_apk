# WebView App

这是一个简单的Android应用，它使用WebView加载从API获取的URL。

## 功能

- 从API获取URL
- 在WebView中加载获取到的URL
- 如果无法获取URL，则加载默认页面

## 构建和运行

1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 点击运行按钮或使用命令行执行`./gradlew assembleDebug`来构建APK
4. 生成的APK将位于`app/build/outputs/apk/debug/`目录下

## 注意事项

- 请在使用前将MainActivity.java中的API_URL替换为实际的API地址
- 应用需要网络权限才能正常工作


keystore 密码：xiaoai123
key 密码：密码：xiaoai123
key 别名：androidkey 

### 打包命令

 ./gradlew assembleRelease

 $ANDROID_HOME/build-tools/30.0.3/zipalign -v 4 app/build/outputs/apk/release/app_v1.0.0_release.apk app/build/outputs/apk/release/app_v1.0.0_release_aligned.apk

 $ANDROID_HOME/build-tools/30.0.3/apksigner sign --ks /Users/admin/apptest/keystore/release.keystore --ks-key-alias androidkey --ks-pass pass:android --key-pass pass:android app/build/outputs/apk/release/app-release-aligned.apk