package nemo.com.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by nemo on 16-9-9.
 */
public class AntivirusDao {
    public static boolean isVirus(String md5) {
        String path = "/data/data/nemo.com.mobilesafe/files/antivirus.db";
        boolean result = false;

        SQLiteDatabase dataBase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = dataBase.rawQuery("SELECT * FROM datable WHERE md5=?", new String[]{md5});
        if(cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        dataBase.close();
        return result;
    }
}
