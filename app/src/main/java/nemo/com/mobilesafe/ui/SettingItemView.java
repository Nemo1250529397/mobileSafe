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

    private String title;
    private String desc_on;
    private String desc_off;

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.setting_item_view, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);

        tvTitle.setText(title);
        if(cbStatus.isChecked()) {
            tvDesc.setText(desc_on);
        } else {
            tvDesc.setText(desc_off);
        }
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "settings_title");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_off");

        title = context.getString(Integer.parseInt(title.substring(1)));
        desc_on = context.getString(Integer.parseInt(desc_on.substring(1)));
        desc_off = context.getString(Integer.parseInt(desc_off.substring(1)));

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

        if(isChecked) {
            tvDesc.setText(desc_on);
        } else {
            tvDesc.setText(desc_off);
        }
    }

    public boolean checkStatus() {
        return cbStatus.isChecked();
    }
}
