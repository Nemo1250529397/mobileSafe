package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import nemo.com.mobilesafe.utils.SmsUtils;

/**
 * Created by nemo on 16-6-30.
 */
public class AToolsActivity extends Activity {
    private ProgressDialog progressDialog = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atools_activity);


    }

    public void startNumberLocationQuery(View view) {
        Intent intent = new Intent(AToolsActivity.this, TelephoneNumberQueryActivity.class);
        startActivity(intent);
    }

    public void startSmssBackup(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在备份短信！");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtils.backupSms(getApplicationContext(), new SmsUtils.ProgressCallBack() {

                        @Override
                        public void setMaxProcess(int max) {
                            progressDialog.setMax(max);
                        }

                        @Override
                        public void setCurrentProcess(int currentProcess) {
                            progressDialog.setProgress(currentProcess);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "备份短信成功！", Toast.LENGTH_SHORT).show();
                        }
                    });

                    progressDialog.dismiss();
                    progressDialog = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startRestoreSmss(View view) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SmsUtils.restoreSmss(getApplicationContext(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }
}
