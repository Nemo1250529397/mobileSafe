package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.domain.TaskInfo;
import nemo.com.mobilesafe.engine.TaskInfoProvider;
import nemo.com.mobilesafe.utils.SystemInfoUtils;

import static nemo.com.mobilesafe.utils.SystemInfoUtils.getAvailableMemory;

/**
 * Created by nemo on 16-8-14.
 */
public class TaskManagerActivity extends Activity {
    private TextView tvProcessCount = null;
    private TextView tvMemInfo = null;
    private List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = null;
    private LinearLayout llLoading = null;
    private ListView lvTaskManager = null;
    private TextView tvStatus;

    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private MyAdapter myAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskmanager_ativity);

        tvProcessCount = (TextView) findViewById(R.id.tv_process_count);
        tvMemInfo = (TextView) findViewById(R.id.tv_mem_info);

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        appProcessInfoList = am.getRunningAppProcesses();
        tvProcessCount.setText("运行中的进程：" + String.valueOf(appProcessInfoList.size()));

        tvMemInfo.setText("剩余内存/总内存：" + android.text.format.Formatter.formatFileSize(this, getAvailableMemory(this))
                + "/" + android.text.format.Formatter.formatFileSize(this, SystemInfoUtils.getTotalMemory()));

        tvStatus = (TextView) findViewById(R.id.tv_status);


        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        lvTaskManager = (ListView) findViewById(R.id.lv_task_manager);
        findData();

        lvTaskManager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && systemTaskInfos != null) {
                    if (firstVisibleItem > userTaskInfos.size()) {
                        tvStatus.setText("系统进程：" + systemTaskInfos.size() + "个");
                    } else {
                        tvStatus.setText("用户进程：" + userTaskInfos.size() + "个");
                    }
                }
            }
        });

        lvTaskManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if(position == 0) {
                    return;
                } else if(position == (userTaskInfos.size()) + 1) {
                    return;
                } else if(position <= userTaskInfos.size()) {
                    taskInfo = userTaskInfos.get(position -1);
                } else {
                    taskInfo = systemTaskInfos.get(position -
                    userTaskInfos.size() - 2);
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                if(taskInfo.isChecked()) {
                    taskInfo.setChecked(false);
                    holder.cbStatus.setChecked(false);
                } else {
                    taskInfo.setChecked(true);
                    holder.cbStatus.setChecked(true);
                }
            }
        });
    }

    public void findData() {
        llLoading.setVisibility(View.VISIBLE);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                        userTaskInfos = new ArrayList<TaskInfo>();
                        systemTaskInfos = new ArrayList<TaskInfo>();

                        for(TaskInfo info : allTaskInfos) {
                            if(info.isUserTask()) {
                                userTaskInfos.add(info);
                            } else {
                                systemTaskInfos.add(info);
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                llLoading.setVisibility(View.INVISIBLE);
                                if(myAdapter == null) {
                                    myAdapter = new MyAdapter();
                                    lvTaskManager.setAdapter(myAdapter);
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }
                                setTitle();
                            }
                        });
                    }
                }
        ).start();
    }

    public void setTitle() {
        tvProcessCount.setText("运行中的进程：" +
                SystemInfoUtils.getRunningProcessCount(getApplicationContext()));
        tvMemInfo.setText("剩余内存/总内存：" +
                Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailableMemory(getApplicationContext())) +
                "/" + Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getTotalMemory()));
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            if(sp.getBoolean("showsystem", false)) {
                return userTaskInfos.size() + systemTaskInfos.size() + 2;
            } else {
                return userTaskInfos.size() + 1;
            }
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
            TaskInfo taskInfo;
            if(position ==0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户进程：" + userTaskInfos.size());
                return tv;
            } else if(position == (userTaskInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程：" + systemTaskInfos.size());
                return tv;
            } else if(position <= userTaskInfos.size()) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - userTaskInfos.size() -2);
            }

            View view;
            ViewHolder holder;
            if(convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tvMemSize = (TextView) view.findViewById(R.id.tv_task_memsize);
                holder.cbStatus = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }

            holder.imageView.setImageDrawable(taskInfo.getIcon());
            holder.tvName.setText(taskInfo.getName());
            holder.tvMemSize.setText("占用内存大小：" + Formatter.formatFileSize(getApplicationContext(),
                    taskInfo.getMemorySize()));
            holder.cbStatus.setChecked(taskInfo.isChecked());

            if(getPackageName().equals(taskInfo.getPackageName())) {
                holder.cbStatus.setVisibility(View.INVISIBLE);
            } else {
                holder.cbStatus.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView tvName;
        TextView tvMemSize;
        CheckBox cbStatus;
    }

    public void onSelectAll(View view) {
        for(TaskInfo info : allTaskInfos) {
            if(getPackageName().equals(info.getPackageName())) {
                continue;
            }
            info.setChecked(true);
        }
        myAdapter.notifyDataSetChanged();
    }

    public void onSelectOppo(View view) {
        for(TaskInfo info : allTaskInfos) {
            if(getPackageName().equals(info.getPackageName())) {
                continue;
            }
            info.setChecked(!info.isChecked());
        }
        myAdapter.notifyDataSetChanged();
    }

    public void onKillAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;

        List<TaskInfo> killedTaskInfo = new ArrayList<TaskInfo>();
        for(TaskInfo info : allTaskInfos) {
            if(info.isChecked()) {
                am.killBackgroundProcesses(info.getPackageName());
                if(info.isUserTask()) {
                    userTaskInfos.remove(info);
                } else {
                    systemTaskInfos.remove(info);
                }
                killedTaskInfo.add(info);
                count++;
                savedMem += info.getMemorySize();
            }
        }
        allTaskInfos.removeAll(killedTaskInfo);
        myAdapter.notifyDataSetChanged();
        Toast.makeText(this, "清理进程个数：" + count + ", 清理内存大小：" +
        Formatter.formatFileSize(this, savedMem), Toast.LENGTH_SHORT).show();

        setTitle();
    }

    public void onEnterSetting(View view) {
        Intent intent = new Intent(this, TaskSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        myAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
