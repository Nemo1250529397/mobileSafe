package nemo.com.mobilesafe.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by nemo on 16-7-4.
 */
public class TelephoneNumberQueryUtils {
    private static final String TAG = "TelephoneNumberQueryUtils";

    public static String queryLocation(Context context, String telephoneNumber) {
        String location = telephoneNumber;
        if(!TextUtils.isEmpty(telephoneNumber)) {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getAbsolutePath()+"/address.db",
                    null, SQLiteDatabase.OPEN_READONLY);
            if(db.isOpen()) {
                String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
                Cursor cursor = db.rawQuery(sql, new String[]{telephoneNumber.substring(0, 7)});
                if(cursor != null) {
                    while(cursor.moveToNext()) {
                        location = cursor.getString(0);
                    }
                    cursor.close();
                }

                if(location.equals(telephoneNumber)) {
                    String sql2 = "select location from data2 where area=?";
                    Cursor cursor2 = db.rawQuery(sql2, new String[]{
                            telephoneNumber.length()==10? telephoneNumber.substring(1,3):telephoneNumber.substring(1,4)});
                    if(cursor2 != null) {
                        while(cursor2.moveToNext()) {
                            location = cursor2.getString(0);
                            location = location.substring(0, location.length()-2);
                            break;
                        }
                        cursor.close();
                    }
                }
                db.close();
            }
        }
        return location;
    }

    public static String dividleQueryLocation(Context context, String telephoneNumber) {
        Log.d(TAG, "telephoneNumber: " + telephoneNumber);

        String regularExpression = "^1[3458][\\d]{9}$";
        if(telephoneNumber.matches(regularExpression)) {
            return queryLocation(context, telephoneNumber);
        }

        switch (telephoneNumber.length()) {
            case 3: {
                return "匪警电话";
            }
            case 5: {
                return "服务类号码!";
            }
            case 10:
            case 11:
            {
                // 座机号码 027-6126409 0713-6126409
                return queryLocation(context, telephoneNumber);
            }
        }

        return telephoneNumber;
    }
}
