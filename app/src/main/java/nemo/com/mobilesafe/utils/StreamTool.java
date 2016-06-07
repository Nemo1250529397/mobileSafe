package nemo.com.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nemo on 16-6-3.
 */
public class StreamTool {
    public static String getStringFormStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int len = -1;

        try {
            while((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }
}
