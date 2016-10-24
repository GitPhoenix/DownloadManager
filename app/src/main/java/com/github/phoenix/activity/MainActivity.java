package com.github.phoenix.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.github.phoenix.R;
import com.github.phoenix.helper.presenter.VersionUpdateImpl;
import com.github.phoenix.service.DownloadService;
import com.github.phoenix.utils.Constant;
import com.github.phoenix.utils.LogUtil;
import com.github.phoenix.utils.MToast;
import com.github.phoenix.utils.SPUtil;
import com.github.phoenix.utils.VersionUpdate;
import com.github.phoenix.widget.NumberProgressBar;

import java.io.File;

/**
 * 模拟版本更新进度
 *
 * @author Phoenix
 * @date 2016-10-24 16:58
 */
public class MainActivity extends AppCompatActivity implements VersionUpdateImpl {
    private String TAG = this.getClass().getSimpleName();

    private NumberProgressBar bnp;

    private boolean isBindService;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            DownloadService downloadService = binder.getService();

            //接口回调，下载进度
            downloadService.setOnProgressListener(new DownloadService.OnProgressListener() {
                @Override
                public void onProgress(float fraction) {
                    LogUtil.i(TAG, "下载进度：" + fraction);
                    bnp.setProgress((int)(fraction * 100));

                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                    if (fraction == DownloadService.UNBIND_SERVICE && isBindService) {
                        unbindService(conn);
                        isBindService = false;
                        MToast.shortToast("下载完成！");
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        removeOldApk();

        VersionUpdate.checkVersion(this);
    }

    /**
     * 初始化View
     */
    private void initView() {
        bnp = (NumberProgressBar) findViewById(R.id.number_bar);
    }

    @Override
    public void bindService(String apkUrl) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, apkUrl);
        isBindService = bindService(intent, conn, BIND_AUTO_CREATE);
    }

    /**
     * 删除上次更新存储在本地的apk
     */
    private void removeOldApk() {
        //获取老ＡＰＫ的存储路径
        File fileName = new File(SPUtil.getString(Constant.SP_DOWNLOAD_PATH, ""));
        LogUtil.i(TAG, "老APK的存储路径 =" + SPUtil.getString(Constant.SP_DOWNLOAD_PATH, ""));

        if (fileName != null && fileName.exists() && fileName.isFile()) {
            fileName.delete();
            LogUtil.i(TAG, "存储器内存在老APK，进行删除操作");
        }
    }
}
