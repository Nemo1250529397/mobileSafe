package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * Created by nemo on 16-6-15.
 */
public class Step04Activity extends Activity {
    private SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step04_activity);

        sp = getSharedPreferences("lostfundconfig", MODE_PRIVATE);
    }

    public void previous(View view) {
        Intent intent = new Intent(Step04Activity.this, Step03Activity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    public void next(View view) {
        Intent intent = new Intent(Step04Activity.this, LostFundActivity.class);
        startActivity(intent);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isSetup", true);
        editor.commit();

        finish();
    }
}
