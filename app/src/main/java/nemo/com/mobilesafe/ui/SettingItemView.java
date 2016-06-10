package nemo.com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nemo.com.mobilesafe.R;

/**
 * Created by nemo on 16-6-11.
 */
public class SettingItemView extends RelativeLayout {

    private TextView tvTitle = null;
    private TextView tvDesc = null;
    private CheckBox cbStatus = null;

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.setting_item_view, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDescription(String description) {
        tvDesc.setText(description);
    }

    public void setStatus(boolean isChecked) {
        cbStatus.setChecked(isChecked);
    }

    public boolean checkStatus() {
        return cbStatus.isChecked();
    }
}
