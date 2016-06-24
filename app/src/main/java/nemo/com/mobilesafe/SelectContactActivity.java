package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nemo on 16-6-23.
 */
public class SelectContactActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "SelectContactActivity";
    private ListView lvContact = null;
    private ArrayList<HashMap<String, String>> dataContacts;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcontact_activity);

        dataContacts = new ArrayList<HashMap<String, String>>();
        getContacts();

        lvContact = (ListView) findViewById(R.id.lv_contacts);
        lvContact.setAdapter(new SimpleAdapter(this, dataContacts, R.layout.contact_item_view,
                new String[]{"Name", "telephoneNumber"},
                new int[]{R.id.tv_name, R.id.tv_telephonenumber}));
        lvContact.setOnItemClickListener(this);

        Log.d(TAG, String.valueOf(dataContacts.size()));
    }

    private void getContacts() {
        HashMap<String, String> map = null;

        ContentResolver contentResolver = getContentResolver();
        Uri uriRawContacts = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");

        Cursor cursorRawContacts = contentResolver.query(uriRawContacts, new String[]{"contact_id"}, null, null, null);

        Log.d(TAG, "cursorRawContacts: " + String.valueOf(cursorRawContacts.getCount()));

        while(cursorRawContacts.moveToNext()) {
            String rawContactsId = cursorRawContacts.getString(0);
            if(!TextUtils.isEmpty(rawContactsId)) {
                Cursor cursorData = contentResolver.query(uriData, new String[]{"mimetype", "data1"}, "contact_id=?",
                        new String[]{rawContactsId}, null);

                map = new HashMap<String, String>();

                while(cursorData.moveToNext()) {
                    String mimeType = cursorData.getString(0);
                    String data = cursorData.getString(1);

                    Log.d(TAG, "data1=="+data+"==mimetype=="+mimeType);

                    if("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
                        map.put("telephoneNumber", data);
                    } else if("vnd.android.cursor.item/name".equals(mimeType)) {
                        map.put("Name", data);
                    }
                }
                dataContacts.add(map);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("Name", dataContacts.get(position).get("Name"));
        intent.putExtra("telephoneNumber", dataContacts.get(position).get("telephoneNumber"));
        setResult(100, intent);
        finish();
    }
}
