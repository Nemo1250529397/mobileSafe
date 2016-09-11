package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.db.dao.AppLockDao;
import nemo.com.mobilesafe.domain.AppInfo;
import nemo.com.mobilesafe.engine.AppInfoProvider;
import nemo.com.mobilesafe.utils.DensityUtil;

/**
 * Created by nemo on 16-7-24.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AppManagerActivity";
    private TextView tvAvailableRom;
    private TextView tvAvailableSD;

    private LinearLayout llLoading;
    private ListView lvAppInfos;

    private List<AppInfo> appInfoList;
    private List<AppInfo> userAppInfoList;
    private List<AppInfo> systemAppInfoList;

    private TextView tvStatus;
    private PopupWindow popupWindow;
    private LinearLayout llStart;
    private LinearLayout llShare;
    private LinearLayout llUninstall;
    private AppInfo appInfo;
    private AppInfoAdapter adapter;

    private AppLockDao appLockDao;

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

        tvStatus = (TextView) findViewById(R.id.tv_status);

        adapter = new AppInfoAdapter();

        appLockDao = new AppLockDao(this);

        fillData();

        lvAppInfos.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                if (userAppInfoList != null && systemAppInfoList != null) {
                    if (firstVisibleItem > userAppInfoList.size()) {
                        tvStatus.setText("系统软件：" + systemAppInfoList.size() + "个");
                    } else {
                        tvStatus.setText("用户软件：" + userAppInfoList.size() + "个");
                    }
                }
            }
        });

        lvAppInfos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                } else if (position == userAppInfoList.size() + 1) {
                    return;
                } else if (position <= userAppInfoList.size()) {
                    int newPosition = position - 1;
                    appInfo = userAppInfoList.get(newPosition);
                } else {
                    int newPosition = position - userAppInfoList.size() - 2;
                    appInfo = systemAppInfoList.get(newPosition);
                }

                dismissPopupWindow();

                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
                llStart = (LinearLayout) contentView.findViewById(R.id.ll_start);
                llShare = (LinearLayout) contentView.findViewById(R.id.ll_share);
                llUninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);

                llStart.setOnClickListener(AppManagerActivity.this);
                llShare.setOnClickListener(AppManagerActivity.this);
                llUninstall.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentView, -2, -2);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);

                int dip = 60;
                int px = DensityUtil.dip2px(getApplicationContext(), dip);
                popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, px, location[1]);

                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                sa.setDuration(300);
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(300);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                contentView.setAnimation(set);

            }
        });

        lvAppInfos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    return true;
                } else if (position == (userAppInfoList.size() + 1)) {
                    return true;
                } else if(position <= userAppInfoList.size()) {
                    int newPosition = position - 1;
                    appInfo = userAppInfoList.get(newPosition);
                } else {
                    int newPosition = position - userAppInfoList.size() - 2;
                    appInfo = userAppInfoList.get(newPosition);
                }

                AppInfoAdapter.ViewHolder holder = (AppInfoAdapter.ViewHolder) view.getTag();
                if(appLockDao.find(appInfo.getPackageName())) {
                    appLockDao.delete(appInfo.getPackageName());
                    holder.ivStatus.setImageResource(R.drawable.unlock);
                } else {
                    appLockDao.add(appInfo.getPackageName());
                    holder.ivStatus.setImageResource(R.drawable.lock);
                }
                return true;
            }
        });
    }

    private void fillData() {
        llLoading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
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
                        lvAppInfos.setAdapter(adapter);
                        llLoading.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }.start();
    }

    private void dismissPopupWindow() {
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopupWindow();
    }

    @Override
    public void onClick(View v) {
        dismissPopupWindow();
        switch (v.getId()) {
            case R.id.ll_share: {
                Log.i(TAG, "程序分享：" + appInfo.getName());
                shareApplication();
                break;
            }

            case R.id.ll_start: {
                Log.i(TAG, "启动程序：" + appInfo.getName());
                startApplication();
                break;
            }

            case R.id.ll_uninstall: {
                if(appInfo.isUserApp()) {
                    Log.i(TAG, "卸载程序：" + appInfo.getName());
                    uninstallApplication();
                } else {
                    Toast.makeText(this, "系统程序， 不能卸载！", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    private void uninstallApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillData();
    }

    private void startApplication() {
        PackageManager pm = getPackageManager();

//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
//        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);

        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
        if(intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "启动应用程序：" + appInfo.getName() + "失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "分享应用程序：" + appInfo.getName());
        startActivity(intent);
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
            if(appLockDao.find(appInfo.getPackageName())) {
                viewHolder.ivStatus.setImageResource(R.drawable.lock);
            } else {
                viewHolder.ivStatus.setImageResource(R.drawable.unlock);
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
