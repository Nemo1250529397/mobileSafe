package nemo.com.mobilesafe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.domain.AppInfo;
import nemo.com.mobilesafe.engine.AppInfoProvider;

/**
 * Created by nemo on 16-7-24.
 */
public class AppManagerActivity extends Activity {
    private TextView tvAvailableRom;
    private TextView tvAvailableSD;

    private LinearLayout llLoading;
    private ListView lvAppInfos;

    private List<AppInfo> appInfoList;
    private List<AppInfo> userAppInfoList;
    private List<AppInfo> systemAppInfoList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmanager_activity);

        tvAvailableRom = (TextView) findViewById(R.id.tv_avail_rom);
        tvAvailableSD = (TextView) findViewById(R.id.tv_avail_sd);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        lvAppInfos = (ListView) findViewById(R.id.lv_app_infos);

        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long availableBlock = statFs.getAvailableBlocks();
        long blockSize = statFs.getBlockSize();
        long availableRom = availableBlock * blockSize;
        String romText = "可用内存：" + Formatter.formatFileSize(this, availableRom);
        tvAvailableRom.setText(romText);

        statFs.restat(Environment.getExternalStorageDirectory().getAbsolutePath());
        long availableBlock1 = statFs.getAvailableBlocks();
        long blockSize1 = statFs.getBlockSize();
        long availableSD = availableBlock1 * blockSize1;
        String sdText = "可用SD：" + Formatter.formatFileSize(this, availableSD);
        tvAvailableSD.setText(sdText);

        new Thread(){
            @Override
            public void run() {
                llLoading.setVisibility(View.VISIBLE);
                appInfoList = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userAppInfoList = new ArrayList<AppInfo>();
                systemAppInfoList = new ArrayList<AppInfo>();

                for(AppInfo appInfo : appInfoList) {
                    if(appInfo.isUserApp()) {
                        userAppInfoList.add(appInfo);
                    } else {
                        systemAppInfoList.add(appInfo);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lvAppInfos.setAdapter(new AppInfoAdapter());
                        llLoading.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }.start();
    }

    class AppInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appInfoList.size();
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
            AppInfo appInfo = null;

            View view = null;
            ViewHolder viewHolder = null;

            if(position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户软件：" + userAppInfoList.size() + "个");
                return tv;
            } else if(position == (userAppInfoList.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统软件：" + systemAppInfoList.size() + "个");
                return tv;
            } else if(position <= userAppInfoList.size()) {
                int newPosition = position -1;
                appInfo = userAppInfoList.get(newPosition);
            } else {
                int newPosition = position - 1 -userAppInfoList.size() -1;
                appInfo = systemAppInfoList.get(newPosition);
            }

            if(convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.list_item_appinfo, null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) view.findViewById(R.id.tv_app_name);
                viewHolder.tvLocation = (TextView) view.findViewById(R.id.tv_app_location);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
                viewHolder.ivStatus = (ImageView) view.findViewById(R.id.iv_status);
                view.setTag(viewHolder);
            }

            viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
            viewHolder.tvName.setText(appInfo.getName());
            if(appInfo.isInRom()) {
                viewHolder.tvLocation.setText("内部安装");
            } else {
                viewHolder.tvLocation.setText("外部安装");
            }

            return view;
        }

        class ViewHolder {
            TextView tvName;
            TextView tvLocation;
            ImageView ivIcon;
            ImageView ivStatus;
        }
    }


}
