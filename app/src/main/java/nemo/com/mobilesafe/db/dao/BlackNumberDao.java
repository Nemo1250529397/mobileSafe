package nemo.com.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import nemo.com.mobilesafe.db.BlackNumberOpenHelper;
import nemo.com.mobilesafe.domain.BlackNumberInfo;

/**
 * Created by nemo on 16-7-17.
 */
public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;

    public BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    public boolean find(String number) {
        boolean result = false;
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM blacknumber WHERE number=?", new String[]{number});
        if(cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public String findMode(String number) {
        String result = null;
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT mode FROM blacknumber WHERE number=?", new String[]{number});
        if(cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<BlackNumberInfo> findAll() {
        List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT number, mode FROM blacknumber order by _id desc", null);
        while(cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<BlackNumberInfo> findPart(int maxSize, int offset) {
        List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT number, mode FROM blacknumber order by _id desc", null);

        Cursor cursor = db.rawQuery("SELECT number, mode FROM blacknumber order by _id desc LIMIT ? OFFSET ?",
                new String[]{String.valueOf(maxSize), String.valueOf(offset)});
        while(cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void add(String number, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("mode", mode);
        db.insert("blacknumber", null, contentValues);
        db.close();
    }

    public void update(String number, String newmode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", newmode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }

    public void delete(String number) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }
}
