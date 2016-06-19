package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by nemo on 16-6-15.
 */
public class Step03Activity extends BaseStepActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step03_activity);
    }

    @Override
    public Intent showNext() {
        return new Intent(Step03Activity.this, Step04Activity.class);
    }

    @Override
    public Intent showPrevious() {
        return new Intent(Step03Activity.this, Step02Activity.class);
    }
}
