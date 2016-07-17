package nemo.com.mobilesafe.test;

import android.test.AndroidTestCase;

import java.util.List;
import java.util.Random;

import nemo.com.mobilesafe.db.BlackNumberOpenHelper;
import nemo.com.mobilesafe.db.dao.BlackNumberDao;
import nemo.com.mobilesafe.domain.BlackNumberInfo;

/**
 * Created by nemo on 16-7-17.
 */
public class BlackNumberTest extends AndroidTestCase {
    public void testCreateDB() throws Exception{
        BlackNumberOpenHelper helper = new BlackNumberOpenHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        long basenumber = 13500000000l;
        Random random = new Random();
        for(int i = 0; i < 100; i++) {
            dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
        }
    }

    public void testFindAll() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        List<BlackNumberInfo> infoList = dao.findAll();
        for(BlackNumberInfo info:infoList) {
            System.out.println(info.toString());
        }
    }

    public void testDelete() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.delete("110");
    }

    public void testUpdate() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.update("110", "2");
    }

    public void testFind() {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.add("110", "1");
        boolean result = dao.find("110");
        assertEquals(true, result);
    }
}
