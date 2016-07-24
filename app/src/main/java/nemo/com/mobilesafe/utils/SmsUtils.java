package nemo.com.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by nemo on 16-7-20.
 */
public class SmsUtils {

    public interface ProgressCallBack {
        void setMaxProcess(int max);
        void setCurrentProcess(int currentProcess);
    }

    public static void backupSms(Context context, ProgressCallBack processCallBack) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), "sms_backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "utf-8");

        serializer.startDocument("utf-8", true);

        serializer.startTag(null, "smss");

        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"address", "type", "date", "body"}, null, null, null);
        int count = cursor.getCount();
        processCallBack.setMaxProcess(count);
        serializer.attribute(null, "max", ""+count);
        int current = 0;
        while(cursor.moveToNext()) {
            processCallBack.setCurrentProcess(current++);
            String address = cursor.getString(0);
            String type = cursor.getString(1);
            String date = cursor.getString(2);
            String body = cursor.getString(3);

            serializer.startTag(null, "sms");
            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");
            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");
            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");
            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");
            serializer.endTag(null, "sms");
        }
        processCallBack.setCurrentProcess(current);
        serializer.endTag(null, "smss");
        serializer.endDocument();
        cursor.close();
        fos.close();
    }

    public static void restoreSmss(Context context, boolean flags) throws Exception{
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = context.getContentResolver();
        if(flags) {
            resolver.delete(uri, null, null);
        }

        File file = new File(Environment.getExternalStorageDirectory(), "sms_backup.xml");
        FileInputStream fis = new FileInputStream(file);

        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(fis, "utf-8");

        int max = Integer.parseInt(pullParser.getAttributeValue(null, "max"));
        String address = null;
        String type = null;
        String date = null;
        String body = null;

        int eventType = pullParser.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT: {

                    break;
                }
                case XmlPullParser.START_TAG: {
                    String startName = pullParser.getName();
                    if("address".equals(startName)) {
                        address = pullParser.nextText();
                    } else if("type".equals(startName)) {
                        type = pullParser.nextText();
                    } else if("date".equals(startName)) {
                        date = pullParser.nextText();
                    } else if("body".equals(startName)) {
                        body = pullParser.nextText();
                    }
                    break;
                }
                case XmlPullParser.END_TAG: {
                    String endName = pullParser.getName();
                    if("sms".equals(endName)) {
                        ContentValues values = new ContentValues();
                        values.put("address", address);
                        values.put("type", type);
                        values.put("date", date);
                        values.put("body", body);
                        resolver.insert(uri, values);
                    }
                    break;
                }

            }
            eventType = pullParser.next();
        }
        fis.close();
    }
}
