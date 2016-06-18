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

/**
 * Created by nemo on 16-6-5.
 */
public class LostFundActivity extends Activity {

    public void onCreate(Bundle savedInstatnceSate) {
        super.onCreate(savedInstatnceSate);
        setContentView(R.layout.lostfund_activity);

    }

}
