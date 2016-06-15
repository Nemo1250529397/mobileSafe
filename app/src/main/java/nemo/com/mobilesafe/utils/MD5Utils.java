package nemo.com.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nemo on 16-6-15.
 */
public class MD5Utils {
    public static final String encodeWithMD5(final String src) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte []buffer = digest.digest(src.getBytes());
            StringBuffer sb = new StringBuffer();

            for(byte b : buffer) {
                int tmp = b & 0xff;
                String sTmp = Integer.toHexString(tmp);

                if(sTmp.length() == 1) {
                    sb.append("0");
                }

                sb.append(sTmp);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
