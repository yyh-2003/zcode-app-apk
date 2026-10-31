
## 免安装 Android Studio：GitHub Actions 云端编译

1. 注册 github.com 账号，新建一个仓库（如 zcode-app）
2. 解压本工程，把 ZCodeConnector 文件夹里的**所有文件**拖进仓库页面的 "uploading an existing file" 上传（.github 文件夹也要传，里面有编译脚本）
3. 上传完成后，点仓库顶部 **Actions** 标签 → 左侧 "Build APK" → 右侧 **Run workflow** 手动触发
4. 等几分钟变成绿色对勾后，点进那次运行，页面底部 **Artifacts** 里下载 zcode-apk.zip，解压得到 app-debug.apk
5. 传到手机安装即可（Debug签名仅供自用没问题）

注意：GitHub 网页上传不支持空文件夹，res/drawable 若为空可忽略。
