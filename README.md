# DownloadManager
* DownloadManager简介<br>
DownloadManager是[Android](http://lib.csdn.net/base/android) 2.3（API level 9）用系统服务(Service)的方式提供了DownloadManager来处理长时间的下载操作。它包含两个静态内部类**DownloadManager.Query(用来查询下载信息)**和**DownloadManager.Request(用来请求一个下载)**。
<br>DownloadManager主要提供了下面几个方法：
<br>**public long enqueue(Request request)**把任务加入下载队列并返回downloadId，以便后面用于查询下载信息。若网络不满足条件、Sdcard挂载中、超过最大并发数等异常会等待下载，正常则直接下载。
<br>**public int remove(long… ids)**删除下载，若取消下载，会同时删除下载文件和记录。
<br>**public Cursor query(Query query)**查询下载信息，包括下载文件总大小，已经下载的大小以及下载状态等。

* ContentObserver简介<br>
**public void ContentObserver(Handler handler)** 所有ContentObserver的派生类都需要调用该构造方法，参数：handler Handler对象用于在主线程中修改UI。
<br>**public void onChange(boolean selfChange)**当观察到的Uri中内容发生变化时，就会回调该方法。所有ContentObserver的派生类都需要重载该方法去处理逻辑。
<br>观察特定Uri的步骤如下：
 <br>1、创建我们特定的ContentObserver派生类，必须重载父类构造方法，必须重载onChange()方法去处理回调后的功能实现。
 <br>2、为指定的Uri注册一个ContentObserver派生类实例，当给定的Uri发生改变时，回调该实例对象去处理，调用registerContentObserver()方法去注册内容观察者。
 <br>3、由于ContentObserver的生命周期不同步于Activity和Service等。因此，在不需要时，需要手动的调用unregisterContentObserver()注销内容观察者。

######效果图：

![DownloadManager.gif](http://upload-images.jianshu.io/upload_images/3066970-50027e80fbff6c4c.gif?imageMogr2/auto-orient/strip)
<br>一：执行下载
* 下载配置
```
downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
downloadObserver = new DownloadChangeObserver();
//在执行下载前注册内容监听者
registerContentObserver();
DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
/**设置用于下载时的网络状态*/
request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
/**设置通知栏是否可见*/
request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
/**设置漫游状态下是否可以下载*/
request.setAllowedOverRoaming(false);
/**如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
 我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true.*/
request.setVisibleInDownloadsUi(true);
/**设置文件保存路径*/
request.setDestinationInExternalFilesDir(getApplicationContext(), "phoenix", "phoenix.apk");
/**将下载请求放入队列， return下载任务的ID*/
downloadId = downloadManager.enqueue(request);
//执行下载任务时注册广播监听下载成功状态
registerBroadcast();
```
* 添加权限
```
<!--网络通信权限-->
<uses-permission android:name="android.permission.INTERNET"/>
<!--SD卡写入数据权限-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<!--SD卡创建与删除权限-->
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
<!--VISIBILITY_HIDDEN表示不显示任何通知栏提示的权限-->
<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"/>
<!--DownloadManager-->
<uses-permission android:name="android.permission.ACCESS_DOWNLOAD_MANAGER"/>
```
* 在清单文件中注册Service
```
<!--版本更新服务-->
<service android:name="com.github.phoenix.service.DownloadService"></service>
```

二：监听下载进度
* 注册ContentObserver
三个参数分别是所要监听的Uri、false表示精确匹配此Uri，true表示可以匹配其派生的Uri、ContentObserver的派生类实例。
```
/**
 * 注册ContentObserver
 */
private void registerContentObserver() {
	/** observer download change **/
	if (downloadObserver != null) {
		getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadObserver);
	}
}
```
* 查询已下载数据大小
为了提高性能，在这里开启定时任务，每2秒去查询数据大小并发送到handle中更新UI。
```
/**
 * 监听下载进度
 */
private class DownloadChangeObserver extends ContentObserver {

	public DownloadChangeObserver() {
		super(downLoadHandler);
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	}

	/**
	 * 当所监听的Uri发生改变时，就会回调此方法
	 *
	 * @param selfChange 此值意义不大, 一般情况下该回调值false
	 */
	@Override
	public void onChange(boolean selfChange) {
		scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 2, TimeUnit.SECONDS);
	}
}
/**
 * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
 *
 * @param downloadId
 * @return
 */
private int[] getBytesAndStatus(long downloadId) {
	int[] bytesAndStatus = new int[]{
			-1, -1, 0
	};
	DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
	Cursor cursor = null;
	try {
		cursor = downloadManager.query(query);
		if (cursor != null && cursor.moveToFirst()) {
			//已经下载文件大小
			bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			//下载文件的总大小
			bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			//下载状态
			bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
		}
	} finally {
		if (cursor != null) {
			cursor.close();
		}
	}
	return bytesAndStatus;
}
```
* Activity与Service通信
<br>既然我们要在Activity中实时更新下载进度，那么就需要Activity绑定Service建立通信。
在Service中提供一个接口实时回调进度值。用isBindService来标识Activity是否绑定过Service，在调用bindService(ServiceConnection conn)方法时，如果绑定成功会返回true，否则返回false，只有返回true时才可以进行解绑，否则报错。
```
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
```
三：广播监听下载成功
* 下载完成，自动安装，记录APK存储路径
在下载成功后把APK存储路径保存到SP中，同时关闭定时器，开启apk安装界面。
```
/**
 * 安装APK
 * @param context
 * @param apkPath 安装包的路径
 */
public static void installApk(Context context, Uri apkPath) {
	Intent intent = new Intent();
	intent.setAction(Intent.ACTION_VIEW);
    //此处因为上下文是Context，所以要加此Flag，不然会报错
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setDataAndType(apkPath, "application/vnd.android.package-archive");
	context.startActivity(intent);
}
```

四：善后处理
* 关闭定时器，线程
当收到下载完成的广播时立即停掉定时器，取消线程。

* 解绑Service，注销广播，注销ContentObserver
当Service解绑的时候，要把监听下载完成的广播和监听下载进度的ContentObserver注销。

* 删除APK
当应用安装成功后，再次启动就执行删除Apk操作。
```
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
```

五：具体应用
<br>首先上传当前应用版本号给服务器，让服务器检查是否可以进行版本更新；如果可以进行版本更新，则绑定Service，开始下载APK，下载完成直接弹出安装界面，同时记录APK存储路径；待下次启动时，检查删除APK。

说明：此源码中包含了[把Toast写成单列，自定义样式](http://www.jianshu.com/p/ba844f644adf)的代码
