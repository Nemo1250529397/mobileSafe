package nemo.com.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nemo on 16-7-17.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "blacknumber.db";

    public BlackNumberOpenHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE blacknumber(_id integer primary key autoincrement, number varchar(20), mode varchar(2))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
