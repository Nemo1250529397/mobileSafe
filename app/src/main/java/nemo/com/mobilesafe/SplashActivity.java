package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nemo.com.mobilesafe.utils.StreamTool;

/**
 * Created by nemo on 16-6-2.
 */
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int SHOWUPDATEDIALOG = 0;
    private static final int ENTERHOME = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;

    private TextView tvSplashVersion = null;
    private ProgressBar pbUpdate = null;

    private String description = null;
    private String apkUrl = null;

    private SharedPreferences sp = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOWUPDATEDIALOG: {
                    showUpdateDialog();
                    break;
                }
                case ENTERHOME: {
                    enterHome();
                    finish();
                    break;
                }
                case URL_ERROR: {
                    Toast.makeText(SplashActivity.this, "URL ERROR!", Toast.LENGTH_SHORT).show();
                    enterHome();
                    finish();
                    break;
                }
                case NETWORK_ERROR: {
                    Toast.makeText(SplashActivity.this, "NETWORK ERROR!", Toast.LENGTH_SHORT).show();
                    enterHome();
                    finish();
                    break;
                }
                case JSON_ERROR: {
                    Toast.makeText(SplashActivity.this, "JSON ERROR!", Toast.LENGTH_SHORT).show();
                    enterHome();
                    finish();
                    break;
                }
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        AlphaAnimation aa = new AlphaAnimation(0.2f, 0.1f);
        aa.setDuration(500);
        findViewById(R.id.rl_splash).startAnimation(aa);

        tvSplashVersion = (TextView) findViewById(R.id.tv_splash_version);
        tvSplashVersion.setText("版本号：" + getVersionName());
        pbUpdate = (ProgressBar) findViewById(R.id.pb_updateprogress);

        sp = getSharedPreferences(SettingsActivity.SETTINGS, MODE_PRIVATE);
        installShortCut();

        copyDB("address.db");
        copyDB("antivirus.db");

        if(sp.getBoolean(SettingsActivity.AUTOUPDATE, false)) {
            requestUpate();
        } else {
            SystemClock.sleep(2000);
            enterHome();
            finish();
        }
    }

    private void copyDB(String fileName) {
        try {
            File file = new File(getFilesDir(), fileName);
            if(file.exists() && file.length()>0) {

            } else {
                InputStream is = getAssets().open(fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installShortCut() {
        boolean shortCut = sp.getBoolean("shortcut",false);

        if(shortCut) {
            return;
        } else {
            SharedPreferences.Editor editor = sp.edit();
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "我的手机卫士");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

            Intent shortCutIntent = new Intent();
            shortCutIntent.setAction("android.intent.action.MAIN");
            shortCutIntent.addCategory("android.intent.category.LAUNCHER");
            shortCutIntent.setClassName(getPackageName(), "nemo.com.mobilesafe.SplashActivity");

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
            sendBroadcast(intent);
            editor.putBoolean("shortcut", true);
            editor.commit();
        }

    }

    public void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(R.string.updatenotice);
        builder.setMessage(description);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.updatepositive, new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.i(TAG, "OnClick Positive Button");
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.i(TAG, "Begin download apk and update the app!");

                    pbUpdate.setVisibility(View.VISIBLE);

                    FinalHttp finalHttp = new FinalHttp();
                    finalHttp.download(apkUrl, Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/mobileSafe2.0.apk", new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            pbUpdate.setMax((int) count);
                            pbUpdate.setProgress((int) current);
                        }

                        @Override
                        public void onSuccess(File file) {
                            Toast.makeText(SplashActivity.this, "download successful!", Toast.LENGTH_SHORT).show();
                            installApk(file);
                            super.onSuccess(file);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            Toast.makeText(SplashActivity.this, "download fail!", Toast.LENGTH_SHORT).show();
                            super.onFailure(t, errorNo, strMsg);

                        }

                        private void installApk(File file) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    });
                }
                dialog.dismiss();
                dialog = null;
            }
        });
        builder.setNegativeButton(R.string.updatenegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog = null;
                enterHome();
                finish();
            }
        });

        builder.show();
    }

    private void requestUpate() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL url = null;
                HttpURLConnection conn = null;
                Message mes = handler.obtainMessage();
                long startTime = System.currentTimeMillis();

                try {
                    url = new URL(getString(R.string.updateurl));
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int returnCode = conn.getResponseCode();

                    if(200 == returnCode) {
                        Log.i(TAG, "connect to web successful!");
                        InputStream is = conn.getInputStream();
                        String result = StreamTool.getStringFormStream(is);

                        JSONObject jsonObject = new JSONObject(result);
                        String version = jsonObject.getString("version");
                        description = jsonObject.getString("description");
                        apkUrl = jsonObject.getString("apkurl");

                        Log.i(TAG, "version: " + version +
                                ", description: " + description +
                                ", apkurl: " + apkUrl);

                        if(getVersionName().equals(version)) {
                            mes.what = ENTERHOME;
                        } else {
                            mes.what = SHOWUPDATEDIALOG;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mes.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    mes.what = NETWORK_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mes.what = JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    long durtion = endTime - startTime;

                    if(durtion < 2000) {
                        SystemClock.sleep(2000-durtion);
                    }

                    mes.sendToTarget();
                }
                return null;
            }
        }.execute();
    }

    private String getVersionName() {
        PackageManager pm = getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
