package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nemo.com.mobilesafe.nemo.com.mobilesafe.utils.StreamTool;

/**
 * Created by nemo on 16-6-2.
 */
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private TextView tvSplashVersion = null;
    private Button btnCheckUpdate = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        tvSplashVersion = (TextView) findViewById(R.id.tv_splash_version);
        tvSplashVersion.setText("版本号：" + getVersionName());
        btnCheckUpdate = (Button) findViewById(R.id.btn_checkupdate);
        btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUpate();
            }
        });
    }

    private void requestUpate() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL url = null;
                HttpURLConnection conn = null;

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

                        Log.i(TAG, "The result is: " + result);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
}
