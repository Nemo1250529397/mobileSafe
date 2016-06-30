package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nemo on 16-6-30.
 */
public class TelephoneNumberQueryActivity extends Activity {
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

//        InputStream is = null;
//        FileOutputStream fos = null;
//        try {
//            is = getAssets().open("address.db");
////            fos = openFileOutput("address.db", MODE_PRIVATE);
//            File file = new File(getFilesDir(), "address.db");
//            fos = new FileOutputStream(file);
//
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            while((len = is.read(buffer))!= -1) {
//                fos.write(buffer, 0, len);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if(fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if(is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void startQueryLocation(View view) {
        String telephoneNumber = etTelephoneNumber.getText().toString().trim();

        if(!TextUtils.isEmpty(telephoneNumber)) {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(getFilesDir().getAbsolutePath()+"/address.db",
                    null, SQLiteDatabase.OPEN_READONLY);
            if(db.isOpen()) {
                String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
                Cursor cursor = db.rawQuery(sql, new String[]{telephoneNumber.substring(0, 7)});
                if(cursor != null) {
                    while(cursor.moveToNext()) {
                        String location = cursor.getString(0);
                        tvNumberLocation.setText("手机号码归属地： " + location);
                    }
                    cursor.close();
                }
                db.close();
            }
        }
    }
}
