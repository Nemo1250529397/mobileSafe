package nemo.com.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.db.AppLockDBOpenHelper;

/**
 * Created by nemo on 16-8-28.
 */
public class AppLockDao {
    private AppLockDBOpenHelper helper;
    private Context context;

    public AppLockDao(Context context) {
        this.context = context;
        helper = new AppLockDBOpenHelper(context);
    }

    public void add(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert(AppLockDBOpenHelper.SQL_TABLE, null, values);
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.itheima.mobilesafe.applockchange");
        context.sendBroadcast(intent);
    }

    public void delete(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(AppLockDBOpenHelper.SQL_TABLE, "packagename=?", new String[]{packageName});
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.itheima.mobilesafe.applockchange");
        context.sendBroadcast(intent);
    }

    public boolean find(String packageName) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppLockDBOpenHelper.SQL_TABLE, null, "packagename=?", new String[]{packageName}, null, null, null);
        if(cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<String> findAll() {
        List<String> protectPackageNames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppLockDBOpenHelper.SQL_TABLE, new String[]{"packagename"}, null, null, null, null, null);
        while(cursor.moveToNext()) {
            protectPackageNames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return protectPackageNames;
    }
}
