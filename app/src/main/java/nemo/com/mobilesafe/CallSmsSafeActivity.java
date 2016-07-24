package nemo.com.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nemo.com.mobilesafe.db.dao.BlackNumberDao;
import nemo.com.mobilesafe.domain.BlackNumberInfo;

/**
 * Created by nemo on 16-7-17.
 */
public class CallSmsSafeActivity extends Activity {
    private static final String TAG = "CallSmsSafeActivity";
    private ListView lvCallSmsSafe = null;
    private BlackNumberDao blackNumberDao = null;
    private List<BlackNumberInfo> list = null;
    private MyAdapter myAdapter = null;

    private int maxSize = 10;
    private int offset = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callsmssafe_activity);
        blackNumberDao = new BlackNumberDao(this);
//        list = blackNumberDao.findAll();

        list = blackNumberDao.findPart(maxSize, offset);
        lvCallSmsSafe = (ListView) findViewById(R.id.lv_callsms_safe);

        myAdapter = new MyAdapter();
        lvCallSmsSafe.setAdapter(myAdapter);
        lvCallSmsSafe.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: {
                        Log.i(TAG, "I am the state of SCROLL_STATE_FLING");
                        break;
                    }

                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: {
                        Log.i(TAG, "I am the state of SCROLL_STATE_IDLE");

                        int lastPoaition = lvCallSmsSafe.getLastVisiblePosition();
                        if(lastPoaition == (list.size() - 1)) {

                            offset += maxSize;
                            list = blackNumberDao.findPart(maxSize, offset);
                            myAdapter.notifyDataSetChanged();
                            
                        }
                        break;
                    }

                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {
                        Log.i(TAG, "I am the state of SCROLL_STATE_TOUCH_SCROLL");
                        break;
                    }

                    default: {
                        break;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private EditText etBlackNumber = null;
    private CheckBox cbPhone = null;
    private CheckBox cbSms = null;
    private Button btOk = null;
    private Button btCancel = null;

    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        dialog.setView(contentView);
        dialog.show();
        etBlackNumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
        cbPhone = (CheckBox) contentView.findViewById(R.id.cb_phone);
        cbSms = (CheckBox) contentView.findViewById(R.id.cb_sms);
        btOk = (Button) contentView.findViewById(R.id.ok);
        btCancel = (Button) contentView.findViewById(R.id.cancel);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNumber = etBlackNumber.getText().toString();
                if (TextUtils.isEmpty(blackNumber)) {
                    Toast.makeText(getApplicationContext(), "拦截号码为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode;
                if (cbPhone.isChecked() && cbSms.isChecked()) {
                    mode = "3";
                } else if (cbSms.isChecked()) {
                    mode = "1";
                } else if (cbPhone.isChecked()) {
                    mode = "2";
                } else {
                    Toast.makeText(getApplicationContext(), "未选择拦截模式！", Toast.LENGTH_SHORT).show();
                    return;
                }

                blackNumberDao.add(blackNumber, mode);

                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(blackNumber);
                info.setMode(mode);
                list.add(0, info);

                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder viewHolder = null;
            if(convertView == null) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item_callsms, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
                viewHolder.tv_mode = (TextView) view.findViewById(R.id.tv_block_mode);
                viewHolder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tv_number.setText(list.get(position).getNumber());
            String mode = list.get(position).getMode();
            if("1".equals(mode)) {
                viewHolder.tv_mode.setText("短信拦截");
            } else if("2".equals(mode)) {
                viewHolder.tv_mode.setText("电话拦截");
            } else {
                viewHolder.tv_mode.setText("全部拦截");
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定删除拦截号码！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            blackNumberDao.delete(list.get(position).getNumber());
                            list.remove(position);
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }
}
