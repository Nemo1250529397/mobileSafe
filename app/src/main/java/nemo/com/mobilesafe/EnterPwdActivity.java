package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by nemo on 16-8-29.
 */
public class EnterPwdActivity extends Activity {
    private EditText et_password;
    private String packagename;
    private TextView tv_name;
    private ImageView iv_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);

        packagename = getIntent().getStringExtra("packagename");
        et_password = (EditText) findViewById(R.id.et_password);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);

        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
            tv_name.setText(applicationInfo.loadLabel(pm));
            iv_icon.setImageDrawable(applicationInfo.loadLogo(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void click(View view) {
        String pwd = et_password.getText().toString().trim();
        if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(getApplicationContext(), "输入密码为空，请重新输入！", Toast.LENGTH_SHORT).show();
            return;
        }
        if("nemo".equals(pwd)) {
            Intent intent = new Intent();
            intent.setAction("nemo.com.mobilesafe.service.tempstop");
            intent.putExtra("packagename", packagename);
            sendBroadcast(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "输入密码不正确！", Toast.LENGTH_SHORT).show();
        }
    }
}
