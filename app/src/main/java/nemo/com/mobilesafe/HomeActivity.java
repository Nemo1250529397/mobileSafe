package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import nemo.com.mobilesafe.utils.MD5Utils;

import static nemo.com.mobilesafe.R.layout.list_item_home;

/**
 * Created by nemo on 16-6-5.
 */
public class HomeActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gvModule = null;
    private SharedPreferences sp = null;

    //mobile phone safe
    private EditText etSetupPwd = null;
    private EditText etConfirmPwd = null;
    private Button btnOk = null;
    private Button btnCancel = null;
    private AlertDialog alertDialog = null;

    private String[] moduleName = {
         "手机防盗", "通讯卫士", "软件管理",
         "进程管理", "流量统计", "手机杀毒",
         "缓存清理", "高级工具", "设置中心"
    };

    private int[] moduleID = {
        R.drawable.safe,        R.drawable.callmsgsafe, R.drawable.app,
        R.drawable.taskmanager, R.drawable.netmanager,  R.drawable.trojan,
        R.drawable.sysoptimize, R.drawable.atools,      R.drawable.settings
    };

    public void onCreate(Bundle savedInstatnceSate) {
        super.onCreate(savedInstatnceSate);
        setContentView(R.layout.home_activity);

        gvModule = (GridView) findViewById(R.id.gv_module);
        gvModule.setAdapter(new MyAdapterList());
        gvModule.setOnItemClickListener(this);

        sp = getSharedPreferences("home", MODE_PRIVATE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                if (checkSetupPwd()) {
                    showConfirmPwdDialog();
                } else {
                    showSetupPwdDialog();
                }

                break;
            }

            case 8: {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            }

            default:
                break;
        }
    }

    private void showConfirmPwdDialog() {
        View view = View.inflate(HomeActivity.this, R.layout.home_confirm_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setView(view);

        etConfirmPwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
        btnOk = (Button) view.findViewById(R.id.confirmok);
        btnCancel = (Button) view.findViewById(R.id.cancel);
        alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void showSetupPwdDialog() {
        View view = View.inflate(HomeActivity.this, R.layout.home_setup_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setView(view);

        etSetupPwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        etConfirmPwd = (EditText) view.findViewById(R.id.et_setup_confirm);
        btnOk = (Button) view.findViewById(R.id.setupok);
        btnCancel = (Button) view.findViewById(R.id.cancel);
        alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private boolean checkSetupPwd() {

        return !TextUtils.isEmpty(sp.getString("mobilePassword", null));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.confirmok: {
                String confirmPwd = etConfirmPwd.getText().toString().trim();

                if(TextUtils.isEmpty(confirmPwd)) {
                    Toast.makeText(HomeActivity.this, "密码输入为空！", Toast.LENGTH_SHORT).show();
                }

                String storePwd = sp.getString("mobilePassword", "");

                if(storePwd.equals(MD5Utils.encodeWithMD5(confirmPwd))) {
                    Toast.makeText(HomeActivity.this, "密码输入正确！", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, "密码输入错误！", Toast.LENGTH_SHORT).show();
                    etConfirmPwd.setText("");
                }
                break;
            }

            case R.id.setupok: {
                String setupPwd = etSetupPwd.getText().toString().trim();
                String confirmPwd = etConfirmPwd.getText().toString().trim();

                if(TextUtils.isEmpty(setupPwd) || TextUtils.isEmpty(confirmPwd)) {
                    Toast.makeText(HomeActivity.this, "密码输入为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(setupPwd.equals(confirmPwd)) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("mobilePassword", MD5Utils.encodeWithMD5(setupPwd));
                    editor.commit();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, "密码输入不匹配！", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.cancel: {
                alertDialog.dismiss();
                break;
            }
        }
    }

    private class MyAdapterList extends BaseAdapter {

        @Override
        public int getCount() {
            return moduleName.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if(convertView != null) {
                view = convertView;
            } else {
                view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.list_item_home, null);
            }
            ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
            ivItem.setImageResource(moduleID[position]);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_item);
            tvItem.setText(moduleName[position]);

            return view;
        }
    }
}
