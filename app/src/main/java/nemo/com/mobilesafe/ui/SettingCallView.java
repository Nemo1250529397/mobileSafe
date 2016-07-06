package nemo.com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nemo.com.mobilesafe.R;

/**
 * Created by nemo on 16-7-5.
 */
public class SettingCallView extends RelativeLayout {
    private TextView tvTitle = null;
    private TextView tvDesc = null;

    private String title;
    private String desc_on;
    private String desc_off;

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.setting_call_view, this);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvDesc = (TextView) view.findViewById(R.id.tv_desc);

        tvTitle.setText(title);
    }

    public SettingCallView(Context context) {
        super(context);
        initView(context);
    }

    public SettingCallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "settings_title");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_off");

        initView(context);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDescription(String description) {
        tvDesc.setText(description);
    }

    public void setStatus(boolean isChecked) {
        if(isChecked) {
            tvDesc.setText(desc_on);
        } else {
            tvDesc.setText(desc_off);
        }
    }
}
