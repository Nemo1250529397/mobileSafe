package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import static nemo.com.mobilesafe.R.layout.list_item_home;

/**
 * Created by nemo on 16-6-5.
 */
public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gvModule = null;

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

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 8:
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
                break;
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
