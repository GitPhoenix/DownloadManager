package com.github.phoenix.utils;


import com.github.phoenix.helper.presenter.VersionUpdateImpl;

public class VersionUpdate {

    /**
     * 请求服务器，检查版本是否可以更新
     *
     * @param versionUpdate
     */
     public static void checkVersion(final VersionUpdateImpl versionUpdate) {
         //从网络请求获取到的APK下载路径，此处是随便找的链接
         versionUpdate.bindService("http://www.zhaoshangdai.com/file/android.apk");
     }
}
