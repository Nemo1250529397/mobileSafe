package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * Created by nemo on 16-6-15.
 */
public class Step04Activity extends BaseStepActivity {
    private SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step04_activity);

        sp = getSharedPreferences("lostfundconfig", MODE_PRIVATE);
    }

    @Override
    public Intent showNext() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isSetup", true);
        editor.commit();
        return new Intent(Step04Activity.this, LostFundActivity.class);
    }

    @Override
    public Intent showPrevious() {
        return new Intent(Step04Activity.this, Step03Activity.class);
    }
}
