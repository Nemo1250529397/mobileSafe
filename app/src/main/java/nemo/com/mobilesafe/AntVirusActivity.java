package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import nemo.com.mobilesafe.db.dao.AntivirusDao;

/**
 * Created by nemo on 16-9-8.
 */
public class AntVirusActivity extends Activity {
    protected static final int SCANNING = 0;
    protected static final int FINISH = 1;
    private ImageView iv_scan;
    private ProgressBar progressBar;
    private PackageManager pm;
    private TextView tv_scan_status;
    private LinearLayout ll_container;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING: {
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scan_status.setText("正在扫描： " + scanInfo.name);
                    TextView textView = new TextView(getApplicationContext());
                    if(scanInfo.isVirus) {
                        textView.setTextColor(Color.RED);
                        textView.setText("请注意这是一个病毒： " + scanInfo.name);
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("程序正常：" + scanInfo.name);
                    }
                    ll_container.addView(textView, 0);
                    break;
                }
                case FINISH: {
                    tv_scan_status.setText("程序扫描完毕！");
                    iv_scan.clearAnimation();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(rotateAnimation);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        scanVirus();
    }

    private void scanVirus() {
        pm = getPackageManager();
        tv_scan_status.setText("正在初始化病毒搜索引擎！");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageInfo> infos = pm.getInstalledPackages(0);
                SystemClock.sleep(500);
                progressBar.setMax(infos.size());
                int progress = 0;
                for(PackageInfo info : infos) {
                    String sourceDir = info.applicationInfo.sourceDir;
                    String md5 = getFileMd5(sourceDir);
                    ScanInfo scanInfo = new ScanInfo();
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packageName = info.packageName;
                    System.out.println(scanInfo.packageName + ": " + md5);
                    if(AntivirusDao.isVirus(md5)) {
                        scanInfo.isVirus = true;
                    } else {
                        scanInfo.isVirus = false;
                    }

                    Message msg = Message.obtain();
                    msg.obj = scanInfo;
                    msg.what = SCANNING;
                    handler.sendMessage(msg);
                    SystemClock.sleep(300);
                    progress++;
                    progressBar.setProgress(progress);
                }

                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private String getFileMd5(String path) {
        try {
            File file = new File(path);
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb = new StringBuffer();
            for(byte b : result) {
                int number = b&0xff;
                String str = Integer.toHexString(number);
                if(str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    class ScanInfo {
        String packageName;
        String name;
        boolean isVirus;
    }
}
