package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nemo.com.mobilesafe.utils.TelephoneNumberQueryUtils;

/**
 * Created by nemo on 16-6-30.
 */
public class TelephoneNumberQueryActivity extends Activity implements TextWatcher {
    private EditText etTelephoneNumber = null;
    private TextView tvNumberLocation = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telephonenumberquery_activity);

        etTelephoneNumber = (EditText) findViewById(R.id.et_telephonenumber);
        tvNumberLocation = (TextView) findViewById(R.id.tv_location);

        File file = new File(getFilesDir(), "address.db");
        if(file.exists() && file.length() > 0) {

        } else {
            try {
                InputStream is = getAssets().open("address.db");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        etTelephoneNumber.addTextChangedListener(this);
    }

    public void startQueryLocation(View view) {
        String telephoneNumber = etTelephoneNumber.getText().toString().trim();
        String location = TelephoneNumberQueryUtils.dividleQueryLocation(this, telephoneNumber);
        tvNumberLocation.setText("手机号码归属地： " + location);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(s)) {
            String telephoneNumber = etTelephoneNumber.getText().toString().trim();
            String location = TelephoneNumberQueryUtils.dividleQueryLocation(this, telephoneNumber);
            tvNumberLocation.setText("手机号码归属地： " + location);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
